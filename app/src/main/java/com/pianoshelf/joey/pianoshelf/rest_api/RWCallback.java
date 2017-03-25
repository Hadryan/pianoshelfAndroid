package com.pianoshelf.joey.pianoshelf.rest_api;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pianoshelf.joey.pianoshelf.C;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by joey on 04/05/16.
 */
public abstract class RWCallback<T extends RW> implements Callback<T> {
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        Log.i(C.NET, "HTTP request response code: " + response.code());
        Log.i(C.NET, "Response body: " + response.body());
        Log.i(C.NET, "Response error body: " + response.errorBody());
        try {
            String json = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
                    .writeValueAsString(response.body());
            Log.i(C.NET, json);
        } catch (IOException e) {
            Log.e(C.NET, "Error while converting body to string" + e.getLocalizedMessage());
        }

        if (response.isSuccessful()) {
            // this only indicates deserialization has occurred
            // need to read the actual http status code in the metadata
            T body = response.body();
            // Never post to EventBus an empty data field
            if (body.getData() != null) {
                Log.v("RWC", "data " + body.getData().getClass().toString());
                EventBus.getDefault().post(body.getData());
            } else {
                EventBus.getDefault().post(body.getMeta());
            }
            // shouldn't fire an event in the else case since metadata's type is usually the base type
            // which means if a class has 2 requests both with the response type as metadata there
            // would be a conflict
        } else {
            onFailure(call, new RuntimeException("Request response not handled. "+
                    "Code: " + response.code() + " Body:" + response.errorBody()));
        }
    }
}
