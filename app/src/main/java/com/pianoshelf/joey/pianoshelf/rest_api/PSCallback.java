package com.pianoshelf.joey.pianoshelf.rest_api;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by joey on 04/05/16.
 * PianoShelfCallback
 * Only used when both Data and Meta are both unique Objects
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
