package com.example.singhealthapp;

import android.util.Log;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
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

    // testing REST API with live Integration Tests (with JSON payload)

}
