package com.pianoshelf.joey.pianoshelf.authentication;

/**
 * Created by joey on 05/05/16.
 */
public class UserToken {
    private String username;
    private String token;

    public UserToken(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public static String encodeHeader(String token) {
        return "HEADER " + token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
