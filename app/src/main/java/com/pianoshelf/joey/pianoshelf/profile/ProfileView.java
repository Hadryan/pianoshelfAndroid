package com.pianoshelf.joey.pianoshelf.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.pianoshelf.joey.pianoshelf.BaseActivity;
import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.VolleySingleton;
import com.pianoshelf.joey.pianoshelf.sheet.SheetArrayListFragment;
import com.pianoshelf.joey.pianoshelf.shelf.ShelfView;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by joey on 12/29/14.
 */
public class ProfileView extends BaseActivity {
    private Profile profile;
    private ProgressBar progressBar;
    private String username;
    private String LOG_TAG = "ProfileView";
    private static final int PREVIEW_VALUE = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileview);
        progressBar = (ProgressBar) findViewById(R.id.profile_progress);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        if (TextUtils.isEmpty(username)) {
            throw new RuntimeException("Empty username given to ProfileView.");
        } else {
            final Context context = this;
            JsonObjectRequest profileRequest = new JsonObjectRequest(
                    Request.Method.GET, parseJsonRequestUrl(username), (String) null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            profile = (new Gson()).fromJson(response.toString(), Profile.class);

                            // Update the text fields
                            TextView fullName = (TextView) findViewById(R.id.profile_fullname);
                            TextView userName = (TextView) findViewById(R.id.profile_username);
                            TextView description = (TextView)
                                    findViewById(R.id.profile_description);
                            fullName.setText(profile.getFull_name());
                            userName.setText(profile.getUsername());
                            description.setText(profile.getDescription());

                            // Fetch the image from network if a url is provided.
                            // Otherwise use the default avatar image
                            final ImageView avatar = (ImageView) findViewById(R.id.profile_avatar);
                            final String avatarUrl = profile.getLarge_profile_picture();
                            if (avatarUrl == null || avatarUrl.isEmpty()) {
                                avatar.setImageResource(R.drawable.default_avatar);
                            } else {
                                try {
                                    URL urlCheck = new URL(avatarUrl);
                                    avatarImageRequest(context, avatarUrl, avatar);
                                } catch (MalformedURLException ex) {
                                    Log.d(LOG_TAG, ex.toString());
                                    progressBar.setVisibility(View.GONE);
                                    avatar.setImageResource(R.drawable.default_avatar);
                                }
                            }

                            // Initialize myShelf
                            SheetArrayListFragment myShelf = SheetArrayListFragment.newInstance(
                                    shrinkJsonArray(profile.getShelf().getSheetmusic(),
                                            PREVIEW_VALUE));
                            getSupportFragmentManager().beginTransaction().replace(
                                    R.id.profile_myshelf, myShelf).commit();
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

    public void invokeShelfView(View view) {
        Intent intent = new Intent(this, ShelfView.class);
        if (profile != null) {
            intent.putExtra(C.SHELF_CONTENT, profile.getShelf().getSheetmusic().toString());
            intent.putExtra(C.SHELF_USER, username);
        } else {
            intent.putExtra(C.SHELF_URL, parseJsonRequestUrl(username));
        }
        startActivity(intent);
    }

    private void avatarImageRequest(Context context, String avatarUrl, final ImageView avatar) {
        VolleySingleton.getInstance(context).getImageLoader().get(
                avatarUrl,
                new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response,
                                           boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            progressBar.setVisibility(View.GONE);
                            avatar.setImageBitmap(response.getBitmap());
                        } else {
                            progressBar.setVisibility(View.GONE);
                            //avatar.setImageResource(R.drawable.default_avatar);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // We don't set the image here since the error
                        // response triggers before the actual response
                        //avatar.setImageResource(R.drawable.default_avatar);
                    }
                }, avatar.getHeight(), avatar.getHeight());
    }

    private String parseJsonRequestUrl(String username) {
        return SERVER_ADDR + "api/profile/?username=" + username;
    }

    /**
     * Shrinks a given JsonArray to newSize
     * @param array
     * @return
     */
    private JsonArray shrinkJsonArray(JsonArray array, int newSize) {
        if (array.size() <= newSize) {
            return array;
        }
        JsonArray shrunkenArray = new JsonArray();
        for(int i=0; i<newSize; ++i) {
            shrunkenArray.add(array.get(i));
        }
        return shrunkenArray;
    }


}
