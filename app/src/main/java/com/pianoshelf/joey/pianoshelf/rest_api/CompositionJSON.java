package com.pianoshelf.joey.pianoshelf.rest_api;

import com.pianoshelf.joey.pianoshelf.composition.Composition;

/**
 * Created by joey on 27/04/16.
 *
 * Each POJO requires the definition of its own data field, since the field needs to be strongly typed
 */
public class CompositionJSON extends RESTJSON {
    private Composition data;

    public Composition getData() {
        return data;
    }

    public void setData(Composition data) {
        this.data = data;
    }
}
