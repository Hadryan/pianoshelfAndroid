package com.pianoshelf.joey.pianoshelf.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.pianoshelf.joey.pianoshelf.BaseActivity;
import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.SharedPreferenceHelper;
import com.pianoshelf.joey.pianoshelf.rest_api.MetaData;
import com.pianoshelf.joey.pianoshelf.rest_api.RW;
import com.pianoshelf.joey.pianoshelf.rest_api.RWCallback;
import com.pianoshelf.joey.pianoshelf.sheet.SheetArrayListFragment;
import com.pianoshelf.joey.pianoshelf.shelf.Shelf;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import retrofit2.Call;

/**
 * Created by joey on 1/2/15.
 */
public class ShelfView extends BaseActivity {
    private static final String LOG_TAG = "My Shelf";
    private MenuItem mEditIcon;
    private int mEditIconResource = R.drawable.ic_mode_edit_24dp;
    private SheetArrayListFragment mSheetList;

    private Shelf mShelf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelfview);


        mSheetList = SheetArrayListFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.single_frame, mSheetList)
                .commit();

        Intent intent = getIntent();
        // Set the action bar title to myshelf if current user is the shelf owner.
        String loggedInUser = new SharedPreferenceHelper(this).getUser();
        String intentUser = intent.getStringExtra(C.SHELF_USER);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (!TextUtils.isEmpty(loggedInUser) && !TextUtils.isEmpty(intentUser) && loggedInUser.equals(intentUser)) {
                actionBar.setTitle(getString(R.string.myshelf));
            } else if (!TextUtils.isEmpty(intentUser)) {
                actionBar.setTitle(intentUser + "'s Shelf");
            } else {
                actionBar.setTitle("Shelf");
            }
        }

        if (!TextUtils.isEmpty(intentUser)) {
            Log.v(LOG_TAG, "Loading user " + intentUser);

            apiService.getShelf(intentUser).enqueue(new RWCallback<RW<Shelf, MetaData>>(200) {
                @Override
                public void onFailure(Call<RW<Shelf, MetaData>> call, Throwable t) {
                    Log.e(C.NET, "Profile request failed " + t.getLocalizedMessage());
                }
            });
        }
    }

    @Subscribe
    public void onShelfEvent(Shelf shelf) {
        mShelf = shelf;

        // Announce list to fragment
        EventBus.getDefault().post(shelf.getSheetmusic());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shelf_view, menu);
        mEditIcon = menu.findItem(R.id.shelf_edit);
        mEditIcon.setIcon(mEditIconResource);
        return super.onCreateOptionsMenu(menu);
    }

    public void editToggle(MenuItem item) {
        switch (mEditIconResource) {
            case R.drawable.ic_mode_edit_24dp: {
                mEditIconResource = R.drawable.ic_done_24dp;
                mEditIcon.setIcon(mEditIconResource);
                mSheetList.enableDelete();
                break;
            }
            case R.drawable.ic_done_24dp: {
                mEditIconResource = R.drawable.ic_mode_edit_24dp;
                mEditIcon.setIcon(mEditIconResource);
                mSheetList.disableDelete();
                break;
            }
        }
    }
}
