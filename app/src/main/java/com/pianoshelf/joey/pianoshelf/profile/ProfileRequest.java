package com.pianoshelf.joey.pianoshelf.profile;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.pianoshelf.joey.pianoshelf.utility.QueryUtil;

/**
 * Created by joey on 21/11/15.
 */
public class ProfileRequest extends SpringAndroidSpiceRequest<Profile> {
    private String mUrl;

    public ProfileRequest(String username) {
        super(Profile.class);
        mUrl = QueryUtil.createProfile(username);
    }

    public String createCacheKey() {
        return "ProfileRequest" + mUrl;
    }

    @Override
    public Profile loadDataFromNetwork() throws Exception {
        return getRestTemplate().getForObject(mUrl, Profile.class);
    }
}
