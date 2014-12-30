package com.pianoshelf.joey.pianoshelf;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by joey on 12/29/14.
 */
public class AddSheetToShelfTask extends AsyncTask<String, Void, Void> {
    private String additionUrlSuffix = "api/shelf/";
    private String LOG_TAG = "AddSheetToShelfTask";

    @Override
    protected Void doInBackground(String... param) {
        if (param.length < 2) {
            throw new RuntimeException("Insufficient parameters for adding current item to shelf.");
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost addToMyShelfRequest = new HttpPost(Constants.SERVER_ADDR + additionUrlSuffix);
        addToMyShelfRequest.setHeader("Authorization", Constants.TOKEN_PREFIX + param[0]);

        int responseStatusCode;
        try {
            HttpResponse addToMyShelfResponse = httpClient.execute(addToMyShelfRequest);
            responseStatusCode = addToMyShelfResponse.getStatusLine().getStatusCode();
            if (responseStatusCode == HttpStatus.SC_BAD_REQUEST) {
                Log.d(LOG_TAG, "Http bad request");
            }
        } catch (IOException ex) {
            Log.d(LOG_TAG, ex.toString());
        }
        Log.d(LOG_TAG, "Logout exception thrown during POST.");
        return null;
    }
}
