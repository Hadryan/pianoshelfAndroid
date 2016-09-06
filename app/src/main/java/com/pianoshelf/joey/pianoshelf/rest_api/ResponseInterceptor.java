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

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by joey on 05/09/16.
 */
public class ResponseInterceptor implements Interceptor {
    SharedPreferenceHelper sph;

    public ResponseInterceptor(Context context) {
        sph = new SharedPreferenceHelper(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        Log.i(C.NET, "Response body" + response.body());


        if (response.code() == 403) {
            // Check for invalid token
            String jsonString = response.body().string();
            Log.v(C.NET, jsonString);
            try {
                RW<String, DetailMeta> json = new ObjectMapper()
                        .readValue(jsonString, new TypeReference<RW<String, DetailMeta>>() {
                        });
                if ("Invalid token.".equals(json.getMeta().getDetail())) {
                    // void the stored token and user
                    Log.i(C.NET, "Response Interceptor removing Authorization token");
                    sph.removeAuthToken().removeUser();
                    // redo the request without the token if we have the authentication token in the header
                    // the request might not even require the token in the first place!
                    if (null != chain.request().headers().get("Authorization")) {
                        Log.w(C.NET, "Response Interceptor retrying request without Authorization token");
                        Request noAuth = chain.request().newBuilder()
                                .removeHeader("Authorization")
                                .build();
                        return chain.proceed(noAuth);
                    } else {
                        Log.i(C.NET, "Passing invalid response to UI");
                        // We pass the error to be handled by the UI code
                        return response;
                    }
                }
            } catch (IOException e) {
                // we can safely fail here if some other error occurs, just handle it in the UI
                e.printStackTrace();
                Log.e(C.NET, "Failure while serializing response json " + e.getLocalizedMessage());
            }
        }

        return response.newBuilder().code(200).build();
        // return response;
    }
}
;