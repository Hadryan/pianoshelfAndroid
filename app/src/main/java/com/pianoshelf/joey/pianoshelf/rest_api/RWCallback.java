package com.pianoshelf.joey.pianoshelf.rest_api;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pianoshelf.joey.pianoshelf.C;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by joey on 04/05/16.
 */
public abstract class RWCallback<T extends RW> implements Callback<T> {
    private Set<Integer> mValidStatusCodeList = new HashSet<>();
    private boolean mStickyData = false;

    public RWCallback() {
    }

    public RWCallback(Integer mStatusCodeCheck) {
        this.mValidStatusCodeList.add(mStatusCodeCheck);
    }

    public RWCallback(Integer mStatusCodeCheck, boolean mStickyData) {
        this.mValidStatusCodeList.add(mStatusCodeCheck);
        this.mStickyData = mStickyData;
    }

    public RWCallback(Integer[] validStatusCodeList) {
        this.mValidStatusCodeList.addAll(Arrays.asList(validStatusCodeList));
    }

    public RWCallback(Integer[] mValidStatusCodeList, boolean mStickyData) {
        this.mValidStatusCodeList.addAll(Arrays.asList(mValidStatusCodeList));
        this.mStickyData = mStickyData;
    }

    public RWCallback(boolean mStickyData) {
        this.mStickyData = mStickyData;
    }

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

            // perform http status code checking
            if (mValidStatusCodeList.size() != 0) {
                if (!mValidStatusCodeList.contains(body.getMeta().getCode())) {
                    onFailure(call, new StatusCodeMismatchException(
                            "Request response not handled.\n" +
                                    " Code: " + response.code() +
                                    " Body: " + response.errorBody()));
                }
            }

            // Never post to EventBus an empty data field
            if (body.getData() != null) {
                Log.v("RWC", "data " + body.getData().getClass().toString());
                if (mStickyData) {
                    EventBus.getDefault().post(body.getData());
                } else {
                    Log.e("YAZE", "sticky event!");
                    EventBus.getDefault().postSticky(body.getData());
                }
            } else {
                EventBus.getDefault().post(body.getMeta());
            }
        } else {
            // shouldn't fire an event in the else case since metadata's type is usually the base type
            // which means if a class has 2 requests both with the response type as metadata there
            // would be a conflict
            onFailure(call, new RuntimeException(
                    "Request response not handled.\n" +
                            " Code: " + response.code() +
                            " Body: " + response.errorBody()));
        }
    }

    public static class StatusCodeMismatchException extends Exception {
        public StatusCodeMismatchException(String message) {
            super(message);
        }
    }
}
