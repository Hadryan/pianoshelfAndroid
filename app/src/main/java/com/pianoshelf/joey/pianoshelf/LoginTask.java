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
 * Uses the delegate design pattern instead of callbacks
 * http://django-rest-auth.readthedocs.org/en/latest/api_endpoints.html
 * Created by joey on 11/23/14.
 */
public class LoginTask extends AsyncTask<String, Void, String> {
    private final String loginUrl = "api-auth/login/";
    private final String usernameParam = "username";
    private final String passwordParam = "password";
    private final TaskDelegate delegate;
    private final String LOG_TAG = "LoginTask";

    public LoginTask(TaskDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Attempt POST login to fetch authorization token
     * @param loginParams username and password
     * @return Authorization token if login succeeds
     */
    protected String doInBackground(String... loginParams) {
        if (loginParams.length < 2) {
            throw new RuntimeException("Insufficient parameters for login.");
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost loginRequest = new HttpPost(Constants.SERVER_ADDR + loginUrl);
        List<NameValuePair> loginPostParams = new ArrayList<>(2);
        loginPostParams.add(new BasicNameValuePair(usernameParam, loginParams[0]));
        loginPostParams.add(new BasicNameValuePair(passwordParam, loginParams[1]));

        HttpResponse loginResponse;
        HttpEntity loginEntity;
        String loginJsonResponseString;
        JSONObject loginJsonObject;
        try {
            loginRequest.setEntity(new UrlEncodedFormEntity(loginPostParams, "UTF-8"));
            loginResponse = httpClient.execute(loginRequest);
            loginEntity = loginResponse.getEntity();
            loginJsonResponseString = EntityUtils.toString(loginEntity);
            loginJsonObject = new JSONObject(loginJsonResponseString);
            return loginJsonObject.getString("auth_token");
        } catch (UnsupportedEncodingException ex) {
            Log.d(LOG_TAG, ex.toString());
        } catch (IOException ex) {
            Log.d(LOG_TAG, ex.toString());
        } catch (JSONException ex) {
            Log.d(LOG_TAG, ex.toString());
        }
        return null;
    }

    /**
     * Invoke delegate
     * @param token The authorization token returned by POST login, or the empty string
     *              if the login failed.
     */
    @Override
    protected void onPostExecute(String token) {
        delegate.taskCompleted(token);
    }
}
