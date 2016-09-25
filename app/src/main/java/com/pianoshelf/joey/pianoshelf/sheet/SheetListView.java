package com.pianoshelf.joey.pianoshelf.sheet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.pianoshelf.joey.pianoshelf.BaseActivity;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.VolleySingleton;
import com.pianoshelf.joey.pianoshelf.composition.ComposerListFragment;
import com.pianoshelf.joey.pianoshelf.composition.Composition;
import com.pianoshelf.joey.pianoshelf.rest_api.PageInfo;
import com.pianoshelf.joey.pianoshelf.rest_api.PagedMeta;
import com.pianoshelf.joey.pianoshelf.rest_api.RW;
import com.pianoshelf.joey.pianoshelf.rest_api.SearchQuery;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * Created by joey on 12/29/14.
 */

// View for basically everything. - Responsible for swapping fragments
public class SheetListView extends BaseActivity {
    private static final String LOG_TAG = "Sheet_list";

    private String composerUrl;

    SheetArrayListFragment mSheetList;
    SheetArrayGridFragment mSheetGrid;

    // Current state of list view
    private enum SheetListState {
        INVALID, SHEETMUSIC, COMPOSER
    }

    private SheetListState mState = SheetListState.INVALID;
    private Semaphore mStateSem = new Semaphore(1);

    private int mListIconResource = R.drawable.ic_list_24dp;
    private MenuItem mListIcon;

    // set iteration order will be based on insertion order
    private Set<Composition> mSheets = new LinkedHashSet<>();

    private ProgressBar mSpinner;


    TextView mSheetMusicText;
    TextView mComposerText;

    private PageInfo mPageInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sheet_list_view);

        getSupportActionBar().setTitle("Browse");

        mSpinner = (ProgressBar) findViewById(R.id.progress_spinner);

        // Fetch intent information
        Intent intent = getIntent();
        composerUrl = intent.getStringExtra("composersUrl");


        mSheetList = SheetArrayListFragment.newInstance();
        mSheetGrid = SheetArrayGridFragment.newInstance();


        mSheetMusicText = (TextView) findViewById(R.id.sheetmusic_tab);
        mComposerText = (TextView) findViewById(R.id.composer_tab);

        // Very critical to keep these fragments in memory so we don't reload everything when
        // switch from list to grid and back
        // TODO remember user's preference of list or grid
        getSupportFragmentManager().beginTransaction()
                .add(R.id.single_frame, mSheetList)
                .hide(mSheetList)
                .add(R.id.single_frame, mSheetGrid)
                .commit();

        // Default action, opens the first page of popular sheets
        getSheetList(1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.sheet_list, menu);
        mListIcon = menu.findItem(R.id.grid_list_toggle);
        mListIcon.setIcon(mListIconResource);
        return true;
    }

    public void getSheetList(int page) {
        // Change state to sheetmusic
        if (!mStateSem.tryAcquire()) {
            Log.d(LOG_TAG, "Failed to acquire sheet music list UI semaphore");
            return;
        }
        mSpinner.setVisibility(View.VISIBLE);
        new SearchQuery(apiService).getDefault(page);
    }

    @Deprecated
    public void loadComposerList() {
        if (!mStateSem.tryAcquire()) {
            Log.d(LOG_TAG, "Composer list failed to acquire UI semaphore.");
            return;
        }
        mSpinner.setVisibility(View.VISIBLE);
        Log.v(LOG_TAG, "Composer Request Url " + composerUrl);
        JsonArrayRequest composersRequest = new JsonArrayRequest
                (Request.Method.GET, composerUrl, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mState = SheetListState.COMPOSER;
                        //Populate the list with JSON objects
                        try {
                            ComposerListFragment composerList = ComposerListFragment.newInstance(response);
                            FragmentManager fm = getSupportFragmentManager();
                            fm.beginTransaction().replace(R.id.single_frame, composerList).commit();
                            // update view before unlocking
                            fm.executePendingTransactions();
                            // update UI
                            mSpinner.setVisibility(View.GONE);
                        } catch (Error ex) {
                            throw new RuntimeException(ex);
                        } finally {
                            mStateSem.release();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO Something Here
                        Log.e(LOG_TAG, "composer request error " + error.getMessage());
                        mSpinner.setVisibility(View.GONE);
                        mStateSem.release();
                    }
                });
        VolleySingleton.getInstance(this).addToRequestQueue(composersRequest);
    }

    public void gridListToggle(MenuItem item) {
        if (!mStateSem.tryAcquire()) {
            Log.i(LOG_TAG, "Unable to acquire state sem, aborting grid list toggle");
            return;
        }

        switch (mState) {
            case SHEETMUSIC: {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                switch (mListIconResource) {
                    // TODO port this logic into XML
                    case R.drawable.ic_grid_24dp: {
                        ft.hide(mSheetList);
                        ft.show(mSheetGrid);
                        mListIconResource = R.drawable.ic_list_24dp;
                        mListIcon.setIcon(mListIconResource);
                        break;
                    }
                    case R.drawable.ic_list_24dp: {
                        ft.hide(mSheetGrid);
                        ft.show(mSheetList);
                        mListIconResource = R.drawable.ic_grid_24dp;
                        mListIcon.setIcon(mListIconResource);
                        break;
                    }
                }
                ft.commit();
                break;
            }
        }
        mStateSem.release();
    }

    @Subscribe
    void onQueryFinished(RW<List<Composition>, PagedMeta> sheetList) {
        mState = SheetListState.SHEETMUSIC;

        //Populate the list with JSON objects
        mSheets.addAll(sheetList.getData());

        mPageInfo = sheetList.getMeta().getPagination();

        mSheetList.setSheetList(mSheets);
        mSheetGrid.setSheetList(mSheets);

        mSpinner.setVisibility(View.GONE);
        mStateSem.release();
    }

}
