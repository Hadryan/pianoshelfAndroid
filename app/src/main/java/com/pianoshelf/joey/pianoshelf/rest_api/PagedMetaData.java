package com.pianoshelf.joey.pianoshelf.rest_api;

/**
 * Created by joey on 27/04/16.
 */
public class PagedMetaData extends PageInfo {
    private PageInfo pagination;

    public PageInfo getPagination() {
        return pagination;
    }

    public void setPagination(PageInfo pagination) {
        this.pagination = pagination;
    }
}
