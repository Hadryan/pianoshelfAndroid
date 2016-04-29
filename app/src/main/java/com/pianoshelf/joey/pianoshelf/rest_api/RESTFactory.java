package com.pianoshelf.joey.pianoshelf.rest_api;

/**
 * Created by joey on 27/04/16.
 */
public class RESTFactory {
    public enum RESTRequesttypes {
        SHEET_INFO
    }
    public RESTJSON gen(RESTRequesttypes type) {
        switch (type) {
            case SHEET_INFO:
                return new CompositionJSON();
            default:
                throw(new RuntimeException());
        }
    }
}
