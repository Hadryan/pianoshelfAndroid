package com.pianoshelf.joey.pianoshelf.authentication;

import android.text.TextUtils;

import com.pianoshelf.joey.pianoshelf.rest_api.DetailMeta;
import com.pianoshelf.joey.pianoshelf.rest_api.MetaData;

import java.util.List;

/**
 * Created by joey on 04/05/16.
 */
public class LoginMeta extends DetailMeta {
    public List<String> non_field_errors;

    public List<String> getNon_field_errors() {
        return non_field_errors;
    }

    public void setNon_field_errors(List<String> non_field_errors) {
        this.non_field_errors = non_field_errors;
    }

    @Override
    public String toString() {
        String suffix = "";
        if (non_field_errors != null) {
            suffix += TextUtils.join(" ", non_field_errors);
        }
        return super.toString() + suffix;
    }
}
