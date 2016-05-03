package com.pianoshelf.joey.pianoshelf.rest_api;

/**
 * Created by joey on 27/04/16.
 *
 * Fields cannot be overriden in Java, only hidden.
 * Every request contains a meta field, not necessarily a data field
 */
public abstract class RESTJSON {
    public MetaData meta = null;

    public MetaData getMeta() {
        return meta;
    }

    public void setMeta(MetaData meta) {
        this.meta = meta;
    }
}
