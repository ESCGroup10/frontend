package com.example.singhealthapp;

import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Token;
import com.example.singhealthapp.Models.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginUnitTest {

    DatabaseApiCaller apiCaller = new Retrofit.Builder()
            .baseUrl("https://esc10-303807.et.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DatabaseApiCaller.class);

    // test api call using mock server (to simulate real response results)
    @Test
    public void mockPostLoginTest() throws IOException { // test if interface works
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

    @Test
    public void mockGetSingleUserTest() throws IOException {
        String mockSingleUserJson = "[{" +
                "\"email\": \"auditor@test.com\", " +
                "\"password\": \"pbkdf2_sha256$216000$Sz\", " +
                "\"id\": \"20\", " +
                "\"name\": \"Emily\", " +
                "\"company\": \"\", " +
                "\"location\": \"\", " +
                "\"institution\": \"SGH\", " +
                "\"type\": \"Auditor\"}]";
        String mockToken = "127bc352b84eead35cc28340349a8dda9";
        String mockEmail = "auditor@test.com";

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(mockSingleUserJson));

        DatabaseApiCaller mockApiCaller = new Retrofit.Builder()
                .baseUrl(server.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);

        Call<List<User>> authCall = mockApiCaller.getSingleUser("Token " + mockToken, mockEmail);

        String userType = authCall.execute().body().get(0).getType();

        assertEquals("Auditor", userType);

        server.shutdown();
    }
//
//    @Test
//    public void mockTimeoutTest () throws IOException {
//
//        MockWebServer server = new MockWebServer();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(server.url("").toString())
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        MockResponse response = new MockResponse()
//                .addHeader("Content-Type", "application/json; charset=utf-8")
//                .addHeader("Cache-Control", "no-cache")
//                .setBody("{}");
//        response.throttleBody(1024, 1, TimeUnit.SECONDS);
//
//        server.enqueue(response);
//        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);
//        Call<Token> authCall = apiCaller.postLogin("auditor@test.com", "1234");
//
//        try {
//            String token = authCall.execute().body().getToken();
//        } finally {
//
//        }
//
//        assert(authCall.isExecuted()).onLoadFailed(IOException.class);
//    }

    // testing REST API with live Integration Tests (with JSON payload)
    @Test
    public void correctAuditorTest() throws IOException {
        Call<Token> authCall = apiCaller.postLogin("auditor@test.com", "1234");
        assertEquals(200, authCall.execute().code());
    }

    @Test
    public void correctTenantTest() throws IOException {
        Call<Token> authCall = apiCaller.postLogin("tenant@test.com", "1234");
        assertEquals(200, authCall.execute().code());
    }

    @Test
    public void wrongEmailTest() throws IOException {
        Call<Token> authCall = apiCaller.postLogin("xxxx@test.com", "1234");
        assertNotEquals(200, authCall.execute().code());
    }

    @Test
    public void wrongPasswordTest() throws IOException {
        Call<Token> authCall = apiCaller.postLogin("auditor@test.com", "xxxx");
        assertNotEquals(200, authCall.execute().code());
    }

    @Test
    public void emptyEmailTest() throws IOException {
        Call<Token> authCall = apiCaller.postLogin("", "xxxx");
        assertNotEquals(200, authCall.execute().code());
    }

    @Test
    public void emptyPasswordTest() throws IOException {
        Call<Token> authCall = apiCaller.postLogin("auditor@test.com", "");
        assertNotEquals(200, authCall.execute().code());
    }

}
