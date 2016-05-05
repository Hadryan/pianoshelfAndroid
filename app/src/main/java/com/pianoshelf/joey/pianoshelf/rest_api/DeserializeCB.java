package com.pianoshelf.joey.pianoshelf.rest_api;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.authentication.LoginResponse;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by joey on 04/05/16.
 */
public abstract class DeserializeCB<T extends RW> implements Callback<T> {
    
    public abstract void onSuccess(T response);
    public abstract void onInvalid(T response);
    public abstract T convert(String json) throws IOException;
    
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        Log.i(C.NET, "HTTP response code: " + response.code());
        if (response.isSuccessful()) {
            onSuccess(response.body());
        } else {
            try {
                String json = response.errorBody().string();

                // Code Graveyard
                //Log.i(C.NET, new Gson().fromJson(json, new TypeToken<T>() {}.getType()).getClass().toString());
                //Log.i(C.NET, new ObjectMapper().readValue(json, new TypeReference<T>(){}).getClass().toString());


                // due to Java's Type Erasure, convert cannot determine the type of T,
                // hence it must be stated explicitly for each usage of DeserializeCB
                T errorBodyObject = convert(json);

                Log.i(C.NET, errorBodyObject.getClass().toString());
                onInvalid(errorBodyObject);
            } catch (IOException e) {
                // hand off to failure case
                onFailure(call, e);
            }
        }
    }

}
