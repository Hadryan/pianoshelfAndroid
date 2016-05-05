package com.pianoshelf.joey.pianoshelf.rest_api;

import java.util.List;

/**
 * Created by joey on 04/05/16.
 */
public class LoginMeta extends MetaData {
    public List<String> non_field_errors;

    public List<String> getNon_field_errors() {
        return non_field_errors;
    }

    public void setNon_field_errors(List<String> non_field_errors) {
        this.non_field_errors = non_field_errors;
    }

    @Override
    public String toString() {
        return super.toString() + " " + non_field_errors.toString();
    }
}
