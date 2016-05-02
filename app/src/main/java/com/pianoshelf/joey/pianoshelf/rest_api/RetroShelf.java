package com.pianoshelf.joey.pianoshelf.rest_api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by joey on 29/04/16.
 */
public interface RetroShelf {
    // EP -> ENDPOINT
    public static final String
            SHEET_EP = "api/sheetmusic/",
            SHELF_EP = "api/shelf/";

    @GET(SHEET_EP + "{id}")
    Call<CompositionJSON> getSheet(@Path("id") int sheetId);

    @Headers("Authorization: TOKEN {authToken}")
    @POST(SHELF_EP)
    Call<Void> addSheetToShelf(@Path("authToken") String authToken, @Body ShelfSheetMusic sheetId);
    // TODO check what the api sends back

    @GET(SHEET_EP)
    Call<SheetList> querySheetList(@QueryMap Map order, @Query("page") int pageNumber, @Query("page_size") int pageSize);

}
