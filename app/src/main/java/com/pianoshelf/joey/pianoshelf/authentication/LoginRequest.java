package com.pianoshelf.joey.pianoshelf.authentication;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.pianoshelf.joey.pianoshelf.C;

/**
 * Uses the delegate design pattern instead of callbacks
 * http://django-rest-auth.readthedocs.org/en/latest/api_endpoints.html
 * Created by joey on 11/23/14.
 */

public class LoginRequest extends SpringAndroidSpiceRequest<LoginResponse> {
    private final String loginUrl = C.SERVER_ADDR + "api-auth/login/";
    private Login credentials;

    public LoginRequest(String username, String password) {
        super(LoginResponse.class);
        this.credentials = new Login(username, password);
    }

    public LoginRequest(Login login) {
        super(LoginResponse.class);
        this.credentials = login;
    }

    public String createCacheKey() {
        return "LOGIN_CK" + credentials.getUsername();
    }

    @Override
    public LoginResponse loadDataFromNetwork() throws Exception {
        return getRestTemplate().postForObject(loginUrl, credentials, LoginResponse.class);
    }
}