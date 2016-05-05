package com.pianoshelf.joey.pianoshelf.rest_api;

/**
 * Created by joey on 27/04/16.
 * <p/>
 * Implicitly gets the code field from MetaData
 */
public class PagedMeta extends MetaData {
    private PageInfo pagination;

    public PageInfo getPagination() {
        return pagination;
    }

    public void setPagination(PageInfo pagination) {
        this.pagination = pagination;
    }
}
