package com.pianoshelf.joey.pianoshelf.recycler;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by joey on 13/11/15.
 */
public abstract class ListRecycler<VH extends RecyclerView.ViewHolder, T>
        extends RecyclerView.Adapter<VH> {
    private static final String LOG_TAG = "ListRecycler";
    protected List<T> mList = new ArrayList<>();
    private boolean mMoreItemsRequested = false;

    public ListRecycler() {
    }

    public ListRecycler(Collection<? extends T> list) {
        mList.addAll(list);
    }

    public List<T> getList() {
        return mList;
    }

    public void setList(Collection<? extends T> coll) {
        mList.clear();
        mList.addAll(coll);
        notifyDataSetChanged();

        // item request completed
        setMoreItemsRequested(false);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public synchronized boolean moreItemsRequested() {
        return mMoreItemsRequested;
    }

    public synchronized void setMoreItemsRequested(boolean mMoreItemsRequested) {
        this.mMoreItemsRequested = mMoreItemsRequested;
    }
}
