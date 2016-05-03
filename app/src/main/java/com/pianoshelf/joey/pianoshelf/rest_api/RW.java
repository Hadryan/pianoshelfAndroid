package com.pianoshelf.joey.pianoshelf.rest_api;

/**
 * Created by joey on 03/05/16.
 * RW = ResponseWrapper
 * Dis some dumb shit if u ask me but whatever
 */
public class RW<T1, T2> {
    public T1 data;
    public T2 meta;

    public T1 getData() {
        return data;
    }

    public void setData(T1 data) {
        this.data = data;
    }

    public T2 getMeta() {
        return meta;
    }

    public void setMeta(T2 meta) {
        this.meta = meta;
    }
}
