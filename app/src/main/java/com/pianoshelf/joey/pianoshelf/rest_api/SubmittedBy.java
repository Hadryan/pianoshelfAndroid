package com.pianoshelf.joey.pianoshelf.rest_api;

import java.net.URL;
import java.util.Date;

/**
 * Created by joey on 05/09/16.
 */
public class SubmittedBy {
    String username;
    String full_name;
    URL small_profile_picture;
    URL large_profile_picture;
    // ISO 8601
    Date user_since;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public URL getSmall_profile_picture() {
        return small_profile_picture;
    }

    public void setSmall_profile_picture(URL small_profile_picture) {
        this.small_profile_picture = small_profile_picture;
    }

    public URL getLarge_profile_picture() {
        return large_profile_picture;
    }

    public void setLarge_profile_picture(URL large_profile_picture) {
        this.large_profile_picture = large_profile_picture;
    }

    public Date getUser_since() {
        return user_since;
    }

    public void setUser_since(Date user_since) {
        this.user_since = user_since;
    }
}
