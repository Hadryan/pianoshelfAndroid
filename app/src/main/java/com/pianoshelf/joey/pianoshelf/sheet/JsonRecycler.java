package com.pianoshelf.joey.pianoshelf.sheet;

import android.support.v7.widget.RecyclerView;

import com.pianoshelf.joey.pianoshelf.composition.Composition;
import com.pianoshelf.joey.pianoshelf.shelf.ShelfSheetInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by joey on 13/11/15.
 */
public abstract class JsonRecycler<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private static final String LOG_TAG = "Json Recycler";
    protected List<Composition> mSheetList = new ArrayList<>();

    public JsonRecycler(List<Composition> sheets) {
        mSheetList.addAll(sheets);
    }

    public void setJsonList(Collection<Composition> jsonCollection) {
        mSheetList.clear();
        mSheetList.addAll(jsonCollection);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mSheetList.size();
    }

    @Override
    public long getItemId(int position) {
        return mSheetList.get(position).getId();
    }

}
