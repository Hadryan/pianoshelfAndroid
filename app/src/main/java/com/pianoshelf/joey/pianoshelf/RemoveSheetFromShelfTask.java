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
import org.apache.http.protocol.HTTP;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joey on 1/2/15.
 */
public class RemoveSheetFromShelfTask extends AsyncTask<String, Void, Void> {
    private String additionUrlSuffix = "api/shelf/";
    private String sheetMusicParam = "sheetmusic";
    private String LOG_TAG = "RemoveSheetFromShelfTask";


    @Override
    protected Void doInBackground(String... param) {
        if (param.length < 2) {
            throw new RuntimeException("Insufficient parameters for removing current item from shelf.");
        }

        HttpURLConnection connection = null;
        try {
            URL requestURL = new URL(Constants.SERVER_ADDR + additionUrlSuffix);
            connection = (HttpURLConnection) requestURL.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty(Constants.AUTHORIZATION_HEADER, Constants.TOKEN_PREFIX
                    + param[0]);
            connection.setDoOutput(true);
            OutputStreamWriter data = new OutputStreamWriter(connection.getOutputStream());
            data.write(sheetMusicParam + "=" + param[1]);
            data.flush();
            data.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                Log.d(LOG_TAG, "Http bad request");
            }
            return null;
        } catch (MalformedURLException ex) {
            Log.e(LOG_TAG, "Malformed URL exception. Possible API update.");
        } catch (IOException ex) {
            Log.d(LOG_TAG, ex.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}
