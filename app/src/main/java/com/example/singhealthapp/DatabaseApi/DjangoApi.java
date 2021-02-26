package com.example.singhealthapp.DatabaseApi;

import com.example.singhealthapp.ObjectsFromDatabase.LoginInfo;
import com.example.singhealthapp.ObjectsFromDatabase.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface DjangoApi {

    @GET("users/")
    Call<List<User>> getUser();

    @GET("auth/")
    Call<LoginInfo> authenticate(@Header("Authorization") String authHeader);


}
