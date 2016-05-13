package com.pianoshelf.joey.pianoshelf.authentication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pianoshelf.joey.pianoshelf.BaseActivity;
import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.rest_api.DeserializeCB;
import com.pianoshelf.joey.pianoshelf.rest_api.LoginMeta;
import com.pianoshelf.joey.pianoshelf.rest_api.MetaData;
import com.pianoshelf.joey.pianoshelf.rest_api.PSCallback;
import com.pianoshelf.joey.pianoshelf.rest_api.RW;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private ProgressBar progressBar;
    private TextView warningMessage;
    private TextView errorMessage;
    private EditText mUserNameText;
    private EditText mPasswordText;

    private String mUsername;
    private final String LOG_TAG = "LoginView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginview);
        progressBar = (ProgressBar) findViewById(R.id.loginview_progress);
        warningMessage = (TextView) findViewById(R.id.loginview_warning_message);
        errorMessage = (TextView) findViewById(R.id.loginview_error_message);
        mUserNameText = (EditText) findViewById(R.id.loginview_username);
        mPasswordText = (EditText) findViewById(R.id.loginview_password);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * POST login if mUsername and password are valid
     *
     * @param view layout for this class
     */
    public void invokeLogin(View view) {
        // Clear the error and warning messages
        warningMessage.setText("");
        errorMessage.setText("");

        mUsername = mUserNameText.getText().toString();
        String password = mPasswordText.getText().toString();

        Login login = new Login(mUsername, password);

        // Verify mUsername and password
        // Short circuiting
        if (checkUsername(mUsername) && checkPassword(password)) {
            performRequest(login);
        }
    }

    private void performRequest(Login login) {
        progressBar.setVisibility(View.VISIBLE);

        apiService.login(login).enqueue(new PSCallback<RW<LoginResponse, LoginMeta>>() {
            @Override
            public void onFailure(Call<RW<LoginResponse, LoginMeta>> call, Throwable t) {
                Log.e(C.NET, t.getLocalizedMessage());
                Toast.makeText(LoginView.this, "Error during request: " +
                        t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);

            }
            @Override
            public RW<LoginResponse, LoginMeta> convert(String json) throws IOException {
                return new ObjectMapper().readValue(json,
                        new TypeReference<RW<LoginResponse, LoginMeta>>(){});
            }
        });
    }

    @Subscribe
    public void onLoginComplete(LoginResponse response) {
        progressBar.setVisibility(View.INVISIBLE);

        String token = response.getAuth_token();

        // Log.i(LOG_TAG, loginResponse.getAuth_token());
        getSharedPreferences(PIANOSHELF, MODE_PRIVATE).edit()
                .putString(C.USERNAME, mUsername)
                .putString(AUTHORIZATION_TOKEN, token)
                .apply();

        // Announce token to other UI elements
        EventBus.getDefault().post(new UserToken(mUsername, token));

        // exit from this login screen back to where we came from
        setResult(RESULT_OK);
        finish();
    }

    @Subscribe
    public void onLoginFailed(LoginMeta meta) {
        progressBar.setVisibility(View.INVISIBLE);

        Log.e(LOG_TAG, "Login failed, response: " + meta.toString());
        errorMessage.setText(R.string.input_login_failure);
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
