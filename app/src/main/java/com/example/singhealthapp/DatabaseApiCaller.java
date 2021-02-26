package com.example.singhealthapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DatabaseApiCaller {

    @GET("/api/users/?format=json")
    Call<List<User>> getUsers();

    @FormUrlEncoded
    @POST("/api/users/?format=json")
    Call<User> postUser(
            @Field("password") String password,
            @Field("name") String name,
            @Field("company") String company,
            @Field("location") String location,
            @Field("institution") String institution,
            @Field("type") String type
    );
}
