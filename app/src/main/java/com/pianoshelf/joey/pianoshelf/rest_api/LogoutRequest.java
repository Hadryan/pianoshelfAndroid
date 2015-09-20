package com.pianoshelf.joey.pianoshelf.rest_api;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.pianoshelf.joey.pianoshelf.Constants;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

/**
 * Created by joey on 03/07/15.
 */
public class LogoutRequest extends SpringAndroidSpiceRequest<Void> {
    private final String logoutUrl = Constants.SERVER_ADDR + "api-auth/logout/";
    private String authToken;

    public LogoutRequest(String authToken) {
        super(Void.class);
        this.authToken = authToken;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constants.AUTHORIZATION_HEADER, Constants.TOKEN_PREFIX + authToken);
        HttpEntity<String> request = new HttpEntity<>(headers);
        getRestTemplate().postForEntity(logoutUrl, request, Void.class);
        return null;
    }
}
