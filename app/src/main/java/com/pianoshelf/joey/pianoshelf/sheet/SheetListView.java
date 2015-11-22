package com.pianoshelf.joey.pianoshelf.sheet;

import android.content.Intent;
import android.graphics.Typeface;
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
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pianoshelf.joey.pianoshelf.BaseActivity;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.VolleySingleton;
import com.pianoshelf.joey.pianoshelf.composition.ComposerListFragment;
import com.pianoshelf.joey.pianoshelf.composition.Composition;
import com.pianoshelf.joey.pianoshelf.rest_api.SheetList;
import com.pianoshelf.joey.pianoshelf.rest_api.SheetListRequest;
import com.pianoshelf.joey.pianoshelf.utility.QueryUtil;

import org.json.JSONArray;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * Created by joey on 12/29/14.
 */

// View for basically everything. - Responsible for swapping fragments
public class SheetListView extends BaseActivity {
    private static final String LOG_TAG = "Sheet list view";
    // Information sent to the server
    private String query;       // A query (i.e. composers)
    private String queryType;   // A type of query (i.e. order_by)
    private int queryListBegin;
    private int queryListSize;

    // Information received from the server
    private int sheetListCount;

    private String nextPageUrl;
    private String prevPageUrl;

    private final int DEFAULT_PAGE_BEGIN = 1;
    private final int DEFAULT_PAGE_SIZE = 20;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sheet_list_view);

        getSupportActionBar().setTitle("Sheet Music");

        mSpinner = (ProgressBar) findViewById(R.id.progress_spinner);

        // Fetch intent information
        Intent intent = getIntent();
        //TODO write a function that computes this query
        query = intent.getStringExtra("query");
        queryType = intent.getStringExtra("queryType");
        queryListBegin = intent.getIntExtra("pageBegin", DEFAULT_PAGE_BEGIN);
        queryListSize = intent.getIntExtra("pageSize", DEFAULT_PAGE_SIZE);
        composerUrl = intent.getStringExtra("composersUrl");


        mSheetList = SheetArrayListFragment.newInstance();
        mSheetGrid = SheetArrayGridFragment.newInstance();

        // Very critical to keep these fragments in memory so we don't reload everything when
        // switch from list to grid and back
        getSupportFragmentManager().beginTransaction()
                .add(R.id.single_frame, mSheetList)
                .hide(mSheetList)
                .add(R.id.single_frame, mSheetGrid)
                .commit();

        loadSheetmusicList(query, queryType, queryListBegin, queryListSize);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.sheet_list, menu);
        mListIcon = menu.findItem(R.id.grid_list_toggle);
        mListIcon.setIcon(mListIconResource);
        return true;
    }

    public void loadSheetmusicList(String query, String queryType, int queryListBegin, int queryListSize) {
        // Change state to sheetmusic
        if (mStateSem.tryAcquire()) {
            mSpinner.setVisibility(View.VISIBLE);
            String jsonQueryUrl;
            // Compose the queryURL by parsing the extras from the intent
            if (query.isEmpty()) {
                throw new RuntimeException("Empty query given to SheetListView.java");
            } else if (queryType.isEmpty()) {
                jsonQueryUrl = QueryUtil.parse(query, queryListBegin, queryListSize);
            } else {
                jsonQueryUrl = QueryUtil.parse(query, queryType, queryListBegin, queryListSize);
            }
            Log.i(LOG_TAG, "Query URL: " + jsonQueryUrl);

            SheetListRequest request = new SheetListRequest(jsonQueryUrl);
            spiceManager.execute(request, request.createCacheKey(),
                    DurationInMillis.ONE_HOUR * 4, new SheetListRequestListener());
        }
    }

    public void loadComposerList() {
        if (mStateSem.tryAcquire()) {
            mSpinner.setVisibility(View.VISIBLE);
            JsonArrayRequest composersRequest = new JsonArrayRequest
                    (Request.Method.GET, composerUrl, (String) null, new Response.Listener<JSONArray>() {
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
                                highlightText(mState);
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
                            highlightText(mState);
                            mStateSem.release();
                        }
                    });
            VolleySingleton.getInstance(this).addToRequestQueue(composersRequest);

        }
    }

    public void gridListToggle(MenuItem item) {
        if (mStateSem.tryAcquire()) {
            switch (mState) {
                case SHEETMUSIC: {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    switch (mListIconResource) {
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
    }

    public void loadSheetmusic(View view) {
        // disable clicking until view is updated
        findViewById(R.id.sheetmusic_tab).setClickable(false);
        findViewById(R.id.composer_tab).setClickable(false);
        loadSheetmusicList(query, queryType, queryListBegin, queryListSize);
    }

    public void loadComposer(View view) {
        findViewById(R.id.sheetmusic_tab).setClickable(false);
        findViewById(R.id.composer_tab).setClickable(false);
        loadComposerList();
    }


    private void highlightText(SheetListState state) {
        TextView sheetMusicView = (TextView) findViewById(R.id.sheetmusic_tab);
        TextView composer = (TextView) findViewById(R.id.composer_tab);
        switch (state) {
            case SHEETMUSIC:
                sheetMusicView.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
                composer.setTypeface(Typeface.DEFAULT);
                break;
            case COMPOSER:
                sheetMusicView.setTypeface(Typeface.DEFAULT);
                composer.setTypeface(Typeface.DEFAULT_BOLD);

        }
        sheetMusicView.setClickable(true);
        composer.setClickable(true);
    }

    private class SheetListRequestListener implements RequestListener<SheetList> {
        @Override
        public void onRequestSuccess(SheetList sheetList) {
            mState = SheetListState.SHEETMUSIC;
            //Populate the list with JSON objects
            mSheets.addAll(sheetList.getResults());

            mSheetList.setSheetList(mSheets);
            mSheetGrid.setSheetList(mSheets);

            updateUI();
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.e(LOG_TAG, "sheetmusic request error " + spiceException.getMessage());
            updateUI();
        }

        private void updateUI() {
            mSpinner.setVisibility(View.GONE);
            highlightText(mState);
            mStateSem.release();
        }

    }
}
