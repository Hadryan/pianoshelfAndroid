package com.pianoshelf.joey.pianoshelf.authentication;

import android.text.TextUtils;

import com.pianoshelf.joey.pianoshelf.rest_api.MetaData;

import java.util.List;

/**
 * Created by joey on 19/08/16.
 */
public class RegistrationMeta extends MetaData {
    List<String> username;
    List<String> password1;
    List<String> password2;
    List<String> email;

    @Override
    public String toString() {
        return TextUtils.join(", ", username) + ", " + TextUtils.join(", ", password1) + ", " +
                TextUtils.join(", ", password2) + ", " + TextUtils.join(", ", username);
    }

    public List<String> getUsername() {
        return username;
    }

    public void setUsername(List<String> username) {
        this.username = username;
    }

    public List<String> getPassword1() {
        return password1;
    }

    public void setPassword1(List<String> password1) {
        this.password1 = password1;
    }

    public List<String> getPassword2() {
        return password2;
    }

    public void setPassword2(List<String> password2) {
        this.password2 = password2;
    }

    public List<String> getEmail() {
        return email;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }
}
