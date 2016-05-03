package com.pianoshelf.joey.pianoshelf.rest_api;

/**
 * Created by joey on 03/05/16.
 */
public interface RWInterface<T1, T2> {
    T1 getData();

    void setData(T1 data);

    T2 getMeta();

    void setMeta(T2 meta);
}
