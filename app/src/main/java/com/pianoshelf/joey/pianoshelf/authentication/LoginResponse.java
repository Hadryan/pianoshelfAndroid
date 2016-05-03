package com.pianoshelf.joey.pianoshelf.authentication;

import java.net.URL;

/**
 * Created by joey on 03/07/15.
 */
public class LoginResponse {
    String username;
    String email;
    String first_name;
    String last_name;
    String auth_token;
    boolean is_superuser;
    URL profile_picture;

    public LoginResponse(String username, String email, String first_name, String last_name, String auth_token, boolean is_superuser) {
        this.username = username;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.auth_token = auth_token;
        this.is_superuser = is_superuser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public boolean is_superuser() {
        return is_superuser;
    }

    public void setIs_superuser(boolean is_superuser) {
        this.is_superuser = is_superuser;
    }

    public URL getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(URL profile_picture) {
        this.profile_picture = profile_picture;
    }
}
