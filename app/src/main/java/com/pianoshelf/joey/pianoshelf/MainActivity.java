package com.pianoshelf.joey.pianoshelf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pianoshelf.joey.pianoshelf.authentication.LoginView;
import com.pianoshelf.joey.pianoshelf.authentication.LogoutResponse;
import com.pianoshelf.joey.pianoshelf.authentication.SignupView;
import com.pianoshelf.joey.pianoshelf.authentication.UserToken;
import com.pianoshelf.joey.pianoshelf.composition.ComposerView;
import com.pianoshelf.joey.pianoshelf.profile.ProfileView;
import com.pianoshelf.joey.pianoshelf.rest_api.DeserializeCB;
import com.pianoshelf.joey.pianoshelf.rest_api.MetaData;
import com.pianoshelf.joey.pianoshelf.rest_api.RW;
import com.pianoshelf.joey.pianoshelf.sheet.SheetListView;
import com.pianoshelf.joey.pianoshelf.sheet.SheetView;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import retrofit2.Call;

/**
 * This is the main logic page
 * This does not have to be the front page
 */
public class MainActivity extends BaseActivity {
    private String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(PIANOSHELF, MODE_PRIVATE);

        getSupportActionBar().setTitle("PianoShelf");
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
        intent.putExtra(AUTHORIZATION_TOKEN, getAuthToken());
        startActivity(intent);
    }

    public void invokeComposerView(View view) {
        Intent intent = new Intent(this, ComposerView.class);
        intent.putExtra("composersEndpoint", "/api/composers/");
        intent.putExtra("composersUrl", SERVER_ADDR + "/api/composers/");
        intent.putExtra("sheetMusicEndPoint", "/api/sheetmusic/");
        intent.putExtra(AUTHORIZATION_TOKEN, getAuthToken());
        startActivity(intent);
    }

    public void invokeSheetList(View view) {
        Intent intent = new Intent(this, SheetListView.class);
        intent.putExtra("composersEndpoint", "/api/composers/");
        intent.putExtra("composersUrl", SERVER_ADDR + "api/composers/");
        intent.putExtra(AUTHORIZATION_TOKEN, getAuthToken());
        startActivity(intent);
    }

    public void invokeLogin(View view) {
        Intent intent = new Intent(ACTION_LOGIN, null, this, LoginView.class);
        startActivityForResult(intent, TOKEN_REQUEST);
    }

    public void invokeLogout(View view) {
        final SharedPreferences sharedPreferences = getSharedPreferences(PIANOSHELF, MODE_PRIVATE);
        String authToken = sharedPreferences.getString(AUTHORIZATION_TOKEN, null);
        if (authToken == null) {
            Log.e(C.AUTH, "Attempt to logout without login token! Logout aborted.");
            return;
        }

        apiService.logout(UserToken.encodeHeader(authToken))
                .enqueue(new DeserializeCB<RW<LogoutResponse, MetaData>>() {
                    @Override
                    public void onSuccess(RW<LogoutResponse, MetaData> response) {
                        sharedPreferences.edit().remove(AUTHORIZATION_TOKEN).apply();
                        Log.i(C.AUTH, "Auth token removed");
                        EventBus.getDefault().post(response.getData());
                    }

                    @Override
                    public void onInvalid(RW<LogoutResponse, MetaData> response) {
                        Log.e(C.AUTH, "Invalid Response from logout request! " + response.getMeta().getCode());
                    }

                    @Override
                    public RW<LogoutResponse, MetaData> convert(String json) throws IOException {
                        return new ObjectMapper().readValue(json,
                                new TypeReference<RW<LogoutResponse, MetaData>>() {
                                });
                    }

                    @Override
                    public void onFailure(Call<RW<LogoutResponse, MetaData>> call, Throwable t) {
                        t.printStackTrace();
                        Log.e(C.AUTH, "Logout request failed! " + t.getLocalizedMessage());
                    }
                });
    }

    public void invokeRegistration(View view) {
        Intent intent = new Intent(this, SignupView.class);
        startActivity(intent);
    }

    public void invokeProfile(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(PIANOSHELF, MODE_PRIVATE);
        String username = sharedPreferences.getString(C.USERNAME, null);
        Intent intent = new Intent(this, ProfileView.class);
        intent.putExtra("username", "hello");
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        }
    }

    private String getAuthToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(PIANOSHELF, MODE_PRIVATE);
        return sharedPreferences.getString(AUTHORIZATION_TOKEN, null);
    }
}
