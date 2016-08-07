package com.pianoshelf.joey.pianoshelf.rest_api;

import com.pianoshelf.joey.pianoshelf.authentication.Login;
import com.pianoshelf.joey.pianoshelf.authentication.LoginMeta;
import com.pianoshelf.joey.pianoshelf.authentication.LoginResponse;
import com.pianoshelf.joey.pianoshelf.authentication.LogoutMeta;
import com.pianoshelf.joey.pianoshelf.authentication.LogoutResponse;
import com.pianoshelf.joey.pianoshelf.composition.Composition;
import com.pianoshelf.joey.pianoshelf.shelf.ShelfUpdateResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by joey on 29/04/16.
 * Retrofit + Pianoshelf
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
    Call<RW<LogoutResponse, LogoutMeta>> logout();


    /* Shelf */

    // We don't care what these items return aside from the meta
    @POST(SHELF_EP)
    Call<RW<ShelfUpdateResponse, MetaData>> shelfAddSheet(@Body ShelfSheetMusic sheetId);

    @DELETE(SHELF_EP)
    Call<RW<ShelfUpdateResponse, MetaData>> shelfRemoveSheet(@Body ShelfSheetMusic sheetId);

}
