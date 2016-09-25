package com.pianoshelf.joey.pianoshelf.rest_api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.EventLog;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.SharedPreferenceHelper;
import com.pianoshelf.joey.pianoshelf.authentication.UserToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by joey on 22/06/16.
 */
public class HeaderInterceptor implements Interceptor {
    public static final String LOG_TAG = "HeaderInterceptor";
    private String mAuthToken;

    public HeaderInterceptor(String token) {
        mAuthToken = token;
        EventBus.getDefault().register(this);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // Intercept the chain
        Request request = chain.request();

        Response response;

        if (mAuthToken != null) {
            // Attach header
            Request authRequest = request.newBuilder()
                    .addHeader("Authorization", "TOKEN " + mAuthToken)
                    .build();
            // move forward with the modified request
            response = chain.proceed(authRequest);
        } else {
            response = chain.proceed(request);
        }

        Log.v(LOG_TAG, "Processing response");

        // Handles the case where token is invalid
        // resend the request without the header token, if it fails we let the UI code handle the rest

        String responseBody = response.body().string();
        Log.v(C.NET, responseBody);

        // Handle invalid token case here
        try {
            RW<String, DetailMeta> json = new ObjectMapper()
                    .readValue(responseBody, new TypeReference<RW<String, DetailMeta>>() {
                    });
            if ("Invalid token.".equals(json.getMeta().getDetail())) {
                // void the stored token and user
                Log.i(C.NET, "Interceptor removing Authorization token");
                EventBus.getDefault().post(new SharedPreferenceHelper.RemoveUserAndToken());
                // redo the request without the token if we have the authentication token in the header
                // the request might not even require the token in the first place!
                if (null != response.request().headers().get("Authorization")) {
                    Log.w(C.NET, "Interceptor retrying request without Authorization token");
                    Request noAuth = response.request().newBuilder()
                            .removeHeader("Authorization")
                            .build();

                    response = chain.proceed(noAuth);
                } else {
                    // We pass the error to be handled by the UI code
                    Log.i(C.NET, "Passing invalid response down the chain");
                }
            }
        } catch (IOException e) {
            // we can safely fail here if some other error occurs, just handle it in the UI
            Log.w(C.NET, "Passing onto next in chain. Failure while serializing response json." + e.getLocalizedMessage());
        }

        Log.v(LOG_TAG, "Done");
        return response.newBuilder()
                .body(ResponseBody.create(response.body().contentType(), responseBody))
                .build();
    }

    @Subscribe
    public void onUserAuthChanged(UserToken userToken) {
        mAuthToken = userToken.getToken();
        Log.i(C.AUTH, "Header Interceptor updated token ");
    }
}
