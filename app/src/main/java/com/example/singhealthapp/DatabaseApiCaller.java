package com.example.singhealthapp;

import com.example.singhealthapp.ObjectsFromDatabase.LoginInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface DatabaseApiCaller {

    // get a list of all users (auditors + tenants)
    @GET("/api/users/?format=json")
    Call<List<User>> getUsers();

    // post details of new user i.e. add a new tenant/auditor to the database
    @FormUrlEncoded
    @POST("/api/users/?format=json")
    Call<User> postNewUser(
            @Field("name") String name,
            @Field("company") String company,
            @Field("email") String email,
            @Field("location") String location,
            @Field("institution") String institution,
            @Field("type") String type
    );

    // get the details of a single user
    @GET("users/")
    Call<List<com.example.singhealthapp.ObjectsFromDatabase.User>> getUser();

    // get authentication of a single user
    @GET("auth/")
    Call<LoginInfo> authenticate(@Header("Authorization") String authHeader);
}
