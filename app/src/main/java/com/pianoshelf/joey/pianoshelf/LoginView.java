package com.pianoshelf.joey.pianoshelf;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * It seems impossible to execute two concurrent activities in android
 * Thus its only logical that either the login was done on the main activity or
 * in a separate login activity.
 * We can assume that every time the user presses the login key, we complete the POST request
 * However, we only process the information received by the POST request iff we are in the same
 * activity that initiated the POST request, thus we are exposed to an edge case where the request
 * is sent but the activity is frozen on the stack if we allow the user to go back from the login
 * activity. Thus I believe we either need to stall the user (bad) or find a concurrent way to
 * process the return value of the POST request.
 * http://django-rest-auth.readthedocs.org/en/latest/api_endpoints.html
 * Created by root on 11/25/14.
 */
public class LoginView extends Activity implements TaskDelegate {
    private String username;
    private ProgressBar progressBar;
    private TextView warningMessage;
    private TextView errorMessage;
    private String LOG_TAG = "LoginView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginview);
        progressBar = (ProgressBar) findViewById(R.id.loginview_progress);
        warningMessage = (TextView) findViewById(R.id.loginview_warning_message);
        errorMessage = (TextView) findViewById(R.id.loginview_error_message);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * POST login if username and password are valid
     * @param view layout for this class
     */
    public void invokeLogin(View view) {
        // Clear the error and warning messages
        warningMessage.setText("");
        errorMessage.setText("");

        username = ((EditText) findViewById(R.id.loginview_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.loginview_password)).getText().toString();

        // Verify username and password
        // Short circuiting
        if (checkUsername(username) && checkPassword(password)) {
            progressBar.setVisibility(View.VISIBLE);
            (new LoginTask(this)).execute(username, password);
        }
    }

    /**
     * Required by delegate
     * Update views and set Token if login has succeeded
     * @param token Authorization token returned by POST login
     */
    @Override
    public void taskCompleted(String token) {
        Intent returnIntent = new Intent();
        SharedPreferences.Editor globalPreferenceEditor =
                getSharedPreferences(Constants.PIANOSHELF, MODE_PRIVATE).edit();
        progressBar.setVisibility(View.INVISIBLE);
        if (token != null) {
            globalPreferenceEditor.putString(Constants.USERNAME, username);
            returnIntent.putExtra(Constants.AUTHORIZATION_TOKEN, token);
            setResult(RESULT_OK, returnIntent);
            finish();
        } else {
            // Failed to fetch token, update view accordingly
            globalPreferenceEditor.remove(Constants.USERNAME);
            errorMessage.setText(getString(R.string.input_login_failure));
            // TODO put a failure reason on returnIntent
            setResult(Constants.RESULT_FAILED);
        }
        globalPreferenceEditor.apply();
    }

    private boolean checkUsername(String username) {
        if (username == null || username.isEmpty()) {
            warningMessage.setText(getString(R.string.input_username_missing));
            return false;
        }
        return true;
    }

    private boolean checkPassword(String password) {
        if (password == null || password.isEmpty()) {
            warningMessage.setText(getString(R.string.input_password_missing));
            return false;
        }
        return true;
    }

}
