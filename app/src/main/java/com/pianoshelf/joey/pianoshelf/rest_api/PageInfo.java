package com.pianoshelf.joey.pianoshelf.rest_api;

/**
 * Created by joey on 27/04/16.
 */
public class PageInfo {
    private int page;
    private int pages;
    private int count;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
