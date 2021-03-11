package com.example.singhealthapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.singhealthapp.HelperClasses.CentralisedToast;
import com.example.singhealthapp.container.AuditorFragmentContainer;
import com.example.singhealthapp.container.TenantFragmentContainer;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    EditText textViewEmail, textViewPassword;
    Button login_button, auditorBtn, tenantBtn;

    String email, password;
    Call<List<User>> getUserCall;

    Retrofit retrofit;
    DatabaseApiCaller apiCaller;

    Token token;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // create an api caller to the webserver
        retrofit = new Retrofit.Builder()
                .baseUrl("https://esc10-303807.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiCaller = retrofit.create(DatabaseApiCaller.class);

        textViewEmail = findViewById(R.id.login_email);
        textViewPassword = findViewById(R.id.login_password);

        login_button = findViewById(R.id.loginButton);
        login_button.setOnClickListener(v -> {
            email = textViewEmail.getText().toString();
            password = textViewPassword.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                CentralisedToast.makeText(this,
                        "Email or password is empty", CentralisedToast.LENGTH_LONG);
            } else {
                authenticate();
            }
        });

        setUpNavBtns();
    }


    private void authenticate() {
        Call<Token> loginCall = apiCaller.postLogin(email, password);

        // make a call to post a new User to the database
        loginCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {

                if (response.code() == 200) {
                    token = response.body();
                    login();
                } else {
                    CentralisedToast.makeText(LoginActivity.this, "Email or Password is wrong. Please try again.", CentralisedToast.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                CentralisedToast.makeText(LoginActivity.this,
                        "Error: " + t, CentralisedToast.LENGTH_LONG);
            }
        });
    }

    private void login() {
        getUserCall = apiCaller.getSingleUser("Token " + token.getToken(), email);

        getUserCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> user = response.body();

                saveData(user.get(0).getId());

                Intent intent;
                if (user.get(0).getType().equals("Auditor")) {
                    intent = new Intent(LoginActivity.this, AuditorFragmentContainer.class);
                } else {
                    intent = new Intent(LoginActivity.this, TenantFragmentContainer.class);
                }
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                CentralisedToast.makeText(LoginActivity.this,
                        "Error: " + t, CentralisedToast.LENGTH_LONG);
            }
        });
    }

    private void saveData(int userId) {
        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("TOKEN_KEY", token.getToken());
        editor.putInt("USER_ID_KEY", userId);
        editor.commit();
    }

    private void setUpNavBtns() {
        auditorBtn = findViewById(R.id.auditorButton);
        auditorBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, AuditorFragmentContainer.class);
            startActivity(intent);
        });

        tenantBtn = findViewById(R.id.tenantButton);
        tenantBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, TenantFragmentContainer.class);
            startActivity(intent);
        });
    }
}
