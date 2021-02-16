package com.example.singhealthapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.singhealthapp.container.AuditorFragmentContainer;
import com.example.singhealthapp.container.TenantFragmentContainer;

public class LoginActivity extends AppCompatActivity {

    Button auditor;
    Button tenant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
    }
}