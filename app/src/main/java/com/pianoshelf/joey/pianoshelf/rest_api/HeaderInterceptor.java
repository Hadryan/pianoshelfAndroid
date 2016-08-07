package com.pianoshelf.joey.pianoshelf.rest_api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.pianoshelf.joey.pianoshelf.C;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by joey on 22/06/16.
 */
public class HeaderInterceptor implements Interceptor, SharedPreferences.OnSharedPreferenceChangeListener {
    private String authToken;

    public HeaderInterceptor(SharedPreferences sp) {
        sp.registerOnSharedPreferenceChangeListener(this);
        authToken = sp.getString(C.AUTHORIZATION_TOKEN, null);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // Intercept the chain
        Request request = chain.request();

        if (authToken != null) {
            // Attach header
            Request authRequest = request.newBuilder()
                    .addHeader("Authorization", "TOKEN " + authToken)
                    .build();
            // move forward with the modified request
            return chain.proceed(authRequest);
        } else {
            return chain.proceed(request);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (C.AUTHORIZATION_TOKEN.equals(key)) {
            authToken = sharedPreferences.getString(C.AUTHORIZATION_TOKEN, null);
            Log.i(C.AUTH, "Header Interceptor updated token ");
        }
    }
}
