package com.example.singhealthapp.auditor;

import com.example.singhealthapp.Tenant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterfaceTenant {
    @GET("/api/users/")
    Call<List<Tenant>> getTenants();
}