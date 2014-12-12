package com.pianoshelf.joey.pianoshelf;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.awt.font.TextAttribute;

/**
 * Created by joey on 12/6/14.
 */
public class SignupView extends Activity implements TaskDelegate {
    private ProgressBar progressBar;
    private TextView errorMessage;
    private TextView warningMessage;
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
            // Do POST registration
            progressBar.setVisibility(View.VISIBLE);
            (new SignupTask(this)).execute(username, password, passwordRepeat, email);
        }

    }

    @Override
    public void taskCompleted(String message) {
        if (message == null || message.isEmpty()) {
            // Display a toast message for the user to check email for confirmation.
            finish();
        } else {
            errorMessage.setText(message);
        }
    }

    private boolean checkUsername(String username) {
        if (username == null || username.isEmpty()) {
            warningMessage.setText(getString(R.string.input_username_missing));
            return false;
        }
        return true;
    }

    private boolean checkPassword(String password, String passwordRepeat) {
        if (password == null || password.isEmpty()) {
            warningMessage.setText(getString(R.string.input_password_missing));
            return false;
        }
        if (passwordRepeat == null || passwordRepeat.isEmpty()) {
            warningMessage.setText(getString(R.string.input_password_repeat_missing));
            return false;
        }
        if (!password.equals(passwordRepeat)) {
            errorMessage.setText(getString(R.string.input_password_mismatch));
            return false;
        }
        return true;
    }

    private boolean checkEmail(String email) {
        if (email != null && !email.isEmpty()) {
            if (email.contains("@")) {
                return true;
            }
        }
        warningMessage.setText(getString(R.string.input_email_invalid));
        return false;
    }

}
