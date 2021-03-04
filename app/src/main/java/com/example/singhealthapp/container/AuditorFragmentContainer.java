package com.example.singhealthapp.container;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.singhealthapp.FirebaseLogin;
import com.example.singhealthapp.R;
import com.example.singhealthapp.TestFragment;
import com.example.singhealthapp.auditor.AddTenantFragment;
import com.example.singhealthapp.auditor.ReportsFragment;
import com.example.singhealthapp.StatisticsFragment;
import com.example.singhealthapp.auditor.SearchTenantFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

public class AuditorFragmentContainer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "AuditorFragmentContain";

    DrawerLayout auditor_drawer;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditor_fragment_container);

        mAuth = FirebaseAuth.getInstance();

        Toolbar auditor_toolbar = findViewById(R.id.auditor_toolbar);
        setSupportActionBar(auditor_toolbar);

        auditor_drawer = findViewById(R.id.auditor_drawer_layout);
        NavigationView navigationView = findViewById(R.id.auditor_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, auditor_drawer, auditor_toolbar, R.string.drawer_open, R.string.drawer_close);
        auditor_drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new SearchTenantFragment()).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        if (auditor_drawer.isDrawerOpen(GravityCompat.START)) {
            auditor_drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Do you want to log out? ");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //AuditorFragmentContainer.super.onBackPressed();
                    dialog.dismiss();
                    mAuth.signOut();
                    Intent intent = new Intent(AuditorFragmentContainer.this, FirebaseLogin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    //finish();
                }
            });
            builder.show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_Auditor_Statistics:
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new StatisticsFragment()).commit();
                break;

            case R.id.nav_Tenants:
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new SearchTenantFragment()).commit();
                break;

            case R.id.nav_Reports:
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new ReportsFragment()).commit();
                break;

            case R.id.nav_Add_Tenant:
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new AddTenantFragment()).commit();
                break;

            case R.id.nav_Test:
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new TestFragment()).commit();
                break;
        }

        auditor_drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}