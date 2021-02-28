package com.example.singhealthapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.singhealthapp.HelperClasses.CentralisedToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ResetPasswordActivity";

    EditText emailEditText;
    Button resetPasswordButton;
    Button backButton;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        resetPasswordButton = findViewById(R.id.confirmResetPasswordButton);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(ResetPasswordActivity.this, FirebaseLogin.class);
                startActivity(intent);
            }
        });

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (isEmailRegistered(emailEditText.getText().toString())) {
                        mAuth.sendPasswordResetEmail(emailEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener() {

                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    CentralisedToast.makeText(ResetPasswordActivity.this,
                                            "Reset email instructions sent to " + emailEditText.getText().toString(), CentralisedToast.LENGTH_LONG);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "sendPasswordResetEmail failure: ", e);
                                }
                            });
                    }
                }
        });

    }

    public boolean isEmailRegistered(String email) {
        return true;
    }
}


//                        mAuth.confirmPasswordReset(codeEditText.toString(), newPasswordEditText.getText().toString())
//                        .addOnCompleteListener(new OnCompleteListener() {
//
//                            @Override
//                            public void onComplete(@NonNull Task task) {
//                                if (task.isSuccessful()) {
//                                    CentralisedToast.makeText(ResetPasswordActivity.this,
//                                            "Password has been reset!", CentralisedToast.LENGTH_SHORT);
//                                }
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.e(TAG, "confirmPasswordReset failure: ", e);
//                                CentralisedToast.makeText(ResetPasswordActivity.this,
//                                        "Error occurred while sending email to " + emailEditText.getText().toString() +
//                                                ". Please try again.", CentralisedToast.LENGTH_LONG);
//                            }
//                        });