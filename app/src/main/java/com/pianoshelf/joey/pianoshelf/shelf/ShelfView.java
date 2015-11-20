package com.pianoshelf.joey.pianoshelf.shelf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.pianoshelf.joey.pianoshelf.BaseActivity;
import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.VolleySingleton;
import com.pianoshelf.joey.pianoshelf.profile.Profile;
import com.pianoshelf.joey.pianoshelf.sheet.SheetArrayListFragment;

import org.json.JSONObject;

/**
 * Created by joey on 1/2/15.
 */
public class ShelfView extends BaseActivity {
    private MenuItem mEditItem;
    private int mEditItemIcon;
    private SheetArrayListFragment mSheetList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelfview);

        Intent intent = getIntent();
        // Set the action bar title to myshelf if current user is the shelf owner.
        SharedPreferences globalPreferences = getSharedPreferences(PIANOSHELF, MODE_PRIVATE);
        String currentUser = globalPreferences.getString(C.USERNAME, null);
        String intentUser = intent.getStringExtra(C.SHELF_USER);

        ActionBar actionBar = getSupportActionBar();
        if (!TextUtils.isEmpty(currentUser) && !TextUtils.isEmpty(intentUser) && currentUser.equals(intentUser)) {
            actionBar.setTitle(getString(R.string.myshelf));
        } else if (intentUser != null) {
            actionBar.setTitle(intentUser + "'s Shelf");
        } else {
            actionBar.setTitle("Shelf");
        }

        // Retrieve shelf content
        if (intent.hasExtra(C.SHELF_CONTENT)) {
            //progressBar.setVisibility(View.GONE);
            mSheetList = SheetArrayListFragment.newInstance(intent.getStringExtra(C.SHELF_CONTENT));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.single_frame, mSheetList)
                    .commit();
        } else if (intent.hasExtra(C.SHELF_URL)) {
            JsonObjectRequest profileRequest = new JsonObjectRequest(
                    Request.Method.GET, intent.getStringExtra(C.SHELF_URL), (String) null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Profile profile = (new Gson()).fromJson(
                                    response.toString(), Profile.class);
                            mSheetList = SheetArrayListFragment.newInstance(
                                    profile.getShelf().getSheetmusic());
                            getSupportFragmentManager().beginTransaction().replace(
                                    R.id.single_frame, mSheetList).commit();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //TODO error message
                        }
                    });
            VolleySingleton.getInstance(this).addToRequestQueue(profileRequest);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shelf_view, menu);
        mEditItem = menu.findItem(R.id.shelf_edit);
        mEditItemIcon = R.drawable.ic_mode_edit_24dp;
        mEditItem.setIcon(mEditItemIcon);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shelf_edit: {
                switch (mEditItemIcon) {
                    case R.drawable.ic_mode_edit_24dp: {
                        mEditItemIcon = R.drawable.ic_done_24dp;
                        mEditItem.setIcon(mEditItemIcon);
                        if (mSheetList != null) {
                            mSheetList.enableDelete();
                        }
                        break;
                    }
                    case R.drawable.ic_done_24dp: {
                        mEditItemIcon = R.drawable.ic_mode_edit_24dp;
                        mEditItem.setIcon(mEditItemIcon);
                        if (mSheetList != null) {
                            mSheetList.disableDelete();
                        }
                        break;
                    }
                }
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
