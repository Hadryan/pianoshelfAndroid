package com.pianoshelf.joey.pianoshelf.authentication;

import android.text.TextUtils;

import com.pianoshelf.joey.pianoshelf.rest_api.MetaData;

import java.util.ArrayList;
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
        List<String> errorMessage = new ArrayList<>();
        // Glorious java programming language
        if (username != null) {
            errorMessage.addAll(username);
        }
        if (password1 != null) {
            errorMessage.addAll(password1);
        }
        if (password2 != null) {
            errorMessage.addAll(password2);
        }
        if (email != null) {
            errorMessage.addAll(email);
        }
        return TextUtils.join("\n", errorMessage);
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
