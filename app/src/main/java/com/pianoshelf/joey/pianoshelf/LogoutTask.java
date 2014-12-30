package com.pianoshelf.joey.pianoshelf;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * For logout, we don't care if the server responds or not. Simply place the request and move on.
 * Login will always POST the login request.
 * There should be no need to block main UI for logout.
 * Currently the server does not invalidate the case where a user tries to login to an account
 * already signed in. Even if that is the case, the second login attempt should always succeed.
 * POST header in curl: "Authorization: Token token_variable"
 * Created by joey on 12/1/14.
 */
public class LogoutTask extends AsyncTask<String, Void, Void>{
    private String logoutUrl = "/api-auth/logout/";
    private String LOG_TAG = "LogoutTask";
    private String LOGOUT_SUCCESS = "success";

    /**
     * Impossible to return a primitive type in Java. Thus we use the BOOLEAN CLASS FUCK YEAH.
     * Remember to use compareTo(boolean) or booleanValue() since Boolean is a class.
     * @param logoutParams
     * @return
     */
    @Override
    protected Void doInBackground(String... logoutParams) {
        if (logoutParams.length < 1) {
            throw new RuntimeException("Insufficient parameters for logout.");
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost logoutRequest = new HttpPost(Constants.SERVER_ADDR + logoutUrl);
        logoutRequest.setHeader("Authorization", Constants.TOKEN_PREFIX + logoutParams[0]);

        HttpResponse logoutResponse;
        HttpEntity logoutEntity;
        String logoutJsonResponseString;
        JSONObject logoutJsonObject;
        try {
            logoutResponse = httpClient.execute(logoutRequest);
            logoutEntity = logoutResponse.getEntity();
            logoutJsonResponseString = EntityUtils.toString(logoutEntity);
            logoutJsonObject = new JSONObject(logoutJsonResponseString);

            if (logoutJsonObject.has(LOGOUT_SUCCESS)) {
                // Report success
                Log.i(LOG_TAG, "Logout success");
            } else {
                Log.i(LOG_TAG, "Logout failure");
            }
            return null;
        } catch (IOException ex) {
            Log.d(LOG_TAG, ex.toString());
        } catch (JSONException ex) {
            Log.d(LOG_TAG, ex.toString());
        }
        Log.d(LOG_TAG, "Logout exception thrown during POST.");
        return null;
    }
}
