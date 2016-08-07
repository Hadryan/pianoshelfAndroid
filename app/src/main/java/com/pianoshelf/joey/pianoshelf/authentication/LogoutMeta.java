package com.pianoshelf.joey.pianoshelf.authentication;

import com.pianoshelf.joey.pianoshelf.rest_api.MetaData;

/**
 * Created by joey on 22/06/16.
 */
public class LogoutMeta extends MetaData {
    public String detail;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
