package com.pianoshelf.joey.pianoshelf.rest_api;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by joey on 02/05/16.
 */
public class SearchQuery {
    private static final String QUERY_TAG = "API_Query";
    public static final int PAGE_SIZE = 12;

    // TODO maybe change this to a user setting
    private static final int TRENDING_SHEET_SIZE = 30;

    private static final String
            ORDER = "order_by",
            SORT = "sort_by";

    private RetroShelf mApiService;

    Callback<SheetList> queryFinishedAction = new Callback<SheetList>() {
        @Override
        public void onResponse(Call<SheetList> call, Response<SheetList> response) {
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
        public void onFailure(Call<SheetList> call, Throwable t) {
            Log.e(QUERY_TAG, "Sheet music request failed");
        }
    };

    public SearchQuery(RetroShelf apiService) {
        mApiService = apiService;
    }

    public SearchQuery(RetroShelf apiService, Callback<SheetList> cb) {
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
        mApiService.querySheetList(order, page, PAGE_SIZE)
                .enqueue(queryFinishedAction);

    }

    public void getNew(int page) {
        Map<String, String> order = new HashMap<>();
        order.put(ORDER, "new");
        order.put(SORT, "desc");
        mApiService.querySheetList(order, page, PAGE_SIZE)
                .enqueue(queryFinishedAction);
    }

    public void getMostDifficult(int page) {
        Map<String, String> order = new HashMap<>();
        order.put(ORDER, "difficulty");
        order.put(SORT, "desc");
        mApiService.querySheetList(order, page, PAGE_SIZE)
                .enqueue(queryFinishedAction);
    }

    public void getLeastDifficult(int page) {
        Map<String, String> order = new HashMap<>();
        order.put(ORDER, "difficulty");
        order.put(SORT, "asc");
        mApiService.querySheetList(order, page, PAGE_SIZE)
                .enqueue(queryFinishedAction);
    }

    public void getTrending(int page) {
        Map<String, String> order = new HashMap<>();
        order.put(ORDER, "trending");
        mApiService.queryTrendingSheetList(order, TRENDING_SHEET_SIZE, PAGE_SIZE)
                .enqueue(queryFinishedAction);
    }
}
