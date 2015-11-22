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

import com.bumptech.glide.Glide;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pianoshelf.joey.pianoshelf.BaseActivity;
import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.composition.Composition;
import com.pianoshelf.joey.pianoshelf.sheet.SheetArrayListFragment;

import java.util.List;

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

    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileview);
        progressBar = (ProgressBar) findViewById(R.id.profile_progress);


        fullName = (TextView) findViewById(R.id.profile_fullname);
        userName = (TextView) findViewById(R.id.profile_username);
        description = (TextView) findViewById(R.id.profile_description);

        avatar = (ImageView) findViewById(R.id.profile_avatar);

        myShelf = SheetArrayListFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.profile_myshelf, myShelf)
                .commit();

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        if (TextUtils.isEmpty(username)) {
            throw new RuntimeException("Empty username given to ProfileView.");
        } else {
            ProfileRequest request = new ProfileRequest(username);
            spiceManager.execute(request, request.createCacheKey(),
                    DurationInMillis.ONE_HOUR, new ProfileRequestListener(this));

        }
    }

    public void invokeShelfView(View view) {
        Intent intent = new Intent(this, ShelfView.class);
        intent.putExtra(C.SHELF_USER, username);
        startActivity(intent);
    }

    private class ProfileRequestListener implements RequestListener<Profile> {
        private Context mContext;

        public ProfileRequestListener(Context context) {
            mContext = context;
        }

        @Override
        public void onRequestSuccess(Profile profile) {
            progressBar.setVisibility(View.GONE);
            mProfile = profile;

            // user info
            fullName.setText(profile.getFull_name());
            userName.setText(profile.getUsername());
            description.setText(profile.getDescription());

            // Fetch the image from network if a url is provided.
            // Otherwise use the default avatar image
            String avatarUrl = profile.getLarge_profile_picture();
            if (TextUtils.isEmpty(avatarUrl)) {
                avatar.setImageResource(R.drawable.default_avatar);
            } else {
                Glide.with(mContext).load(avatarUrl).into(avatar);
            }
            // Load a preview of the user's shelf
            List<Composition> sheetList = profile.getShelf().getSheetmusic();
            if (sheetList.size() > PREVIEW_VALUE) {
                sheetList.subList(sheetList.size() - 1 - PREVIEW_VALUE, sheetList.size()).clear();
            }

            myShelf.setSheetList(sheetList);
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressBar.setVisibility(View.GONE);
            Log.e(LOG_TAG, "User " + username + " failed to load");
        }
    }
}
