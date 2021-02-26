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

}
