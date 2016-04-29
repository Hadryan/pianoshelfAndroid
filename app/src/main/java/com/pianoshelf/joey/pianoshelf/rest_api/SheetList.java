package com.pianoshelf.joey.pianoshelf.rest_api;

import com.pianoshelf.joey.pianoshelf.composition.Composition;

import java.util.List;

/**
 * Created by joey on 21/11/15.
 *
 *
 * Sample JSON input
{
    "data":[],
    "meta":{
        "code":200,
        "pagination":{
            "page":1,
            "pages":117,
            "count":2334
        }
    }
}
 *
 */
public class SheetList {
    private MetaData meta;
    private List<Composition> data;

    public List<Composition> getData() {
        return data;
    }

    public void setData(List<Composition> data) {
        this.data = data;
    }
}
