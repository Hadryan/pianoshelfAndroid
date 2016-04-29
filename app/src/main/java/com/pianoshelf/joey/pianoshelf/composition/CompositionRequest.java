package com.pianoshelf.joey.pianoshelf.composition;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.pianoshelf.joey.pianoshelf.rest_api.CompositionJSON;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by joey on 30/11/15.
 */
public class CompositionRequest extends SpringAndroidSpiceRequest<CompositionJSON> {
    private String mUrl;

    public CompositionRequest(String url) {
        super(CompositionJSON.class);
        mUrl = url;
    }

    public String createCacheKey() {
        return "CompositionRequest" + mUrl;
    }

    @Override
    public CompositionJSON loadDataFromNetwork() throws Exception {
        return getRestTemplate().getForObject(mUrl, CompositionJSON.class);
    }
}
