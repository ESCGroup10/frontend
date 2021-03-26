package com.example.singhealthapp;

import android.widget.Toast;

import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Token;
import com.example.singhealthapp.Models.User;
import com.example.singhealthapp.Views.Login.LoginActivity;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import static org.junit.Assert.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddTenantUnitTest {
    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://esc10-303807.et.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);
    String mockToken = "127bc352b84eead35cc28340349a8dda9";
    Token token;

    @Before
    public void login() throws InterruptedException {
        Call<Token> authCall = apiCaller.postLogin("auditor@test.com", "1234");

        authCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                    token = response.body();
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {

            }
        });
        Thread.sleep(3000);
    }


    @Test
    public void mockAddTenantTest() throws IOException{

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(201));

        DatabaseApiCaller caller = new Retrofit.Builder()
                .baseUrl(server.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(DatabaseApiCaller.class);
        Call<Void> call = caller.postUser("Token " + mockToken, generateField(true));
        assertEquals(201, call.execute().code());
        server.shutdown();
    }

    @Test
    public void correctAddTenantTest() throws IOException {
        Call<Void> call = apiCaller.postUser("Token " + token.getToken(), generateField(true));
        assertEquals(201, call.execute().code());
    }

    @Test
    public void wrongTokenAddTenantTest() throws IOException {
        Call<Void>  call = apiCaller.postUser("Token " + token.getToken(), generateField(true));
        assertNotEquals(200, call.execute().code());
    }

    @Test
    public void wrongFieldAddTenantTest() throws IOException {
        Call<Void>  call = apiCaller.postUser("Token " + token.getToken(), generateField(false));
        assertNotEquals(200, call.execute().code());
    }

    static String generateString(int length){
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    static Map<String, String> generateField(boolean legal){
        Map<String, String> fields = new HashMap<>();
        if ( legal ) {
            fields.put("email", generateString(8) + "@test.com");
            fields.put("password", generateString(8));
            fields.put("name", generateString(8));
            fields.put("company", generateString(8));
            fields.put("location", generateString(8));
            fields.put("type", "F&B");
            fields.put("institution", generateString(8));
        }
        else{
            fields.put("email", "@test.com");
            fields.put("password", "");
            fields.put("name", generateString(8));
            fields.put("company", generateString(8));
            fields.put("location", generateString(8));
            fields.put("type", "falseF&B");
            fields.put("institution", generateString(8));
        }
        return fields;
    }

    static class Code {
        static final int successCode = 401;
    }
}
