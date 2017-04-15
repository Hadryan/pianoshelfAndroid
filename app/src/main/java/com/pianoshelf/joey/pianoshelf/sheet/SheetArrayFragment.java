package com.pianoshelf.joey.pianoshelf.sheet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.composition.Composition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by joey on 21/11/15.
 */
public class SheetArrayFragment extends Fragment {
    protected List<Composition> mSheetList = new ArrayList<>();

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ListRecycler mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.sheet_recycler);
    }

    public void setSheetList(Collection<? extends Composition> jsonCollection) {
        mSheetList.clear();
        mSheetList.addAll(jsonCollection);
        if (mAdapter != null) {
            mAdapter.setList(mSheetList);
        }
    }

}
