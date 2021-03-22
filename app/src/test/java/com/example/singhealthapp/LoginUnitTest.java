package com.example.singhealthapp;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginUnitTest {

    // test api call using mock server (to simulate real response results)
    @Test
    public void mockPostLoginTest() throws IOException { // test if interface works
        System.out.println("mockAuthCallTest starting...");

        String mockTokenJson = "{\"token\": \"127bc352b84eead35cc28340349a8dda9\"}";

        // Create a MockWebServer. These are lean enough that you can create a new
        // instance for every unit test.
        MockWebServer server = new MockWebServer();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        server.enqueue(new MockResponse().setBody(mockTokenJson));

        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);

        Call<Token> authCall = apiCaller.postLogin("auditor@test.com", "1234");

        String token = authCall.execute().body().getToken();
        System.out.println("Token: " + token);
        assertEquals("127bc352b84eead35cc28340349a8dda9", token);

        // Shut down the server. Instances cannot be reused.
        server.shutdown();
    }

    @Test
    public void mockGetSingleUserTest() throws IOException {
        System.out.println("mockAuthCallTest starting...");

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

        // Create a MockWebServer. These are lean enough that you can create a new
        // instance for every unit test.
        MockWebServer server = new MockWebServer();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        server.enqueue(new MockResponse().setBody(mockSingleUserJson));

        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);

        Call<List<User>> authCall = apiCaller.getSingleUser("Token " + mockToken, mockEmail);

        String userType = authCall.execute().body().get(0).getType();

        System.out.println("User Type: " + userType);
        assertEquals("Auditor", userType);

        // Shut down the server. Instances cannot be reused.
        server.shutdown();
    }
//
//    @Test
//    public void mockTimeoutTest () {
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
        DatabaseApiCaller apiCaller = new Retrofit.Builder()
                .baseUrl("https://esc10-303807.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);

        Call<Token> authCall = apiCaller.postLogin("auditor@test.com", "1234");
        assertEquals(200, authCall.execute().code());
    }

    @Test
    public void correctTenantTest() throws IOException {
        DatabaseApiCaller apiCaller = new Retrofit.Builder()
                .baseUrl("https://esc10-303807.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);

        Call<Token> authCall = apiCaller.postLogin("tenant@test.com", "1234");
        assertEquals(200, authCall.execute().code());
    }

    @Test
    public void wrongEmailTest() throws IOException {
        DatabaseApiCaller apiCaller = new Retrofit.Builder()
                .baseUrl("https://esc10-303807.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);

        Call<Token> authCall = apiCaller.postLogin("xxxx@test.com", "1234");
        assertNotEquals(200, authCall.execute().code());
    }

    @Test
    public void wrongPasswordTest() throws IOException {
        DatabaseApiCaller apiCaller = new Retrofit.Builder()
                .baseUrl("https://esc10-303807.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);

        Call<Token> authCall = apiCaller.postLogin("auditor@test.com", "xxxx");
        assertNotEquals(200, authCall.execute().code());
    }

    @Test
    public void emptyEmailTest() throws IOException {
        DatabaseApiCaller apiCaller = new Retrofit.Builder()
                .baseUrl("https://esc10-303807.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);

        Call<Token> authCall = apiCaller.postLogin("", "xxxx");
        assertNotEquals(200, authCall.execute().code());
    }

    @Test
    public void emptyPasswordTest() throws IOException {
        DatabaseApiCaller apiCaller = new Retrofit.Builder()
                .baseUrl("https://esc10-303807.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);

        Call<Token> authCall = apiCaller.postLogin("auditor@test.com", "");
        assertNotEquals(200, authCall.execute().code());
    }
}
