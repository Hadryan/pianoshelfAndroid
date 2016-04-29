package com.pianoshelf.joey.pianoshelf.rest_api;

import com.pianoshelf.joey.pianoshelf.composition.Composition;

/**
 * Created by joey on 27/04/16.
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
