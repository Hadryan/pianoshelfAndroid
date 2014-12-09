package com.pianoshelf.joey.pianoshelf;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joey on 12/8/14.
 */
public class SignupTask extends AsyncTask<String, Void, String> {
    private String registrationUrl = "/api-auth/register/";
    private TaskDelegate delegate;
    private String usernameParam = "username";
    private String passwordParam = "password1";
    private String passwordRepeatParam = "password2";
    private String emailParam = "email";
    private String generalError = "__all__";
    private String LOG_TAG = "SignupTask";

    public SignupTask(TaskDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... signupParams) {
        if (signupParams.length < 4) {
            throw new RuntimeException("Insufficient number of parameters for registration");
        }
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost registrationRequest = new HttpPost(Main.SERVER_ADDR + registrationUrl);
        List<NameValuePair> registrationPostParams = new ArrayList<NameValuePair>(4);
        registrationPostParams.add(new BasicNameValuePair(usernameParam, signupParams[0]));
        registrationPostParams.add(new BasicNameValuePair(passwordParam, signupParams[1]));
        registrationPostParams.add(new BasicNameValuePair(passwordRepeatParam, signupParams[2]));
        registrationPostParams.add(new BasicNameValuePair(emailParam, signupParams[3]));

        HttpResponse registrationResponse;
        HttpEntity registrationEntity;
        String loginJsonResponseString;
        JSONObject registrationJsonObject;
        try {
            registrationRequest.setEntity(new UrlEncodedFormEntity(registrationPostParams, "UTF-8"));
            registrationResponse = httpClient.execute(registrationRequest);
            registrationEntity = registrationResponse.getEntity();
            loginJsonResponseString = EntityUtils.toString(registrationEntity);
            registrationJsonObject = new JSONObject(loginJsonResponseString);
            if (registrationJsonObject.has("auth_token")) {
                return "";
            } else {
                // We don't want to return too many error messages at once
                if (registrationJsonObject.has(usernameParam)) {
                    return registrationJsonObject.getJSONArray(usernameParam).getString(0);
                }
                if (registrationJsonObject.has(passwordParam)) {
                    return registrationJsonObject.getJSONArray(passwordParam).getString(0);
                }
                if (registrationJsonObject.has(passwordRepeatParam)) {
                    return registrationJsonObject.getJSONArray(passwordRepeatParam).getString(0);
                }
                if (registrationJsonObject.has(emailParam)) {
                    return registrationJsonObject.getJSONArray(emailParam).getString(0);
                }
                if (registrationJsonObject.has(generalError)) {
                    return registrationJsonObject.getJSONArray(generalError).getString(0);
                }
            }
        } catch (UnsupportedEncodingException ex) {
            Log.d(LOG_TAG, ex.toString());
        } catch (IOException ex) {
            Log.d(LOG_TAG, ex.toString());
        } catch (JSONException ex) {
            Log.d(LOG_TAG, ex.toString());
        }
        return "Exception encountered while communicating with server.";
    }

    @Override
    protected void onPostExecute(String message) {
        // Invoke delegate with empty string if login succeeds, else pass the error message
        delegate.taskCompleted(message);
    }
}
