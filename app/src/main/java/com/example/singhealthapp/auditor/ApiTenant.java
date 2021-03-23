package com.example.singhealthapp.auditor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiTenant {
    public static String BASE_URL ="https://esc10-303807.et.r.appspot.com/api/";
    private static Retrofit retrofit;
    public static Retrofit getTenant(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}


