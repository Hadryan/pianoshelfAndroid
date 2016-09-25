package com.pianoshelf.joey.pianoshelf.rest_api;

import android.content.Context;
import android.util.Log;

import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.SharedPreferenceHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by joey on 05/09/16.
 */
public class ResponseInterceptor implements Interceptor {
    public static final String LOG_TAG = "ResponseInterceptor";
    private Set<Integer> fakableStatusCodes = new HashSet<>(Arrays.asList(400));

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        Log.v(LOG_TAG, "Processing response");
        int statusCode = response.code();
        Log.i(C.NET, "Response status code " + statusCode);

        // fake the status code if we can safely do so
        // otherwise let UI code handle the rest
        if (fakableStatusCodes.contains(statusCode)) {
            response = response.newBuilder().code(200).build();
        }

        Log.v(LOG_TAG, "Done");
        return response;
    }
}
;