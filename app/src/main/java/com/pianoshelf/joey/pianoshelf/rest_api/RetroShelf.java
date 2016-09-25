package com.pianoshelf.joey.pianoshelf.rest_api;

import com.pianoshelf.joey.pianoshelf.authentication.Login;
import com.pianoshelf.joey.pianoshelf.authentication.LoginMeta;
import com.pianoshelf.joey.pianoshelf.authentication.LogoutMeta;
import com.pianoshelf.joey.pianoshelf.authentication.LogoutResponse;
import com.pianoshelf.joey.pianoshelf.authentication.RegistrationInfo;
import com.pianoshelf.joey.pianoshelf.authentication.RegistrationMeta;
import com.pianoshelf.joey.pianoshelf.authentication.RegistrationResponse;
import com.pianoshelf.joey.pianoshelf.authentication.UserInfo;
import com.pianoshelf.joey.pianoshelf.composition.Composition;
import com.pianoshelf.joey.pianoshelf.profile.Profile;
import com.pianoshelf.joey.pianoshelf.shelf.Shelf;

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
 * function definitions here should lead with a noun followed by the verb
 * where the verb being the action and the noun being the acted
 * with the exception of get/set actions
 */
public interface RetroShelf {
    // EP -> ENDPOINT
    String  SHEET_EP = "api/sheetmusic/",
            SHELF_EP = "api/shelf/",
            LOGIN_EP = "api/auth/login/",
            LOGOUT_EP = "api/auth/logout/",
            REGISTER_EP = "api/auth/register/",
            PROFILE_EP = "api/profile/";

    /* Sheet */

    @GET(SHEET_EP + "{id}")
    Call<RW<Composition, MetaData>> getSheet(@Path("id") int sheetId);

    @GET(SHEET_EP)
    Call<RW<List<Composition>, PagedMeta>> sheetListQuery(
            @QueryMap Map<String, String> order,
            @Query("page") int pageNumber,
            @Query("page_size") int pageSize);

    @GET(SHEET_EP)
    Call<RW<List<Composition>, PagedMeta>> sheetListTrendingQuery(
            @QueryMap Map<String, String> order,
            @Query("days") int days,
            @Query("results") int sheetCount);

    /* Auth */

    @POST(LOGIN_EP)
    Call<RW<UserInfo, LoginMeta>> login(@Body Login login);

    @POST(LOGOUT_EP)
    Call<RW<LogoutResponse, LogoutMeta>> logout();

    @POST(REGISTER_EP)
    Call<RW<RegistrationResponse, RegistrationMeta>> webRegistration(@Body RegistrationInfo credentials);


    /* Shelf */

    @GET(SHELF_EP)
    Call<RW<Shelf, MetaData>> getShelf(@Query("username") String username);

    @POST(SHELF_EP)
    Call<RW<Shelf, MetaData>> shelfAddSheet(@Body ShelfSheetMusic sheetId);

    @DELETE(SHELF_EP)
    Call<RW<Shelf, MetaData>> shelfRemoveSheet(@Body ShelfSheetMusic sheetId);


    @GET(PROFILE_EP)
    Call<RW<Profile, DetailMeta>> getProfile(@Query("username") String username);

    @POST(PROFILE_EP)
    Call<RW<Profile, MetaData>> profileUpdateDescription(@Query("description") String description);

}

