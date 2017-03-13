package com.pianoshelf.joey.pianoshelf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pianoshelf.joey.pianoshelf.authentication.LoginView;
import com.pianoshelf.joey.pianoshelf.authentication.RegistrationView;
import com.pianoshelf.joey.pianoshelf.composition.ComposerView;
import com.pianoshelf.joey.pianoshelf.profile.Profile;
import com.pianoshelf.joey.pianoshelf.profile.ProfileDescription;
import com.pianoshelf.joey.pianoshelf.profile.ProfileView;
import com.pianoshelf.joey.pianoshelf.rest_api.DetailMeta;
import com.pianoshelf.joey.pianoshelf.rest_api.MetaData;
import com.pianoshelf.joey.pianoshelf.rest_api.RW;
import com.pianoshelf.joey.pianoshelf.rest_api.RWCallback;
import com.pianoshelf.joey.pianoshelf.sdcard.UriUtils;
import com.pianoshelf.joey.pianoshelf.sheet.SheetListView;
import com.pianoshelf.joey.pianoshelf.sheet.SheetView;

import java.io.File;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * This is the main logic page
 * This does not have to be the front page
 */
public class MainActivity extends BaseActivity {
    public static final int IMAGE_REQUEST = 123;
    private String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(C.PIANOSHELF, MODE_PRIVATE);

        setTitle("PianoShelf");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            //case R.id.actionBarSearch:
            // Process search keypress from action bar
            //return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void invokeSheetView(View view) {
        Intent intent = new Intent(this, SheetView.class);
        intent.putExtra(SheetView.SHEET_ID_INTENT, 1L);
        startActivity(intent);
    }

    public void invokeComposerView(View view) {
        Intent intent = new Intent(this, ComposerView.class);
        intent.putExtra("composersEndpoint", "/api/composers/");
        intent.putExtra("sheetMusicEndPoint", "/api/sheetmusic/");
        startActivity(intent);
    }

    public void invokeSheetList(View view) {
        Intent intent = new Intent(this, SheetListView.class);
        startActivity(intent);
    }

    public void invokeLogin(View view) {
        Intent intent = new Intent(ACTION_LOGIN, null, this, LoginView.class);
        startActivity(intent);
    }

    public void invokeLogout(View view) {
        logout();
    }

    public void invokeRegistration(View view) {
        Intent intent = new Intent(this, RegistrationView.class);
        startActivity(intent);
    }

    public void invokeProfile(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(C.PIANOSHELF, MODE_PRIVATE);
        String username = sharedPreferences.getString(C.USERNAME, null);
        Intent intent = new Intent(this, ProfileView.class);
        intent.putExtra("username", "hello");
        startActivity(intent);
    }

    public void randomizeToken(View view) {
        mSPHelper.setUserAndToken(null, UUID.randomUUID().toString());
    }

    public void updateDescription(View view) {
        apiService.profileUpdateDescription(
                new ProfileDescription(UUID.randomUUID().toString().replace("-", "")))
                .enqueue(new RWCallback<RW<Profile, MetaData>>() {
                    @Override
                    public void onFailure(Call<RW<Profile, MetaData>> call, Throwable t) {
                        Log.e(C.NET, "Profile update failed" + t.getLocalizedMessage());
                    }
                });
    }

    public void updateProfileImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case IMAGE_REQUEST: {
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    File imageFile = new File(UriUtils.getRealPathFromURI(this, imageUri));

                    MultipartBody.Part imageBody =
                            MultipartBody.Part.createFormData("file", imageFile.getName(),
                                    RequestBody.create(MediaType.parse("multipart/form-data"), imageFile)) ;
                    apiService.profileUpdatePicture(imageBody).enqueue(new RWCallback<RW<Profile, DetailMeta>>() {
                        @Override
                        public void onFailure(Call<RW<Profile, DetailMeta>> call, Throwable t) {
                            Log.e(C.NET, "Failure " + t.getLocalizedMessage());
                        }
                    });
                }
            }
        }
    }
}
