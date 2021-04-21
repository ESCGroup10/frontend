package com.example.singhealthapp.Views.Login;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.singhealthapp.Containers.AuditorFragmentContainer;
import com.example.singhealthapp.Containers.TenantFragmentContainer;
import com.example.singhealthapp.HelperClasses.CentralisedToast;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Token;
import com.example.singhealthapp.Models.User;
import com.example.singhealthapp.R;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText textViewEmail, textViewPassword;
    private Button login_button;

    String email, password;
    int resetCount;
    Call<List<User>> getUserCall;

    DatabaseApiCaller apiCaller;

    private Token token;
    private String userType;
    private int userId;
    private String company;
    private String institution;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // How the 3 types are defined in the database
    private final String auditorType = "Auditor";
    private final String tenantTypeFB = "F&B";
    private final String tenantTypeNFB = "Non F&B";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.a_login);

        autoLogin(); // try to login automatically

        // create an api caller to the webserver
        apiCaller = new Retrofit.Builder()
                .baseUrl("https://esc10-303807.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);

        setUpView();
    }

    // try to navigate to home page immediately using data in SharedPreferences
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void autoLogin() {
        loadUserType(); // load userType from SharedPreferences for auto login

        if (Objects.nonNull(userType)) { // if the userType can be loaded from SharedPreferences (which means user has logged in before)
            Intent intent;
            if (userType.equals(auditorType)) { // check user type and log in
                intent = new Intent(LoginActivity.this, AuditorFragmentContainer.class);
                startActivity(intent);
            } else if (userType.equals("F&B") || userType.equals("Non F&B")) {
                intent = new Intent(LoginActivity.this, TenantFragmentContainer.class);
                startActivity(intent);
            }
        }
    }

    // get token of the user via login post request
    private void authenticate() {
        Call<Token> authCall = apiCaller.postLogin(email, password);

        // make a call to post a new User to the database
        authCall.enqueue(new Callback<Token>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.code() == 200) { // response code is valid
                    token = response.body();
                    getUserInfo(); // check whether user is auditor or tenant to navigate to correct page
                } else {
                    if (resetCount != 4) {
                        resetCount++;
                        CentralisedToast.makeText(LoginActivity.this,
                                String.format("You have entered the wrong password %d times. You have %d tries left.", resetCount, 5-resetCount),
                                CentralisedToast.LENGTH_SHORT);
                    } else {
                        disableLogin();
                    }
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                if (t.getMessage().contains("failed to connect")) {
                    CentralisedToast.makeText(LoginActivity.this,
                            "Login Error: Timeout after 10s \nPlease try again later", CentralisedToast.LENGTH_LONG);
                } else {
                    System.out.println("Error: " + t);
                    CentralisedToast.makeText(LoginActivity.this,
                            "Error: " + t, CentralisedToast.LENGTH_LONG);
                }
            }
        });
    }

    // disable login for 10s if user fails login 5 times
    private void disableLogin() {
        login_button.setEnabled(false); // disable login button
        CentralisedToast.makeText(LoginActivity.this, "You have entered the wrong password 5 times. Please wait 10s to retry.", CentralisedToast.LENGTH_LONG);
        resetCount = 0;

        // schedule function to enable login after 10s
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            login_button.setEnabled(true); // enable the login button

        }, 10000); // 10s
    }

    // navigate to the next page based on the user type (auditor or tenant)
    private void getUserInfo() {
        getUserCall = apiCaller.getSingleUser("Token " + token.getToken(), email);

        getUserCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> user = response.body();

                userId = user.get(0).getId();
                userType = user.get(0).getType();
                if (!userType.equals(auditorType)) {
                    company = user.get(0).getCompany();
                    institution = user.get(0).getInstitution();
                }

                // use async task to save the token so that user can login faster
                new Thread(() -> {
                    saveData(); // save user type, user id and token
                }).start();

                navigate(userType);
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                CentralisedToast.makeText(LoginActivity.this,
                        "Error: " + t, CentralisedToast.LENGTH_LONG);
            }
        });
    }

    // navigate to home page
    private void navigate(String userType) {
        Intent intent;
        if (userType.equals(auditorType)) { // if user logged in is Auditor, move to Auditor page
            intent = new Intent(LoginActivity.this, AuditorFragmentContainer.class);
            startActivity(intent);
        } else if (userType.equals(tenantTypeFB) || userType.equals(tenantTypeNFB)) { // else if user logged in is F&B or Non F&B, move to Tennat page
            intent = new Intent(LoginActivity.this, TenantFragmentContainer.class);
            startActivity(intent);
        } else { // else this user is not a valid user type!
            CentralisedToast.makeText(LoginActivity.this,
                    "Invalid User Type! Please contact the administrator.", CentralisedToast.LENGTH_LONG);
        }

    }

    // load the userType of the user for auto log in as long as the user never logs out
    private void loadUserType() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        userType = sharedPreferences.getString("USER_TYPE_KEY", null);
    }

    // save token, user id and user type
    private void saveData() {
        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("TOKEN_KEY", token.getToken());
        editor.putInt("USER_ID_KEY", userId);
        editor.putString("USER_TYPE_KEY", userType);
        if (!userType.equals(auditorType)) {
            editor.putString("COMPANY_KEY", company);
            editor.putString("INSTITUTION_KEY", institution);
        }
        editor.commit();
    }

    // set up the extra auditor and tenant cheat buttons
    private void setUpView() {
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
    }
}