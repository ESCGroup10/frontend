package com.example.singhealthapp;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DatabaseApiCaller {

    @GET("/api/users/?format=json")
    Call<List<User>> getUsers();

    @FormUrlEncoded
    @POST("/api/users/?format=json")
    Call<User> postUser (@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("posts")
    Call<User> postTest (@FieldMap Map<String, String> fields);

}
