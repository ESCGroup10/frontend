package com.example.singhealthapp.HelperClasses;

import android.annotation.SuppressLint;

import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Token;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiHelper {
//    private static DatabaseApiCaller apiCaller;
    public static String token;
    public final Object lock = new Object();

    public void getToken(DatabaseApiCaller apiCaller, String email, String password) throws InterruptedException {
        Call<Token> authCall = apiCaller.postLogin(email, password);

        // make a call to post a new User to the database
        authCall.enqueue(new Callback<Token>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                synchronized (lock) {
                    if (response.code() == 200) { // response code is valid
                        token = response.body().getToken();
                        lock.notifyAll();
                    }
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                synchronized (lock) {
                    t.printStackTrace();
                    lock.notifyAll();
                }
            }
        });
    }

}
