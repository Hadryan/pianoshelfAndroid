package com.pianoshelf.joey.pianoshelf.rest_api;

import android.util.Log;

import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import retrofit2.Call;

/**
 * Created by joey on 04/05/16.
 * PianoShelfCallback
 */
public abstract class PSCallback<T extends RW> extends DeserializeCB<T> {
    @Override
    public void onSuccess(T response) {
        EventBus.getDefault().post(response.getData());
    }

    @Override
    public void onInvalid(T response) {
        EventBus.getDefault().post(response.getMeta());
    }
}
