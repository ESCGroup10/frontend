package com.example.singhealthapp;

import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Token;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(Parameterized.class)
public class LoginUserInputUnitTest {
    public String email, password;
    public int response_code;
    public boolean expected;
    public DatabaseApiCaller apiCaller = new Retrofit.Builder()
            .baseUrl("https://esc10-303807.et.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DatabaseApiCaller.class);

    // classic constructor
    public LoginUserInputUnitTest (int response_code, String email, String password, boolean expected) {
        this.email = email;
        this.password = password;
        this.response_code = response_code;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        // map result to constructor parameters (each inner list corresponds to the inputs to the constructor)
        return Arrays.asList (new Object [][] {
                {200, "auditor@test.com", "1234", true},
                {200, "tenant@test.com", "1234", true},
                {200, "xxxx@test.com", "1234", false},
                {200, "auditor@test.com", "xxxx", false},
                {200, "", "xxxx", false},
                {200, "auditor@test.com", "", false}
        });
    }

    @Test
    public void postLoginTest() throws IOException {
        Call<Token> authCall = apiCaller.postLogin(email, password);
        if (expected) {
            assertEquals(response_code, authCall.execute().code());
        } else {
            assertNotEquals(response_code, authCall.execute().code());
        }
    }
}
