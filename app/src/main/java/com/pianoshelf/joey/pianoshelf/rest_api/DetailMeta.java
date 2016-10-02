package com.pianoshelf.joey.pianoshelf.rest_api;

import android.text.TextUtils;

/**
 * Created by joey on 06/09/16.
 * Detail usually holds an error message that needs to be handled in the UI
 */
public class DetailMeta extends MetaData {
    String detail;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        if (!TextUtils.isEmpty(detail)) {
            return super.toString() + " " + detail;
        } else {
            return super.toString();
        }
    }
}
