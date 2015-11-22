package com.pianoshelf.joey.pianoshelf.rest_api;

import com.pianoshelf.joey.pianoshelf.composition.Composition;

import java.util.List;

/**
 * Created by joey on 21/11/15.
 */
public class SheetList {
    private int count;
    private String next;
    private String previous;
    private List<Composition> results;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<Composition> getResults() {
        return results;
    }

    public void setResults(List<Composition> results) {
        this.results = results;
    }
}
