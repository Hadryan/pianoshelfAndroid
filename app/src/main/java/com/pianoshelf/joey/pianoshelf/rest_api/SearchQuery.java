package com.pianoshelf.joey.pianoshelf.rest_api;

import android.util.Log;

import com.pianoshelf.joey.pianoshelf.composition.SimpleComposition;

import org.greenrobot.eventbus.EventBus;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    Callback<RW<List<SimpleComposition>, PagedMeta>> queryFinishedAction = new Callback<RW<List<SimpleComposition>, PagedMeta>>() {
        @Override
        public void onResponse(Call<RW<List<SimpleComposition>, PagedMeta>> call, Response<RW<List<SimpleComposition>, PagedMeta>> response) {
            if (response == null || response.body() == null) {
                onFailure(call, null);
                return;
            }

            int metaCode = response.body().getMeta().getCode();
            if (metaCode != HttpURLConnection.HTTP_OK) {
                Log.e(QUERY_TAG, "Metadata status code not OK " + metaCode);
                onFailure(call, null);
            } else {
                EventBus.getDefault().post(response.body());
            }
        }

        @Override
        public void onFailure(Call<RW<List<SimpleComposition>, PagedMeta>> call, Throwable t) {
            Log.e(QUERY_TAG, "Sheet music request failed " + t.getLocalizedMessage());
        }
    };
    private RetroShelf mApiService;

    public SearchQuery(RetroShelf apiService) {
        mApiService = apiService;
    }

    public SearchQuery(RetroShelf apiService, Callback<RW<List<SimpleComposition>, PagedMeta>> cb) {
        mApiService = apiService;
        queryFinishedAction = cb;
    }

    public void getDefault(int page) {
        getPopular(page);
    }

    // orderBy: 'popular', sortBy: 'desc' pageSize: PAGE_SIZE
    public void getPopular(int page) {
        Map<String, String> order = new HashMap<>();
        order.put(ORDER, "popular");
        order.put(SORT, "desc");
        mApiService.sheetListQuery(order, page, PAGE_SIZE)
                .enqueue(queryFinishedAction);

    }

    public void getNew(int page) {
        Map<String, String> order = new HashMap<>();
        order.put(ORDER, "new");
        order.put(SORT, "desc");
        mApiService.sheetListQuery(order, page, PAGE_SIZE)
                .enqueue(queryFinishedAction);
    }

    public void getMostDifficult(int page) {
        Map<String, String> order = new HashMap<>();
        order.put(ORDER, "difficulty");
        order.put(SORT, "desc");
        mApiService.sheetListQuery(order, page, PAGE_SIZE)
                .enqueue(queryFinishedAction);
    }

    public void getLeastDifficult(int page) {
        Map<String, String> order = new HashMap<>();
        order.put(ORDER, "difficulty");
        order.put(SORT, "asc");
        mApiService.sheetListQuery(order, page, PAGE_SIZE)
                .enqueue(queryFinishedAction);
    }

    public void getTrending(int page) {
        Map<String, String> order = new HashMap<>();
        order.put(ORDER, "trending");
        mApiService.sheetListTrendingQuery(order, TRENDING_SHEET_SIZE, PAGE_SIZE)
                .enqueue(queryFinishedAction);
    }
}
