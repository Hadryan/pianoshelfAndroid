package com.pianoshelf.joey.pianoshelf.rest_api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.SharedPreferenceHelper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by joey on 22/06/16.
 */
public class HeaderInterceptor implements Interceptor, SharedPreferences.OnSharedPreferenceChangeListener {
    private String mAuthToken;

    public HeaderInterceptor(Context context) {
        SharedPreferences sp = new SharedPreferenceHelper(context).getSharedPreferences();
        sp.registerOnSharedPreferenceChangeListener(this);
        mAuthToken = sp.getString(C.AUTHORIZATION_TOKEN, null);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // Intercept the chain
        Request request = chain.request();

        if (mAuthToken != null) {
            // Attach header
            Request authRequest = request.newBuilder()
                    .addHeader("Authorization", "TOKEN " + mAuthToken)
                    .build();
            Log.v(C.NET, "DEBUG" + authRequest.headers().toString());
            // move forward with the modified request
            return chain.proceed(authRequest);
        } else {
            return chain.proceed(request);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (C.AUTHORIZATION_TOKEN.equals(key)) {
            mAuthToken = sharedPreferences.getString(C.AUTHORIZATION_TOKEN, null);
            Log.i(C.AUTH, "Header Interceptor updated token ");
        }
    }
}
