package com.pianoshelf.joey.pianoshelf.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pianoshelf.joey.pianoshelf.BaseActivity;
import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.composition.Composition;
import com.pianoshelf.joey.pianoshelf.rest_api.DetailMeta;
import com.pianoshelf.joey.pianoshelf.rest_api.PSCallback;
import com.pianoshelf.joey.pianoshelf.rest_api.RW;
import com.pianoshelf.joey.pianoshelf.sheet.SheetArrayListFragment;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;

/**
 * Created by joey on 12/29/14.
 */
public class ProfileView extends BaseActivity {
    private static final String LOG_TAG = "ProfileView";
    private static final int PREVIEW_VALUE = 5;

    private String username;
    private Profile mProfile;

    private ProgressBar progressBar;
    private SheetArrayListFragment myShelf;

    private TextView fullName;
    private TextView userName;
    private TextView description;

    private ImageView mAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileview);
        progressBar = (ProgressBar) findViewById(R.id.profile_progress);


        fullName = (TextView) findViewById(R.id.profile_fullname);
        userName = (TextView) findViewById(R.id.profile_username);
        description = (TextView) findViewById(R.id.profile_description);

        mAvatar = (ImageView) findViewById(R.id.profile_avatar);

        myShelf = SheetArrayListFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.profile_myshelf, myShelf)
                .commit();

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        if (TextUtils.isEmpty(username)) {
            throw new RuntimeException("Empty username given to ProfileView.");
        } else {
            apiService.getProfile(username).enqueue(new PSCallback<RW<Profile, DetailMeta>>() {
                @Override
                public RW<Profile, DetailMeta> convert(String json) throws IOException {
                    return new ObjectMapper().readValue(json,
                            new TypeReference<RW<Profile, DetailMeta>>(){});
                }

                @Override
                public void onFailure(Call<RW<Profile, DetailMeta>> call, Throwable t) {
                    t.printStackTrace();
                    Log.e(C.NET, "User " + username + " failed to load" + t.getLocalizedMessage());
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    public void invokeShelfView(View view) {
        Intent intent = new Intent(this, ShelfView.class);
        intent.putExtra(C.SHELF_USER, username);
        startActivity(intent);
    }


    @Subscribe
    public void onProfileReceived(Profile profile) {
        progressBar.setVisibility(View.GONE);
        mProfile = profile;

        // user info
        fullName.setText(profile.getFull_name());
        userName.setText(profile.getUsername());
        description.setText(profile.getDescription());

        // Fetch the image from network if a url is provided.
        // Otherwise use the default mAvatar image
        String avatarUrl = profile.getLarge_profile_picture();
        if (TextUtils.isEmpty(avatarUrl)) {
            mAvatar.setImageResource(R.drawable.default_avatar);
        } else {
            Glide.with(this).load(avatarUrl).into(mAvatar);
        }
        // Load a preview of the user's shelf
        List<Composition> sheetList = profile.getShelf().getSheetmusic();
        if (sheetList.size() > PREVIEW_VALUE) {
            sheetList.subList(sheetList.size() - 1 - PREVIEW_VALUE, sheetList.size()).clear();
        }

        myShelf.setSheetList(sheetList);
    }

    @Subscribe
    public void onProfileError(DetailMeta meta) {
        progressBar.setVisibility(View.GONE);
        Log.e(LOG_TAG, "Requested user " + username + " does not exist");
        Toast.makeText(this,
                "User " + username + " does not exist",
                Toast.LENGTH_SHORT).show();
    }

}
