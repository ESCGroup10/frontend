package com.example.singhealthapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.singhealthapp.HelperClasses.CentralisedToast;
import com.example.singhealthapp.ObjectsFromDatabase.User;
import com.example.singhealthapp.container.AuditorFragmentContainer;
import com.example.singhealthapp.container.TenantFragmentContainer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FirebaseLogin extends AppCompatActivity {

    private static final String TAG = "FirebaseLogin";

    EditText textViewEmail;
    String email;
    EditText textViewPassword;
    String password;
    Button login_button;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    Button auditor;
    Button tenant;

    Call<List<User>> getAllUsersCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textViewEmail = (EditText) findViewById(R.id.login_email);
        textViewPassword = (EditText) findViewById(R.id.login_password);
        login_button = (Button) findViewById(R.id.loginButton);

        auditor = findViewById(R.id.auditorButton);
        tenant = findViewById(R.id.tenantButton);

        auditor.setOnClickListener(v -> {
            Intent intent = new Intent(this, AuditorFragmentContainer.class);
            startActivity(intent);
        });
        tenant.setOnClickListener(v -> {
            Intent intent = new Intent(this, TenantFragmentContainer.class);
            startActivity(intent);
        });

        mAuth = FirebaseAuth.getInstance();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://esc10-303807.et.r.appspot.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // retrofit to create the class from the interface
        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);

        getAllUsersCall = apiCaller.getUser();

        mAuthListener = firebaseAuth -> login_button.setOnClickListener(v -> {
            Log.d(TAG, "onClick: login button clicked!");
            email = textViewEmail.getText().toString();
            password = textViewPassword.getText().toString();
            startLogIn();
            if (firebaseAuth.getCurrentUser() != null) { //if logged in, go to home page
                Log.d(TAG, "onClick: user is authenticated!");
                Toast.makeText(FirebaseLogin.this, "Email and pw correct, attempting to log in", Toast.LENGTH_LONG).show();
                // check if auditor or tenant and go to corresponding page
                checkUserTypeAndEnter(getAllUsersCall);
            } else {
                Log.d(TAG, "onClick: user was not authenticated!");
            }
        });


    }

    private void checkUserTypeAndEnter(Call call) {
        call.clone().enqueue(new Callback<List<com.example.singhealthapp.ObjectsFromDatabase.User>>() {
            @Override
            public void onResponse(Call<List<com.example.singhealthapp.ObjectsFromDatabase.User>> call, Response<List<com.example.singhealthapp.ObjectsFromDatabase.User>> response) {
                if (!response.isSuccessful()) {
                    // TODO 1.5: throw a certain type of response if authentication failed so we can handle it here
                    Log.d(TAG, "Login Ver1 onFailure: "+response.code());
                    CentralisedToast.makeText(FirebaseLogin.this, "Database response:\nError "+response.code(), Toast.LENGTH_SHORT);
                    return;
                }
                Log.d(TAG, "successful response code:"+response.code());
                List<com.example.singhealthapp.ObjectsFromDatabase.User> user_list = response.body();
                // TODO 2: if they match, go to the corresponding fragment
                Intent intent;
                int user_index = -1;
                boolean found = false;
                for (int i=0; i<user_list.size(); i++) {
                    User user = user_list.get(i);
                    Log.d(TAG, "checking if "+user.getEmail()+" == "+email);
                    if (user.getEmail().equals(email) && !found) {
                        Log.d(TAG, "onResponse: checking for user index");
                        user_index = i;
                        found = true;
                    } else if (user.getEmail().equals(email) && found) {
                        CentralisedToast.makeText(FirebaseLogin.this, "Email conflict\nPlease wait for developers to solve this problem", Toast.LENGTH_SHORT);
                        return;
                    }
                }
                if (user_index == -1) {
                    CentralisedToast.makeText(FirebaseLogin.this, "email not found", Toast.LENGTH_SHORT);
                    return;
                }
                User user = user_list.get(user_index);
                Log.d(TAG, "checking if "+user.getType()+" == Auditor");
                if (user.getType().equals("Auditor")) {
                    Log.d(TAG, "onResponse: Auditor recognised");
                    CentralisedToast.makeText(FirebaseLogin.this, "account validated:\ngoing to auditor page", Toast.LENGTH_SHORT);
                    intent = new Intent(FirebaseLogin.this, AuditorFragmentContainer.class);
                } else {
                    Log.d(TAG, "onResponse: Tenant recognised");
                    CentralisedToast.makeText(FirebaseLogin.this, "account validated:\ngoing to tenant page", Toast.LENGTH_SHORT);
                    intent = new Intent(FirebaseLogin.this, TenantFragmentContainer.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                CentralisedToast.makeText(FirebaseLogin.this, "Error retrieving from database", Toast.LENGTH_SHORT);
                Log.d(TAG, "onDatabaseCallFailure: "+t.toString());
            }
        });
    }

    private void startLogIn() {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            CentralisedToast.makeText(this, "Email or password is empty", Toast.LENGTH_SHORT);
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(FirebaseLogin.this, "Email or Password is wrong", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
}
