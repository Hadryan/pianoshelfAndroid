package com.pianoshelf.joey.pianoshelf.REST_API;

/**
 * Created by joey on 03/07/15.
 */
public class Login {
    String username;
    String password;

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
