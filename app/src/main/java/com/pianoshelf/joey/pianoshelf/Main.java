package com.pianoshelf.joey.pianoshelf;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * This is the main logic page
 * This does not have to be the front page
 */
public class Main extends Activity {
    public static final String SERVER_ADDR = "http://198.46.142.228:5000/";

    // Public Intent Constants
    public static final int RESULT_FAILED = 1;
    public static final String ACTION_LOGIN = "ACTION_LOGIN";
    public static final String ACTION_LOGOUT = "ACTION_LOGOUT";
    public static final String AUTHORIZATION_TOKEN = "AUTHORIZATION_TOKEN";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";

    // Private Intent Constants
    private static final int TOKEN_REQUEST = 1;
    private static final int LOGOUT = 2;

    private String token;

    // Temporary variables. Needs to be removed before release
    private TextView tokenText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tokenText = (TextView) findViewById(R.id.main_token);

        // Fetch the login token from shared preferences
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String savedLoginToken = sharedPreferences.getString(AUTHORIZATION_TOKEN, null);
        if (savedLoginToken != null) {
            token = savedLoginToken;
        }
        tokenText.setText(token);
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
        switch (item.getItemId()){
            case R.id.action_settings:
                return true;
            //case R.id.actionBarSearch:
                // Process search keypress from action bar
                //return true;
            default:
               return super.onOptionsItemSelected(item);
        }
    }


    // Invoke CustomViews
    public void invokeSheetView(View view){
        Intent intent = new Intent(this, SheetView.class);
        intent.putExtra("sheetMusicUrl", (SERVER_ADDR + "/api/sheetmusic/1/"));
        startActivity(intent);
    }

    public void invokeComposerView(View view) {
        Intent intent = new Intent(this, ComposerView.class);
        intent.putExtra("server", SERVER_ADDR);
        intent.putExtra("composersEndpoint", "/api/composers/");
        intent.putExtra("composersUrl", SERVER_ADDR + "/api/composers/");
        intent.putExtra("sheetMusicEndPoint", "/api/sheetmusic/");
        startActivity(intent);
    }

    public void invokeSheetList(View view) {
        Intent intent = new Intent(this, SheetListView.class);
        intent.putExtra("server", SERVER_ADDR);
        intent.putExtra("query", "popular");
        intent.putExtra("queryType", "order_by");
        startActivity(intent);
    }

    public void invokeLogin(View view) {
        Intent intent = new Intent(ACTION_LOGIN, null, this, AuthView.class);
        //intent.putExtra(USERNAME, "hello");
        //intent.putExtra(PASSWORD, "world");
        startActivityForResult(intent, TOKEN_REQUEST);
    }

    public void invokeLogout(View view) {
        (new LogoutTask()).execute(token);
        (getPreferences(MODE_PRIVATE).edit())
                .remove(AUTHORIZATION_TOKEN).apply();
        token = null;
        tokenText.setText(token);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TOKEN_REQUEST:
                switch (resultCode) {
                    case RESULT_OK:
                        token = data.getStringExtra(AUTHORIZATION_TOKEN);
                        // Store the token in shared preferences, which is private
                        (getPreferences(MODE_PRIVATE).edit())
                                .putString(AUTHORIZATION_TOKEN, token).apply();
                        tokenText.setText(token);
                        Log.i("token", token);
                        break;
                    case RESULT_CANCELED:
                        // We don't care if the user has canceled the request
                        tokenText.setText(token);
                        break;
                    case RESULT_FAILED:
                        // Show a dialog prompting that the login has failed
                        // if the activity has not finished
                        token = null;
                        tokenText.setText(token);
                        break;
                }
        }
    }
}
