package com.pianoshelf.joey.pianoshelf.recycler;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pianoshelf.joey.pianoshelf.BaseFragment;
import com.pianoshelf.joey.pianoshelf.R;

import org.greenrobot.eventbus.EventBus;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

/**
 * Created by Me on 4/15/2017.
 */

public class RecyclerFragment extends BaseFragment {

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ListRecycler mAdapter;

    private int mEndlessThreshold = 5;

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.sheet_recycler);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int visibleItems = mLayoutManager.getChildCount();
                    int totalItems = mLayoutManager.getItemCount();
                    // pastVisibleItems is assigned to be the position of the first visible item
                    int pastVisibleItems = NO_POSITION;
                    if (mLayoutManager instanceof LinearLayoutManager) {
                        pastVisibleItems = ((LinearLayoutManager) mLayoutManager)
                                .findFirstVisibleItemPosition();
                    } else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                        int[] pastVisibleItemsArray = null;
                        pastVisibleItemsArray = ((StaggeredGridLayoutManager) mLayoutManager).findFirstVisibleItemPositions(pastVisibleItemsArray);
                        if (pastVisibleItemsArray != null && pastVisibleItemsArray.length > 0) {
                            pastVisibleItems = pastVisibleItemsArray[0];
                        }
                    }
                    if (!mAdapter.moreItemsRequested()) {
                        mAdapter.setMoreItemsRequested(true);
                        int shownItems = visibleItems + pastVisibleItems;
                        if (totalItems - shownItems >= mEndlessThreshold) {
                            EventBus.getDefault().postSticky(new EndlessScrollEnd());
                        }
                    }
                }
            }
        });
    }

    // Denote that a listview has reached its end and is requesting for more content
    public static class EndlessScrollEnd {
    }
}
