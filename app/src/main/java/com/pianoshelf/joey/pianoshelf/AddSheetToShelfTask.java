package com.pianoshelf.joey.pianoshelf;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joey on 12/29/14.
 */
public class AddSheetToShelfTask extends AsyncTask<String, Void, Void> {
    private final String additionUrlSuffix = "api/shelf/";
    private final String sheetMusicParam = "sheetmusic";
    private final String LOG_TAG = "AddSheetToShelfTask";

    @Override
    protected Void doInBackground(String... param) {
        if (param.length < 2) {
            throw new RuntimeException("Insufficient parameters for adding current item to shelf.");
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost addToMyShelfRequest = new HttpPost(Constants.SERVER_ADDR + additionUrlSuffix);
        addToMyShelfRequest.setHeader(Constants.AUTHORIZATION_HEADER,
                Constants.TOKEN_PREFIX + param[0]);

        List<NameValuePair> postParams = new ArrayList<>(1);
        postParams.add(new BasicNameValuePair(sheetMusicParam, param[1]));

        int responseStatusCode;
        try {
            addToMyShelfRequest.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
            HttpResponse addToMyShelfResponse = httpClient.execute(addToMyShelfRequest);
            responseStatusCode = addToMyShelfResponse.getStatusLine().getStatusCode();
            if (responseStatusCode == HttpStatus.SC_BAD_REQUEST) {
                Log.d(LOG_TAG, "Http bad request");
            }
            return null;
        } catch (IOException ex) {
            Log.d(LOG_TAG, ex.toString());
        }
        Log.d(LOG_TAG, "Exception thrown during POST.");
        return null;
    }
}
