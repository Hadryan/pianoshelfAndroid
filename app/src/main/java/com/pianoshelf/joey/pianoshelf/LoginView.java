package com.pianoshelf.joey.pianoshelf;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pianoshelf.joey.pianoshelf.REST_API.Login;
import com.pianoshelf.joey.pianoshelf.REST_API.LoginRequest;
import com.pianoshelf.joey.pianoshelf.REST_API.LoginResponse;

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
public class LoginView extends BaseActivity {
    private String username;
    private ProgressBar progressBar;
    private TextView warningMessage;
    private TextView errorMessage;
    private final String LOG_TAG = "LoginView";
    protected String lastRequestCacheKey;

    private static final String KEY_LAST_REQUEST_CACHE_KEY = "lastRequestCacheKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginview);
        progressBar = (ProgressBar) findViewById(R.id.loginview_progress);
        warningMessage = (TextView) findViewById(R.id.loginview_warning_message);
        errorMessage = (TextView) findViewById(R.id.loginview_error_message);
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

        Login login = new Login(username, password);

        // Verify username and password
        // Short circuiting
        if (checkUsername(username) && checkPassword(password)) {
            performRequest(login);
        }
    }

    private void performRequest(Login login) {
        progressBar.setVisibility(View.VISIBLE);
        LoginRequest request = new LoginRequest(login);
        lastRequestCacheKey = request.createCacheKey();
        spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_MINUTE,
                new LoginRequestListener());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (!TextUtils.isEmpty(lastRequestCacheKey)) {
            outState.putString(KEY_LAST_REQUEST_CACHE_KEY, lastRequestCacheKey);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(KEY_LAST_REQUEST_CACHE_KEY)) {
            lastRequestCacheKey = savedInstanceState
                    .getString(KEY_LAST_REQUEST_CACHE_KEY);
            spiceManager.getFromCache(LoginResponse.class,
                    lastRequestCacheKey, DurationInMillis.ONE_MINUTE,
                    new LoginRequestListener());
        }
    }

    private class LoginRequestListener implements RequestListener<LoginResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(LoginView.this, "Error during request: " +
                    spiceException.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onRequestSuccess(LoginResponse loginResponse) {
            boolean quit = false;
            SharedPreferences.Editor globalPreferenceEditor =
                    getSharedPreferences(PIANOSHELF, MODE_PRIVATE).edit();
            if (loginResponse == null) {
                globalPreferenceEditor.remove(Constants.USERNAME);
            } else {
                globalPreferenceEditor.putString(Constants.USERNAME, username);
                globalPreferenceEditor.putString(AUTHORIZATION_TOKEN,
                        loginResponse.getAuth_token());
                Log.i(LOG_TAG, loginResponse.getAuth_token());
                quit = true;
            }
            globalPreferenceEditor.apply();
            progressBar.setVisibility(View.INVISIBLE);
            if (quit) {
                finish();
            }
        }
    }


    private boolean checkUsername(String username) {
        if (TextUtils.isEmpty(username)) {
            warningMessage.setText(getString(R.string.input_username_missing));
            return false;
        } else {
            return true;
        }
    }

    private boolean checkPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            warningMessage.setText(getString(R.string.input_password_missing));
            return false;
        } else {
            return true;
        }
    }

}
