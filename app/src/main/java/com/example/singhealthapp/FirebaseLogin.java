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

import com.example.singhealthapp.DatabaseApi.DjangoApi;
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

    // firebase auth objects
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    // call object to interact with the django api
    Call<List<User>> getAllUsersCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textViewEmail = (EditText) findViewById(R.id.login_email);
        textViewPassword = (EditText) findViewById(R.id.login_password);
        login_button = (Button) findViewById(R.id.loginButton);

        mAuth = FirebaseAuth.getInstance();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://esc10-303807.et.r.appspot.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // retrofit will create the class from the interface named DjangoApi
        DjangoApi djangoApi = retrofit.create(DjangoApi.class);

        // makes use of User class to get information from api
        getAllUsersCall = djangoApi.getUser();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) { //if logged in, go to home page
                    Log.d(TAG, "onClick: user is authenticated!");
                    // check if auditor or tenant and go to corresponding page
                    checkUserTypeAndEnter(getAllUsersCall);
                } else {
                    Log.d(TAG, "onClick: user was not authenticated!");
                }

            }
        };

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: login button clicked!");
                email = textViewEmail.getText().toString();
                password = textViewPassword.getText().toString();
                startLogIn();
            }
        });




    }

    /*
    Validates email with database and retrieves user type (auditor or tenant)

    * 1. Retrieves all User information from api
    * 2. Checks for matching email to validate
    * 3. Decides if user is an auditor or tenant
    * 4. Creates intent to corresponding fragment
    * */


    private void checkUserTypeAndEnter(Call call) {
        // Retrieves all User information from api on worker process
        call.clone().enqueue(new Callback<List<com.example.singhealthapp.ObjectsFromDatabase.User>>() {
            @Override
            public void onResponse(Call<List<com.example.singhealthapp.ObjectsFromDatabase.User>> call,
                                   Response<List<com.example.singhealthapp.ObjectsFromDatabase.User>> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "error getting info from database: "+response.code());
                    CentralisedToast.makeText(FirebaseLogin.this,
                            "Database response:\nError "+response.code(), Toast.LENGTH_SHORT);
                    return;
                }
                Log.d(TAG, "got into database response code:"+response.code());
                List<com.example.singhealthapp.ObjectsFromDatabase.User> user_list = response.body(); // get list of user info from the response

                Intent intent;
                int user_index = -1; // initialize as -1 to represent no email match
                boolean found = false; // one email match was found
                for (int i=0; i<user_list.size(); i++) { // iterate through all the registered emails on the database to match with the user's email
                    User user = user_list.get(i);
                    Log.d(TAG, "checking if "+user.getEmail()+" == "+email);
                    if (user.getEmail().equals(email) && !found) {
                        Log.d(TAG, "found an email match!");
                        user_index = i;
                        found = true;
                    } else if (user.getEmail().equals(email) && found) { // if more than one email matches, deny login
                        Log.e(TAG, "found more than one email match!");
                        CentralisedToast.makeText(FirebaseLogin.this,
                                "Email conflict\nPlease wait for developers to solve this problem",
                                Toast.LENGTH_LONG);
                        return;
                    }
                }
                if (user_index == -1) { // if no email match was found, deny login
                    Log.d(TAG, "onResponse: email was validated by firebase but not found in database");
                    return;
                }

                // retrieve user type from database and go to the corresponding page
                User user = user_list.get(user_index);
                if (user.getType().toLowerCase().equals("Auditor".toLowerCase())) {
                    Log.d(TAG, "onResponse: Auditor recognised, logging in");
                    intent = new Intent(FirebaseLogin.this, AuditorFragmentContainer.class);
                } else if (user.getType().toLowerCase().equals("F&B".toLowerCase()) || user.getType().toLowerCase().equals("non F&B".toLowerCase())) {
                    Log.d(TAG, "onResponse: Tenant recognised, logging in");
                    intent = new Intent(FirebaseLogin.this, TenantFragmentContainer.class);
                } else {
                    Log.d(TAG, "User type: "+user.getType()+" does not fit either auditor nor tenant, denying login...");
                    CentralisedToast.makeText(FirebaseLogin.this,
                            "account type invalid, please contact developer.", Toast.LENGTH_LONG);
                    return;
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // so that user does not have to keep logging in
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                CentralisedToast.makeText(FirebaseLogin.this,
                        "Error retrieving from database, please try again.", Toast.LENGTH_SHORT);
                Log.d(TAG, "onDatabaseCallFailure: "+t.toString());
            }
        });
    }

    private void startLogIn() {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            CentralisedToast.makeText(this,
                    "Email or password is empty", Toast.LENGTH_SHORT);
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        CentralisedToast.makeText(FirebaseLogin.this,
                                "Email or Password is wrong. Please try again.", Toast.LENGTH_SHORT);
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
