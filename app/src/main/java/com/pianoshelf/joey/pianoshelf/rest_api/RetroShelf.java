package com.pianoshelf.joey.pianoshelf.rest_api;

import com.pianoshelf.joey.pianoshelf.authentication.Login;
import com.pianoshelf.joey.pianoshelf.authentication.LoginMeta;
import com.pianoshelf.joey.pianoshelf.authentication.LogoutMeta;
import com.pianoshelf.joey.pianoshelf.authentication.LogoutResponse;
import com.pianoshelf.joey.pianoshelf.authentication.RegistrationInfo;
import com.pianoshelf.joey.pianoshelf.authentication.RegistrationMeta;
import com.pianoshelf.joey.pianoshelf.authentication.RegistrationResponse;
import com.pianoshelf.joey.pianoshelf.authentication.UserInfo;
import com.pianoshelf.joey.pianoshelf.comment.Comment;
import com.pianoshelf.joey.pianoshelf.comment.CommentPost;
import com.pianoshelf.joey.pianoshelf.comment.CommentText;
import com.pianoshelf.joey.pianoshelf.composition.FullComposition;
import com.pianoshelf.joey.pianoshelf.composition.SimpleComposition;
import com.pianoshelf.joey.pianoshelf.profile.Profile;
import com.pianoshelf.joey.pianoshelf.profile.ProfileDescription;
import com.pianoshelf.joey.pianoshelf.shelf.Shelf;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
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
            PROFILE_EP = "api/profile/",
            COMMENT_EP = "api/comment/";

    /* Sheet */

    @GET(SHEET_EP + "{id}")
    Call<RW<FullComposition, MetaData>> getSheet(@Path("id") int sheetId);

    @GET(SHEET_EP)
    Call<RW<SimpleComposition[], PagedMeta>> sheetListQuery(
            @QueryMap Map<String, String> order,
            @Query("page") int pageNumber,
            @Query("page_size") int pageSize);

    @GET(SHEET_EP)
    Call<RW<SimpleComposition[], PagedMeta>> sheetListTrendingQuery(
            @QueryMap Map<String, String> order,
            @Query("days") int days,
            @Query("results") int sheetCount);

    /* Comment */

    @GET(COMMENT_EP)
    Call<RW<Comment[], DetailMeta>> getComment(@Query("sheetmusicId") Integer sheetId);

    @POST(COMMENT_EP)
    Call<RW<Comment[], DetailMeta>> commentAdd(@Body CommentPost comment);

    @DELETE(COMMENT_EP + "{id}")
    Call<RW<Comment[], DetailMeta>> commentDelete(@Path("id") int commentId);

    @PATCH(COMMENT_EP + "{id}")
    Call<RW<Comment[], DetailMeta>> commentEdit(@Path("id") int commentId, CommentText commentBody);

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

    // @DELETE annotation does not support body, this is a workaround
    @HTTP(method = "DELETE", path = SHELF_EP, hasBody = true)
    Call<RW<Shelf, MetaData>> shelfRemoveSheet(@Body ShelfSheetMusic sheetId);

    /* Profile */

    @GET(PROFILE_EP)
    Call<RW<Profile, DetailMeta>> getProfile(@Query("username") String username);

    @POST(PROFILE_EP)
    Call<RW<Profile, MetaData>> profileUpdateDescription(@Body ProfileDescription description);

    @Multipart
    @POST(PROFILE_EP + "picture/")
    Call<RW<Profile, DetailMeta>> profileUpdatePicture(@Part MultipartBody.Part file);

}

