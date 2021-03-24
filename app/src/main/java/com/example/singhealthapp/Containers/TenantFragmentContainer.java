package com.example.singhealthapp.Containers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.singhealthapp.Views.Login.LoginActivity;
import com.example.singhealthapp.Views.TestFragment;
import com.example.singhealthapp.Views.Tenant.LatestReportFragment;
import com.example.singhealthapp.Views.Tenant.MyReportsFragment;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Statistics.StatisticsFragment;
import com.google.android.material.navigation.NavigationView;

public class TenantFragmentContainer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "TenantFragmentContainer";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_fragmentcontainer);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LatestReportFragment()).commit();
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
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Do you want to log out? ");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //AuditorFragmentContainer.super.onBackPressed();
                    dialog.dismiss();
                    clearData(); // clear user type (to avoid auto login) and token (for safety)
                    Intent intent = new Intent(TenantFragmentContainer.this, LoginActivity.class);
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
            case R.id.nav_MyReport:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyReportsFragment()).commit();
                break;

            case R.id.nav_Tenant_Statistics:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StatisticsFragment()).commit();
                break;

            case R.id.nav_LatestReport:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LatestReportFragment()).commit();
                break;

            case R.id.nav_Test:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TestFragment()).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clearData() {
        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("TOKEN_KEY", "");
        editor.putString("USER_TYPE_KEY", "");
        editor.commit();
    }
}