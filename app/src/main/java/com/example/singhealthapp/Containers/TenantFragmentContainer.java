package com.example.singhealthapp.Containers;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.HelperClasses.IOnBackPressed;
import com.example.singhealthapp.HelperClasses.Ping;
import com.example.singhealthapp.HelperClasses.ProcessMainClass;
import com.example.singhealthapp.HelperClasses.RestartServiceBroadcastReceiver;
import com.example.singhealthapp.HelperClasses.SendInfoToPMC;
import com.example.singhealthapp.Views.Login.LoginActivity;
import com.example.singhealthapp.Views.Tenant.LatestReportFragment;
import com.example.singhealthapp.Views.Tenant.MyReportsFragment;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Statistics.StatisticsFragment;
import com.google.android.material.navigation.NavigationView;

public class TenantFragmentContainer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Ping,
        SendInfoToPMC {

    private static final String TAG = "TenantFragmentContainer";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static int tenantID = -2;
    public static String token = null;

    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.a_fragmentcontainer_tenant);
        loadFromSharedPref();
        startBackgroundNotificationProcess();

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.tenant_drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
//            EspressoCountingIdlingResource.increment();
            LatestReportFragment latestReportFragment = new LatestReportFragment();
            String tag = latestReportFragment.getClass().getName();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, latestReportFragment, tag)
                    .addToBackStack(tag)
                    .commit();
        }
    }

    public void handleIntentFromNotification() {
        if (getIntent().getExtras() != null) {
            boolean fromNotification = getIntent().getBooleanExtra("FROM_NOTIFICATION", false);
            Log.d(TAG, "handleIntentFromNotification: extras were found, fromNotification: "+fromNotification);

            if (fromNotification) {
                EspressoCountingIdlingResource.increment();
                Log.d(TAG, "handleIntentFromNotification: going to my reports");
                MyReportsFragment myReportsFragment = new MyReportsFragment();
                String tag = myReportsFragment.getClass().getName();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myReportsFragment, tag)
                        .addToBackStack(tag)
                        .commit();
            }
        } else {
            Log.d(TAG, "handleIntentFromNotification: no extras were found");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");
        EspressoCountingIdlingResource.increment();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Do you want to log out? ");
                builder.setPositiveButton("OK", (dialog, id) -> {
                    stopBackgroundNotificationProcess();
                    dialog.dismiss();
                    clearData(); // clear user type (to avoid auto login) and token (for safety)
                    Intent intent = new Intent(TenantFragmentContainer.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
                builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
                builder.show();
            }

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        EspressoCountingIdlingResource.increment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        String currentTag;
        String newTag;
        if (fragmentManager.getBackStackEntryCount() == 0) {
            currentTag = "";
        } else {
            currentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
        }
        switch (item.getItemId()) {
            case R.id.nav_MyReport:
                MyReportsFragment myReportsFragment = new MyReportsFragment();
                newTag = myReportsFragment.getClass().getName();
                Log.d(TAG, "onNavigationItemSelected: current tag: "+currentTag);
                Log.d(TAG, "onNavigationItemSelected: new tag: "+newTag);
                if (currentTag.equals(newTag)) {
                    EspressoCountingIdlingResource.decrement();
                    break;
                }
                fragmentManager.beginTransaction().replace(R.id.fragment_container, myReportsFragment, newTag)
                        .addToBackStack(newTag)
                        .commit();
                break;

            case R.id.nav_Tenant_Statistics:
                StatisticsFragment statisticsFragment = new StatisticsFragment();
                newTag = statisticsFragment.getClass().getName();
                Log.d(TAG, "onNavigationItemSelected: current tag: "+currentTag);
                Log.d(TAG, "onNavigationItemSelected: new tag: "+newTag);
                if (currentTag.equals(newTag)) {
                    EspressoCountingIdlingResource.decrement();
                    break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, statisticsFragment, newTag)
                        .addToBackStack(newTag)
                        .commit();
                break;

            case R.id.nav_LatestReport:
                LatestReportFragment latestReportFragment = new LatestReportFragment();
                newTag = latestReportFragment.getClass().getName();
                Log.d(TAG, "onNavigationItemSelected: current tag: "+currentTag);
                Log.d(TAG, "onNavigationItemSelected: new tag: "+newTag);
                if (currentTag.equals(newTag)) {
                    EspressoCountingIdlingResource.decrement();
                    break;
                }
                EspressoCountingIdlingResource.decrement();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, latestReportFragment, newTag)
                        .addToBackStack(newTag)
                        .commit();
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
        editor.putString("OUTLET_KEY", "");
        editor.putString("INSTITUTION_KEY", "");
        editor.apply();
    }

    private void stopBackgroundNotificationProcess() {
        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean("STOP_NOTIFICATIONS", true);
        editor.apply();
        ProcessMainClass.stopBackgroundService((Context)this);
    }

    private void startBackgroundNotificationProcess() {
        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean("STOP_NOTIFICATIONS", false);
        editor.apply();
        ProcessMainClass.stopBackgroundService((Context)this);
    }

    private void loadFromSharedPref() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        tenantID = sharedPreferences.getInt("USER_ID_KEY", -1);
        token = sharedPreferences.getString("TOKEN_KEY", null);
    }

//    private boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                Log.i ("=== isMyServiceRunning?", true+"");
//                return true;
//            }
//        }
//        Log.i ("=== isMyServiceRunning?", false+"");
//        return false;
//    }

//    public void constantlyCheckService() {
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                isMyServiceRunning(NotificationFromBackground.class);
//            }
//        };
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(timerTask, 0, 5000);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
        } else {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(getApplicationContext());
        }
        handleIntentFromNotification();
    }

    @Override
    @VisibleForTesting
    public void activateEspressoIdlingResource() {
        EspressoCountingIdlingResource.activate();
    }

    @Override
    public String sendToken() {
        return token;
    }

    @Override
    public int sendID() {
        return tenantID;
    }
}