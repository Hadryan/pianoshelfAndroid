package com.pianoshelf.joey.pianoshelf.rest_api;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

/**
 * Created by joey on 21/11/15.
 */
public class SheetListRequest extends SpringAndroidSpiceRequest<SheetList> {
    private String mUrl;
    public SheetListRequest(String url) {
        super(SheetList.class);
        mUrl = url;
    }

    public String createCacheKey() {
        return "SheetListRequest" + mUrl;
    }

    @Override
    public SheetList loadDataFromNetwork() throws Exception {
        return getRestTemplate().getForObject(mUrl, SheetList.class);
    }
}
