package com.pianoshelf.joey.pianoshelf.sheet;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pianoshelf.joey.pianoshelf.BaseActivity;
import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.composition.SimpleComposition;
import com.pianoshelf.joey.pianoshelf.recycler.RecyclerFragment;
import com.pianoshelf.joey.pianoshelf.rest_api.SearchQuery;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by joey on 12/29/14.
 */

// View for basically everything. - Responsible for swapping fragments
public class SheetListView extends BaseActivity {
    private static final String LOG_TAG = "Sheet_list";
    SheetArrayListFragment mSheetList;
    SheetArrayGridFragment mSheetGrid;
    TextView mSheetMusicText;
    TextView mComposerText;
    private SheetListState mState = SheetListState.INVALID;
    private int mListIconResource = R.drawable.ic_list_24dp;
    private MenuItem mListIcon;
    private ProgressBar mSpinner;

    private SearchQuery mQueryInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sheet_list_view);
        mSpinner = (ProgressBar) findViewById(R.id.progress_spinner);
        mSheetMusicText = (TextView) findViewById(R.id.sheetmusic_tab);
        mComposerText = (TextView) findViewById(R.id.composer_tab);

        setTitle("Browse");

        mSheetList = SheetArrayListFragment.newInstance();
        mSheetGrid = SheetArrayGridFragment.newInstance();

        // Very critical to keep these fragments in memory so we don't reload everything when
        // switch from list to grid and back
        // TODO remember user's preference of list or grid
        getSupportFragmentManager().beginTransaction()
                .add(R.id.single_frame, mSheetList)
                .hide(mSheetList)
                .add(R.id.single_frame, mSheetGrid)
                .commit();

        // Default action, opens the first page of popular sheets
        mSpinner.setVisibility(View.VISIBLE);

        mQueryInfo = new SearchQuery(apiService, 1);
        mQueryInfo.getDefault();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.sheet_list, menu);
        mListIcon = menu.findItem(R.id.grid_list_toggle);
        mListIcon.setIcon(mListIconResource);
        return true;
    }

    public void gridListToggle(MenuItem item) {

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
    }

    @Subscribe
    void onQueryFinished(SimpleComposition[] sheetList) {
        mState = SheetListState.SHEETMUSIC;
        mSpinner.setVisibility(View.GONE);
    }

    @Subscribe(sticky = true)
    void onScrollEnd(RecyclerFragment.EndlessScrollEnd event) {
        Log.e(C.NET, "SheetList view got event");
        // end of pages check present in mQueryInfo object
        mQueryInfo.queryNextPage();
        // clear sticky event, if we have not made an api call
        // then this event would never fire again
        EventBus.getDefault().removeStickyEvent(RecyclerFragment.EndlessScrollEnd.class);
    }

    // Current state of list view
    private enum SheetListState {
        INVALID, SHEETMUSIC, COMPOSER
    }

}
