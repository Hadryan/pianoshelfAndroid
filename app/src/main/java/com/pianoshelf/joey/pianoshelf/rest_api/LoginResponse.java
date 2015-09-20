package com.pianoshelf.joey.pianoshelf.rest_api;

/**
 * Created by joey on 03/07/15.
 */
public class LoginResponse {
    String hello;
    String email;
    String first_name;
    String last_name;
    String auth_token;
    boolean is_superuser;

    public LoginResponse(String hello, String email, String first_name, String last_name, String auth_token, boolean is_superuser) {
        this.hello = hello;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.auth_token = auth_token;
        this.is_superuser = is_superuser;
    }

    public String getHello() {
        return hello;
    }

    public void setHello(String hello) {
        this.hello = hello;
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
}
