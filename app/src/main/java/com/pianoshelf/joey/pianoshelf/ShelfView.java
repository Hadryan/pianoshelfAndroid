package com.pianoshelf.joey.pianoshelf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.pianoshelf.joey.pianoshelf.profile.Profile;
import com.pianoshelf.joey.pianoshelf.sheet.SheetArrayListFragment;

import org.json.JSONObject;

/**
 * Created by joey on 1/2/15.
 */
public class ShelfView extends BaseActivity {
    private MenuItem editItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelfview);

        Intent intent = getIntent();
        // Set the action bar title to myshelf if current user is the shelf owner.
        SharedPreferences globalPreferences = getSharedPreferences(PIANOSHELF, MODE_PRIVATE);
        String currentUser = globalPreferences.getString(C.USERNAME, null);
        String intentUser = intent.getStringExtra(C.SHELF_USER);
        if (!TextUtils.isEmpty(currentUser) && !TextUtils.isEmpty(intentUser) && currentUser.equals(intentUser)) {
            getSupportActionBar().setTitle(getString(R.string.myshelf));
        } else if (intentUser != null) {
            getSupportActionBar().setTitle(intentUser + "'s Shelf");
        } else {
            getSupportActionBar().setTitle("Shelf");
        }

        // Retrieve shelf content
        if (intent.hasExtra(C.SHELF_CONTENT)) {
            //progressBar.setVisibility(View.GONE);
            SheetArrayListFragment shelf = SheetArrayListFragment.newInstance(
                    intent.getStringExtra(C.SHELF_CONTENT));
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.single_frame, shelf).commit();
        } else if (intent.hasExtra(C.SHELF_URL)) {
            JsonObjectRequest profileRequest = new JsonObjectRequest(
                    Request.Method.GET, intent.getStringExtra(C.SHELF_URL), (String) null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Profile profile = (new Gson()).fromJson(
                                    response.toString(), Profile.class);
                            SheetArrayListFragment shelf = SheetArrayListFragment.newInstance(
                                    profile.getShelf().getSheetmusic());
                            getSupportFragmentManager().beginTransaction().replace(
                                    R.id.single_frame, shelf).commit();
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
        editItem = menu.findItem(R.id.shelf_edit);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (editItem.isVisible()) {
            super.onBackPressed();
        } else {
            ((SheetArrayListFragment) getSupportFragmentManager().findFragmentById(R.id.single_frame)).
                    setDeleteButtonVisibility(View.GONE);
            editItem.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shelf_edit:
                ((SheetArrayListFragment) getSupportFragmentManager().findFragmentById(R.id.single_frame)).
                        setDeleteButtonVisibility(View.VISIBLE);
                item.setVisible(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
