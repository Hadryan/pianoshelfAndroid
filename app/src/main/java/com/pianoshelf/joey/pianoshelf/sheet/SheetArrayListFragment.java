package com.pianoshelf.joey.pianoshelf.sheet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.composition.Composition;
import com.pianoshelf.joey.pianoshelf.composition.SimpleComposition;
import com.pianoshelf.joey.pianoshelf.recycler.ListRecycler;
import com.pianoshelf.joey.pianoshelf.recycler.RecyclerFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;

/**
 * Created by root on 11/8/14.
 * This class deals with displaying a list of sheet music
 * This class reacts in a query format
 */
public class SheetArrayListFragment extends RecyclerFragment {
    private static final String LOG_TAG = "SheetArrayListFragment";
    private int mDeleteButtonVisibility = View.GONE;

    public static SheetArrayListFragment newInstance() {
        SheetArrayListFragment sheetList = new SheetArrayListFragment();
        return sheetList;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new PreviewRecycler(R.layout.adapter_sheet_list_item_3);
        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    public void enableDelete() {
        if (mDeleteButtonVisibility != View.VISIBLE) {
            mDeleteButtonVisibility = View.VISIBLE;
            // reconstruct all views with delete button
            mAdapter.notifyDataSetChanged();
        }
    }

    public void disableDelete() {
        if (mDeleteButtonVisibility != View.GONE) {
            mDeleteButtonVisibility = View.GONE;
            mAdapter.notifyDataSetChanged();
        }
    }

    public class PreviewRecycler extends ListRecycler<CompositionViewHolder, Composition> {
        protected int mLayout;

        public PreviewRecycler(int layout) {
            super();
            mLayout = layout;
            EventBus.getDefault().register(this);
        }

        @Subscribe
        void onQueryFinished(SimpleComposition[] sheetList) {
            mList.addAll(Arrays.asList(sheetList));

            notifyDataSetChanged();
            setMoreItemsRequested(false);
        }

        @Override
        public void onBindViewHolder(final CompositionViewHolder holder, final int position) {
            holder.bindSheetJson(mList.get(position));
            final long sheetId = mList.get(position).getId();
            holder.mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openSheet = new Intent(getContext(), SheetFrameView.class);
                    openSheet.putExtra(SheetFragment.SHEET_ID_INTENT, sheetId);
                    startActivity(openSheet);
                }
            });
            holder.mDeleteButton.setVisibility(mDeleteButtonVisibility);
            holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mList.size());
                    // TODO remove the actual shelf from server
                }
            });
        }

        @Override
        public CompositionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(mLayout, parent, false);
            return new CompositionViewHolder(view);
        }
    }
}
