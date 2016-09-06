package com.pianoshelf.joey.pianoshelf.rest_api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.SharedPreferenceHelper;
import com.pianoshelf.joey.pianoshelf.composition.Composition;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by joey on 05/09/16.
 */
public class ResponseInterceptor implements Interceptor {
    public static final String LOG_TAG = "ResponseInterceptor";
    int[] fakableStatusCodes = {400};
    SharedPreferenceHelper sph;

    public ResponseInterceptor(Context context) {
        sph = new SharedPreferenceHelper(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        Log.v(LOG_TAG, "Processing response");
        int statusCode = response.code();
        Log.i(C.NET, "Response status code " + statusCode);

        for(int fakableStatusCode : fakableStatusCodes) {
            if (fakableStatusCode == statusCode) {
                // fake the status code to trigger deserialization code
                response = response.newBuilder().code(200).build();
                break;
            }
        }

        Log.v(LOG_TAG, "Done");
        return response;
    }
}
;