package com.pianoshelf.joey.pianoshelf.rest_api;

import android.util.Log;

import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.composition.SimpleComposition;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by joey on 02/05/16.
 */
public class SearchQuery {
    public static final int PAGE_SIZE = 12;
    private static final String QUERY_TAG = "API_Query";
    // TODO maybe change this to a user setting
    private static final int TRENDING_SHEET_SIZE = 30;
    private static final String
            ORDER = "order_by",
            SORT = "sort_by";

    private RetroShelf mApiService;
    private Callback<RW<SimpleComposition[], PagedMeta>> mQueryFinishedAction
            = new RWCallback<RW<SimpleComposition[], PagedMeta>>(200) {
        @Override
        public void onFailure(Call<RW<SimpleComposition[], PagedMeta>> call, Throwable t) {
            Log.e(QUERY_TAG, "Sheet music request failed " + t.getLocalizedMessage());
        }
    };

    private PageInfo mPageInfo = new PageInfo();
    private SheetListQuery mPrevQueryType = null;

    public SearchQuery(RetroShelf apiService, int page) {
        mApiService = apiService;
        mPageInfo.setPage(page);
        EventBus.getDefault().register(this);
    }

    public void getDefault() {
        getPopular();
    }

    @Subscribe
    void onQueryFinished(PagedMeta meta) {
        mPageInfo = meta.getPagination();
    }

    public void queryNextPage() {
        Log.e(C.NET, "pageinfo " + mPageInfo.getPage() + " " + mPageInfo.getPages() + " " + mPageInfo.getCount());
        if (mPageInfo.getPage() < mPageInfo.getPages()) {
            mPageInfo.setPage(mPageInfo.getPage() + 1);
            if (mPrevQueryType != null) {
                switch (mPrevQueryType) {
                    case POPULAR:
                        getPopular();
                        break;
                    case NEW:
                        getNew();
                        break;
                    case MOST_DIFFICULT:
                        getMostDifficult();
                        break;
                    case LEAST_DIFFICULT:
                        getLeastDifficult();
                        break;
                    case TRENDING:
                        getTrending();
                        break;
                }
            }
        }
    }

    // orderBy: 'popular', sortBy: 'desc' page size: PAGE_SIZE
    public void getPopular() {
        Log.e(C.NET, "Fetching popular sheets " + mPageInfo.getPage());
        Map<String, String> order = new HashMap<>();
        order.put(ORDER, "popular");
        order.put(SORT, "desc");
        mApiService.sheetListQuery(order, mPageInfo.getPage(), PAGE_SIZE)
                .enqueue(mQueryFinishedAction);
        mPrevQueryType = SheetListQuery.POPULAR;
    }

    public void getNew() {
        Map<String, String> order = new HashMap<>();
        order.put(ORDER, "new");
        order.put(SORT, "desc");
        mApiService.sheetListQuery(order, mPageInfo.getPage(), PAGE_SIZE)
                .enqueue(mQueryFinishedAction);
        mPrevQueryType = SheetListQuery.NEW;
    }

    public void getMostDifficult() {
        Map<String, String> order = new HashMap<>();
        order.put(ORDER, "difficulty");
        order.put(SORT, "desc");
        mApiService.sheetListQuery(order, mPageInfo.getPage(), PAGE_SIZE)
                .enqueue(mQueryFinishedAction);
        mPrevQueryType = SheetListQuery.MOST_DIFFICULT;
    }

    public void getLeastDifficult() {
        Map<String, String> order = new HashMap<>();
        order.put(ORDER, "difficulty");
        order.put(SORT, "asc");
        mApiService.sheetListQuery(order, mPageInfo.getPage(), PAGE_SIZE)
                .enqueue(mQueryFinishedAction);
        mPrevQueryType = SheetListQuery.LEAST_DIFFICULT;
    }

    public void getTrending() {
        Map<String, String> order = new HashMap<>();
        order.put(ORDER, "trending");
        mApiService.sheetListTrendingQuery(order, TRENDING_SHEET_SIZE, PAGE_SIZE)
                .enqueue(mQueryFinishedAction);
        mPrevQueryType = SheetListQuery.TRENDING;
    }

    public PageInfo getPageInfo() {
        return mPageInfo;
    }

    public void setPageInfo(PageInfo mPageInfo) {
        this.mPageInfo = mPageInfo;
    }

    public SheetListQuery getPrevQueryType() {
        return mPrevQueryType;
    }

    public void setPrevQueryType(SheetListQuery mPrevQueryType) {
        this.mPrevQueryType = mPrevQueryType;
    }

    enum SheetListQuery {
        POPULAR,
        NEW,
        MOST_DIFFICULT,
        LEAST_DIFFICULT,
        TRENDING,
    }
}
