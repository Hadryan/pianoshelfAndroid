package com.pianoshelf.joey.pianoshelf.rest_api;

/**
 * Created by joey on 27/04/16.
 */
public class MetaData {
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "" + code;
    }
}