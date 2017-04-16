package com.pianoshelf.joey.pianoshelf.recycler;

import com.pianoshelf.joey.pianoshelf.composition.Composition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by joey on 21/11/15.
 */
public class SheetArrayFragment extends RecyclerFragment {
    protected List<Composition> mSheetList = new ArrayList<>();


    public void setSheetList(Collection<? extends Composition> jsonCollection) {
        mSheetList.clear();
        mSheetList.addAll(jsonCollection);
        if (mAdapter != null) {
            mAdapter.setList(mSheetList);
        }
    }

}
