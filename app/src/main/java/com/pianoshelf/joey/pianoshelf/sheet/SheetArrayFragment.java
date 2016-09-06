package com.pianoshelf.joey.pianoshelf.sheet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.composition.Composition;
import com.pianoshelf.joey.pianoshelf.shelf.ShelfSheetInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by joey on 21/11/15.
 */
public class SheetArrayFragment extends Fragment {
    private static final String LOG_TAG = "SheetArrayListFragment";
    private static final String JSON_ARRAY = "JSON_ARRAY";
    private static final String JSON_LIST = "JSON_LIST";

    protected final String SERVER_SHEETMUSIC_SUFFIX = "api/sheetmusic/";

    protected List<Composition> mSheetList = new ArrayList<>();

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected JsonRecycler mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.sheet_recycler);
    }

    public void setSheetList(Collection<Composition> jsonCollection) {
        mSheetList.clear();
        mSheetList.addAll(jsonCollection);
        if (mAdapter != null) {
            mAdapter.setJsonList(mSheetList);
        }
    }

}
