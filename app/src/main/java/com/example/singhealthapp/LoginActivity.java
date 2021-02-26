package com.example.singhealthapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.singhealthapp.DatabaseApi.DjangoApi;
import com.example.singhealthapp.HelperClasses.CentralisedToast;
import com.example.singhealthapp.ObjectsFromDatabase.LoginInfo;
import com.example.singhealthapp.ObjectsFromDatabase.User;
import com.example.singhealthapp.container.AuditorFragmentContainer;
import com.example.singhealthapp.container.TenantFragmentContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    Button auditor;
    Button tenant;
    EditText textViewUsername;
    String username;
    EditText textViewPassword;
    String password;
    Button login_button;

    Call<List<User>> getUserCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auditor = findViewById(R.id.auditorButton);
        tenant = findViewById(R.id.tenantButton);
        textViewUsername = (EditText) findViewById(R.id.login_username);
        textViewPassword = (EditText) findViewById(R.id.login_password);
        login_button = (Button) findViewById(R.id.loginButton);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://esc10-303807.et.r.appspot.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // retrofit to create the class from the interface
        DjangoApi djangoApi = retrofit.create(DjangoApi.class);

        getUserCall = djangoApi.getUser();

        String base = username + ":"+ password;
        String authHeader = "Salt "+ Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
        Call<LoginInfo> authenticate = djangoApi.authenticate(authHeader);

        auditor.setOnClickListener(v -> {
            Intent intent = new Intent(this, AuditorFragmentContainer.class);
            startActivity(intent);
        });
        tenant.setOnClickListener(v -> {
            Intent intent = new Intent(this, TenantFragmentContainer.class);
            startActivity(intent);
        });
        login_button.setOnClickListener(v -> {
            // TODO 1: handle username and pw
            // TODO 1.1: read username and pw from textviews
            username = textViewUsername.getText().toString();
            password = textViewPassword.getText().toString();
            // TODO 1.2: if username or pw are empty, tell the user
            if (password == "" || password == null) {
                Log.d(TAG, "onCreate: password:"+password.toString());
                CentralisedToast.makeText(LoginActivity.this, "Login Error:\nPassword is empty", Toast.LENGTH_SHORT);
                return;
            }
            // TODO 1.3: if username contains illegal characters etc, tell the user
            // Regex to check valid username.
            String regex = "^[A-Za-z]\\w{4,29}$";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(username);
            if (!m.matches()) {
                // username is illegal
                CentralisedToast.makeText(LoginActivity.this, "Login Error:\nIllegal username detected", Toast.LENGTH_SHORT);
                return;
            }
            // TODO 1.4: check username and pw against database (to be implemented in database)
            else {
                // make call execute on a worker thread by using enqueue
                LoginVer2(authenticate);
            }
        });

    }

    private void LoginVer2(Call call) {
        call.clone().enqueue(new Callback<LoginInfo>() {
            @Override
            public void onResponse(Call<LoginInfo> call, Response<LoginInfo> response) {
                if (!response.isSuccessful()) {
                    // TODO 1.5: throw a certain type of response if authentication failed so we can handle it here
                    Log.d(TAG, "Login Ver2 onFailure: "+response.code());
                    CentralisedToast.makeText(LoginActivity.this, "Database response:\nLogin error, try again", Toast.LENGTH_SHORT);
                    return; // login info is wrong
                }
                // if login info is correct, login
                checkUsernameAndEnter(getUserCall);

            }

            @Override
            public void onFailure(Call<LoginInfo> call, Throwable t) {
                CentralisedToast.makeText(LoginActivity.this, "Error retrieving from database", Toast.LENGTH_SHORT);
                Log.d(TAG, "onDatabaseCallFailure: "+t.toString());
            }
        });
    }

    private void checkUsernameAndEnter(Call call) {
        call.clone().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (!response.isSuccessful()) {
                    // TODO 1.5: throw a certain type of response if authentication failed so we can handle it here
                    Log.d(TAG, "Login Ver1 onFailure: "+response.code());
                    CentralisedToast.makeText(LoginActivity.this, "Database response:\nError "+response.code(), Toast.LENGTH_SHORT);
                    return;
                }
                Log.d(TAG, "successful response code:"+response.code());
                List<User> user_list = response.body();
                Log.d(TAG, "onResponse: example name: "+user_list.get(0).getName());
                // TODO 2: if they match, go to the corresponding fragment
                Intent intent;
                int user_index = -1;
                boolean found = false;
                for (int i=0; i<user_list.size(); i++) {
                    User user = user_list.get(i);
                    Log.d(TAG, "checking if "+user.getName()+" == "+username);
                    if (user.getName().equals(username) && !found) {
                        Log.d(TAG, "onResponse: checking for user index");
                        user_index = i;
                        found = true;
                    } else if (user.getName().equals(username) && found) {
                        Log.e(TAG, "There are 2 identical usernames! Deal with this pls");
                        CentralisedToast.makeText(LoginActivity.this, "Username conflict\nPlease wait for developers to solve this problem", Toast.LENGTH_SHORT);
                        return;
                    }
                }
                if (user_index == -1) {
                    CentralisedToast.makeText(LoginActivity.this, "username not found", Toast.LENGTH_SHORT);
                    return;
                }
                User user = user_list.get(user_index);
                Log.d(TAG, "checking if "+user.getType()+" == Auditor");
                if (user.getType().equals("Auditor")) {
                    Log.d(TAG, "onResponse: Auditor recognised");
                    CentralisedToast.makeText(LoginActivity.this, "username correct:\ngoing to auditor page", Toast.LENGTH_SHORT);
                    intent = new Intent(LoginActivity.this, AuditorFragmentContainer.class);
                } else {
                    Log.d(TAG, "onResponse: Tenant recognised");
                    CentralisedToast.makeText(LoginActivity.this, "username correct:\ngoing to tenant page", Toast.LENGTH_SHORT);
                    intent = new Intent(LoginActivity.this, TenantFragmentContainer.class);
                }
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                CentralisedToast.makeText(LoginActivity.this, "Error retrieving from database", Toast.LENGTH_SHORT);
                Log.d(TAG, "onDatabaseCallFailure: "+t.toString());
            }
        });
    }
}