package com.pianoshelf.joey.pianoshelf;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by joey on 1/2/15.
 */
public class RemoveSheetFromShelfTask extends AsyncTask<String, Void, Void> {
    private final String additionUrlSuffix = "api/shelf/";
    private final String sheetMusicParam = "sheetmusic";
    private final String LOG_TAG = "RemoveSheetFromShelfTask";


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
