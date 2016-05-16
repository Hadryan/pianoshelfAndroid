package com.pianoshelf.joey.pianoshelf.rest_api;

import com.pianoshelf.joey.pianoshelf.authentication.Login;
import com.pianoshelf.joey.pianoshelf.authentication.LoginResponse;
import com.pianoshelf.joey.pianoshelf.authentication.LogoutResponse;
import com.pianoshelf.joey.pianoshelf.composition.Composition;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
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
    String
            SHEET_EP = "api/sheetmusic/",
            SHELF_EP = "api/shelf/",
            LOGIN_EP = "api/auth/login/",
            LOGOUT_EP = "api/auth/logout/";

    /* Sheet */

    @GET(SHEET_EP + "{id}")
    Call<RW<Composition, MetaData>> getSheet(@Path("id") int sheetId);

    @GET(SHEET_EP)
    Call<RW<List<Composition>, PagedMeta>> querySheetList(
            @QueryMap Map<String, String> order,
            @Query("page") int pageNumber,
            @Query("page_size") int pageSize);

    @GET(SHEET_EP)
    Call<RW<List<Composition>, PagedMeta>> queryTrendingSheetList(
            @QueryMap Map<String, String> order,
            @Query("days") int days,
            @Query("results") int sheetCount);

    /* Auth */

    @POST(LOGIN_EP)
    Call<RW<LoginResponse, LoginMeta>> login(@Body Login login);

    @POST(LOGOUT_EP)
    Call<RW<LogoutResponse, MetaData>> logout(@Header("Authorization") String authToken);

    // TODO check what the api sends back
    @Headers("Authorization: TOKEN {authToken}")
    @POST(SHELF_EP)
    Call<Void> addSheetToShelf(@Path("authToken") String authToken, @Body ShelfSheetMusic sheetId);

}
