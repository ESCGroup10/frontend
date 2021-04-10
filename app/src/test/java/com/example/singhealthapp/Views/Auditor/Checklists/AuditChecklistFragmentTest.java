package com.example.singhealthapp.Views.Auditor.Checklists;

import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Token;

import org.junit.Test;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.*;

public class AuditChecklistFragmentTest {

    DatabaseApiCaller apiCaller = new Retrofit.Builder()
            .baseUrl("https://esc10-303807.et.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DatabaseApiCaller.class);

    @Test
    public void mockPostNewReportTest() throws IOException { // test if interface works
        String mockTokenJson = "{\"token\": \"127bc352b84eead35cc28340349a8dda9\"}";

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(mockTokenJson));

        DatabaseApiCaller mockApiCaller = new Retrofit.Builder()
                .baseUrl(server.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);

        Call<Token> authCall = mockApiCaller.postLogin("auditor@test.com", "1234");

        String token = authCall.execute().body().getToken();
        assertEquals("127bc352b84eead35cc28340349a8dda9", token);

        // Shut down the server. Instances cannot be reused.
        server.shutdown();
    }
}