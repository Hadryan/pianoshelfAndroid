package com.pianoshelf.joey.pianoshelf.rest_api;

import org.json.JSONObject;

/**
 * Created by joey on 27/04/16.
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
