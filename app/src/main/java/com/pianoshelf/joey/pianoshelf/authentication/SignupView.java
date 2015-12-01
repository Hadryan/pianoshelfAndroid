package com.pianoshelf.joey.pianoshelf.authentication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pianoshelf.joey.pianoshelf.BaseActivity;
import com.pianoshelf.joey.pianoshelf.R;

/**
 * Created by joey on 12/6/14.
 */
public class SignupView extends BaseActivity {
    private ProgressBar progressBar;
    private TextView errorMessage;
    private TextView warningMessage;
    protected String lastRequestCacheKey;
    private final String LOG_TAG = "SignupView";

    private static final String KEY_LAST_REQUEST_CACHE_KEY = "lastRequestCacheKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupview);
        progressBar = (ProgressBar) findViewById(R.id.signupview_progress);
        errorMessage = (TextView) findViewById(R.id.signupview_error_message);
        warningMessage = (TextView) findViewById(R.id.signupview_warning_message);
    }

    public void invokeRegistration(View view) {
        progressBar.setVisibility(View.GONE);
        errorMessage.setText("");
        warningMessage.setText("");
        String username = ((EditText) findViewById(R.id.signupview_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.signupview_password_first))
                .getText().toString();
        String passwordRepeat = ((EditText) findViewById(R.id.signupview_password_second))
                .getText().toString();
        String email = ((EditText) findViewById(R.id.signupview_email)).getText().toString();
        boolean signupCheck = checkUsername(username) && checkPassword(password, passwordRepeat)
                && checkEmail(email);
        if (signupCheck) {
            Register credentials = new Register(username, password, passwordRepeat, email);
            performRequest(credentials);
        }
    }

    private void performRequest(Register credentials) {
        progressBar.setVisibility(View.VISIBLE);
        RegisterRequest request = new RegisterRequest(credentials);
        lastRequestCacheKey = request.createCacheKey();
        spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_MINUTE,
                new SignupRequestListener());
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
            spiceManager.getFromCache(RegisterResponse.class,
                    lastRequestCacheKey, DurationInMillis.ALWAYS_EXPIRED,
                    new SignupRequestListener());
        }
    }

    private class SignupRequestListener implements RequestListener<RegisterResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressBar.setVisibility(View.GONE);
            errorMessage.setText(spiceException.getCause().getMessage());
        }

        @Override
        public void onRequestSuccess(RegisterResponse registerResponse) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(SignupView.this, R.string.registration_success, Toast.LENGTH_LONG).show();
            finish();
        }
    }


    private boolean checkUsername(String username) {
        if (username == null || username.isEmpty()) {
            warningMessage.setText(getString(R.string.input_username_missing));
            return false;
        } else {
            return true;
        }
    }

    private boolean checkPassword(String password, String passwordRepeat) {
        if (TextUtils.isEmpty(password)) {
            warningMessage.setText(getString(R.string.input_password_missing));
        } else if (TextUtils.isEmpty(passwordRepeat)) {
            warningMessage.setText(getString(R.string.input_password_repeat_missing));
        } else if (!password.equals(passwordRepeat)) {
            errorMessage.setText(getString(R.string.input_password_mismatch));
        } else {
            return true;
        }
        return false;
    }

    private boolean checkEmail(String email) {
        if (!TextUtils.isEmpty(email) && email.contains("@")) {
            return true;
        } else {
            warningMessage.setText(getString(R.string.input_email_invalid));
            return false;
        }

    }

}
