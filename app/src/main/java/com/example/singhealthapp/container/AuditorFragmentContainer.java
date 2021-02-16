package com.example.singhealthapp.container;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.singhealthapp.tenant.LatestReportFragment;
import com.example.singhealthapp.R;
import com.example.singhealthapp.auditor.ReportsFragment;
import com.example.singhealthapp.StatisticsFragment;
import com.example.singhealthapp.auditor.TenantsFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

public class AuditorFragmentContainer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout auditor_drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditor_fragment_container);

        Toolbar auditor_toolbar = findViewById(R.id.auditor_toolbar);
        setSupportActionBar(auditor_toolbar);

        auditor_drawer = findViewById(R.id.auditor_drawer_layout);
        NavigationView navigationView = findViewById(R.id.auditor_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, auditor_drawer, auditor_toolbar, R.string.drawer_open, R.string.drawer_close);
        auditor_drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new TenantsFragment()).commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (auditor_drawer.isDrawerOpen(GravityCompat.START)) {
            auditor_drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_Auditor_Statistics:
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new StatisticsFragment()).commit();
                break;

            case R.id.nav_Tenants:
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new TenantsFragment()).commit();
                break;

            case R.id.nav_Reports:
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new ReportsFragment()).commit();
                break;
        }

        auditor_drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}