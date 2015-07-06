package com.pianoshelf.joey.pianoshelf.REST_API;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.pianoshelf.joey.pianoshelf.Constants;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by joey on 03/07/15.
 */
public class RegisterRequest extends SpringAndroidSpiceRequest<RegisterResponse> {
    private final String registrationUrl = Constants.SERVER_ADDR + "api-auth/register/";
    private final String usernameParam = "username";
    private final String passwordParam = "password1";
    private final String passwordRepeatParam = "password2";
    private final String emailParam = "email";
    private final String generalError = "__all__";
    private final String unknownException = "Unknown exception.";
    private final String LOG_TAG = "RegisterRequest";
    private Register credentials;

    public RegisterRequest(Register credentials) {
        super(RegisterResponse.class);
        this.credentials = credentials;
    }

    public RegisterRequest(String username, String password1, String password2, String email) {
        super(RegisterResponse.class);
        this.credentials = new Register(username, password1, password2, email);
    }

    public RegisterRequest(String username, String password1, String password2) {
        super(RegisterResponse.class);
        this.credentials = new Register(username, password1, password2);
    }

    public String createCacheKey() {
        return "SIGNUP_CK" + credentials.getUsername();
    }

    @Override
    public RegisterResponse loadDataFromNetwork() throws Exception {
        try {
            return getRestTemplate().postForObject(registrationUrl, credentials,
                    RegisterResponse.class);
        } catch (HttpClientErrorException ex) {
            Log.d(LOG_TAG, "Error Status Code: " + ex.getStatusCode().toString());
            String responseString = ex.getResponseBodyAsString();
            Log.d(LOG_TAG, responseString);
            JsonObject jsonResponse = new JsonParser().parse(responseString).getAsJsonObject();
            String exceptionMessage;
            if (jsonResponse.has(usernameParam)) {
                exceptionMessage = jsonResponse.getAsJsonArray(usernameParam).getAsString();
            } else if (jsonResponse.has(passwordParam)) {
                exceptionMessage = jsonResponse.getAsJsonArray(passwordParam).getAsString();
            } else if (jsonResponse.has(passwordRepeatParam)) {
                exceptionMessage = jsonResponse.getAsJsonArray(passwordRepeatParam).getAsString();
            } else if (jsonResponse.has(emailParam)) {
                exceptionMessage = jsonResponse.getAsJsonArray(emailParam).getAsString();
            } else if (jsonResponse.has(generalError)) {
                exceptionMessage = jsonResponse.getAsJsonArray(generalError).getAsString();
            } else {
                exceptionMessage = unknownException;
            }
            throw new SpiceException(exceptionMessage);
        }

    }
}
