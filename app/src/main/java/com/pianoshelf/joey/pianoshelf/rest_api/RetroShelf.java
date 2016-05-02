package com.pianoshelf.joey.pianoshelf.rest_api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by joey on 29/04/16.
 */
public interface RetroShelf {
    @GET("api/sheetmusic/{id}")
    Call<CompositionJSON> getSheet(@Path("id") int sheetId);

    @Headers("Authorization: TOKEN {authToken}")
    @POST("api/shelf/")
    Call<Void> addSheetToShelf(@Path("authToken") String authToken, @Body ShelfSheetMusic sheetId);
    // TODO check what the api sends back

}
