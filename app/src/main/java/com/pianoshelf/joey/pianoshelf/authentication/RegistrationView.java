package com.pianoshelf.joey.pianoshelf.authentication;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pianoshelf.joey.pianoshelf.BaseActivity;
import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.rest_api.RWCallback;
import com.pianoshelf.joey.pianoshelf.rest_api.RW;

import org.greenrobot.eventbus.Subscribe;

import retrofit2.Call;

/**
 * Created by joey on 12/6/14.
 */
public class RegistrationView extends BaseActivity {
    public static final String LOG_TAG = "RegistrationView";
    private ProgressBar progressBar;
    private TextView errorMessage;
    private TextView warningMessage;

    private RegistrationInfo mCredentials;

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
            mCredentials = new RegistrationInfo(username, password, passwordRepeat, email);
            performRequest(mCredentials);
        }
    }

    private void performRequest(RegistrationInfo credentials) {
        progressBar.setVisibility(View.VISIBLE);

        apiService.webRegistration(credentials).enqueue(
                new RWCallback<RW<RegistrationResponse, RegistrationMeta>>() {
            @Override
            public void onFailure(Call<RW<RegistrationResponse, RegistrationMeta>> call, Throwable t) {
                Toast.makeText(RegistrationView.this, "Network error: " +
                        t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                errorMessage.setText("Network error.");
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Subscribe
    public void onRegistrationSuccess(RegistrationResponse response) {
        progressBar.setVisibility(View.INVISIBLE);
        Log.i(LOG_TAG, "Registration success " + response);
        Toast.makeText(RegistrationView.this, R.string.registration_success, Toast.LENGTH_LONG).show();

        if (!mCredentials.getUsername().equals(response.getUsername())) {
            throw new RuntimeException("Response username different from sent username. \n" +
                    "Sent: " + mCredentials.getUsername() + " Response: " + response.getUsername());
        }

        // Login the user with another request, let BaseActivity handle the logic
        apiService.login(new Login(mCredentials.getUsername(), mCredentials.getPassword1()))
                .enqueue(new RWCallback<RW<UserInfo, LoginMeta>>() {
                    @Override
                    public void onFailure(Call<RW<UserInfo, LoginMeta>> call, Throwable t) {
                        Log.e(C.NET, "Network error: " + t.getLocalizedMessage());
                    }
                });

        // Race condition?
        Log.i(LOG_TAG, "Exiting");
        finish();
    }

    @Subscribe
    public void onRegistrationFailure(RegistrationMeta meta) {
        Log.i(LOG_TAG, "Registration failed " + meta);
        progressBar.setVisibility(View.GONE);
        errorMessage.setText(meta.toString());
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
