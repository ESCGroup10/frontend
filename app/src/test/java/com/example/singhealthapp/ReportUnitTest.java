package com.example.singhealthapp;

import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Token;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertEquals;

public class ReportUnitTest {
    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://esc10-303807.et.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);
    String token;

    @Before
    public void login() throws InterruptedException {
        Call<Token> authCall = apiCaller.postLogin("auditor@test.com", "1234");

        authCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                token = response.body().getToken();
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {

            }
        });
        Thread.sleep(3000);
    }

    @Test
    public void mockGetReportTest() throws IOException {

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(201));

        DatabaseApiCaller caller = new Retrofit.Builder()
                .baseUrl(server.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(DatabaseApiCaller.class);

    }

    @Test
    public void getReportTest() throws IOException {

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(201));

        DatabaseApiCaller caller = new Retrofit.Builder()
                .baseUrl(server.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(DatabaseApiCaller.class);

    }
}
