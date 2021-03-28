package com.example.singhealthapp;

import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Token;
import com.example.singhealthapp.Models.User;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginAPIUnitTest {

    // Use mock server to Test api calls (to simulate real response results)
    // ensure test errors are due to code implementation and not backend

    // POST token authentication request to get token
    @Test
    public void mockPostLoginTest() throws IOException { // test if interface works

        MockWebServer server = new MockWebServer();

        String mockTokenJson = "{\"token\": \"127bc352b84eead35cc28340349a8dda9\"}";
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

    // GET user type for auto login (if user has logged in before)
    @Test
    public void mockGetSingleUserTest() throws IOException {

        MockWebServer server = new MockWebServer();

        String mockSingleUserJson = "[{" +
                "\"email\": \"auditor@test.com\", " +
                "\"password\": \"pbkdf2_sha256$216000$Sz\", " +
                "\"id\": \"20\", " +
                "\"name\": \"Emily\", " +
                "\"company\": \"\", " +
                "\"location\": \"\", " +
                "\"institution\": \"SGH\", " +
                "\"type\": \"Auditor\"}]";
        server.enqueue(new MockResponse().setBody(mockSingleUserJson));

        DatabaseApiCaller mockApiCaller = new Retrofit.Builder()
                .baseUrl(server.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);

        String mockToken = "127bc352b84eead35cc28340349a8dda9";
        String mockEmail = "auditor@test.com";

        Call<List<User>> authCall = mockApiCaller.getSingleUser("Token " + mockToken, mockEmail);

        String userType = authCall.execute().body().get(0).getType();

        assertEquals("Auditor", userType);

        server.shutdown();
    }
}
