package com.pianoshelf.joey.pianoshelf.composition;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

/**
 * Created by joey on 30/11/15.
 */
public class CompositionRequest extends SpringAndroidSpiceRequest<Composition> {
    private String mUrl;

    public CompositionRequest(String url) {
        super(Composition.class);
        mUrl = url;
    }

    public String createCacheKey() {
        return "CompositionRequest" + mUrl;
    }

    @Override
    public Composition loadDataFromNetwork() throws Exception {
        return getRestTemplate().getForObject(mUrl, Composition.class);
    }
}
