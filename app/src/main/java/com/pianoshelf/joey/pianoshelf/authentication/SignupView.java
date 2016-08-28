package com.pianoshelf.joey.pianoshelf.authentication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pianoshelf.joey.pianoshelf.BaseActivity;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.SharedPreferenceHelper;
import com.pianoshelf.joey.pianoshelf.rest_api.PSCallback;
import com.pianoshelf.joey.pianoshelf.rest_api.RW;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

import retrofit2.Call;

/**
 * Created by joey on 12/6/14.
 */
public class SignupView extends BaseActivity {
    private ProgressBar progressBar;
    private TextView errorMessage;
    private TextView warningMessage;

    private RegisterInfo mCredentials;

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
            mCredentials = new RegisterInfo(username, password, passwordRepeat, email);
            performRequest(mCredentials);
        }
    }

    private void performRequest(RegisterInfo credentials) {
        progressBar.setVisibility(View.VISIBLE);

        apiService.webRegistration(credentials).enqueue(new PSCallback<RW<RegistrationResponse, RegistrationMeta>>() {
            @Override
            public RW<RegistrationResponse, RegistrationMeta> convert(String json) throws IOException {
                return new ObjectMapper().readValue(json,
                        new TypeReference<RW<RegistrationResponse, RegistrationMeta>>(){});
            }

            @Override
            public void onFailure(Call<RW<RegistrationResponse, RegistrationMeta>> call, Throwable t) {
                Toast.makeText(SignupView.this, "Network error: " +
                        t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Subscribe
    public void onRegistrationSuccess(RegistrationResponse response) {
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(SignupView.this, R.string.registration_success, Toast.LENGTH_LONG).show();

        // Login the user with another request, leave activity when finished
        apiService.login(new Login(mCredentials.getUsername(), mCredentials.getPassword1()))
                .enqueue(new PSCallback<RW<LoginResponse, LoginMeta>>() {
                    @Override
                    public RW<LoginResponse, LoginMeta> convert(String json) throws IOException {
                        return new ObjectMapper().readValue(json,
                                new TypeReference<RW<LoginResponse, LoginMeta>>(){});
                    }

                    @Override
                    public void onFailure(Call<RW<LoginResponse, LoginMeta>> call, Throwable t) {
                        Toast.makeText(SignupView.this, "Network error: " +
                                t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                });
    }

    @Subscribe
    public void onRegistrationFailure(RegistrationMeta meta) {
        progressBar.setVisibility(View.GONE);
        errorMessage.setText(meta.toString());
    }

    @Subscribe
    public void onLoginComplete(LoginResponse response) {
        progressBar.setVisibility(View.INVISIBLE);

        String token = response.getAuth_token();

        // Save to disk
        // Log.i(LOG_TAG, loginResponse.getAuth_token());
        new SharedPreferenceHelper(this)
                .setAuthToken(token)
                .setUser(mCredentials.getUsername());

        // Announce token to other UI elements
        EventBus.getDefault().post(new UserToken(mCredentials.getUsername(), token));

        // exit from this login screen back to where we came from
        finish();
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
