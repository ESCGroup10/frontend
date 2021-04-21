package com.example.singhealthapp.Containers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

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

import com.example.singhealthapp.HelperClasses.HandleImageOperations;
import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.HelperClasses.HandlePhotoInterface;
import com.example.singhealthapp.HelperClasses.Ping;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Auditor.AddTenant.AddTenantFragment;
import com.example.singhealthapp.Views.Auditor.Checklists.AuditChecklistFragment;
import com.example.singhealthapp.Views.Auditor.Checklists.ChecklistAdapter;
import com.example.singhealthapp.HelperClasses.IOnBackPressed;
import com.example.singhealthapp.Views.Auditor.TenantsPreview.TenantsPreviewFragment;
import com.example.singhealthapp.Views.ReportsPreview.ReportsPreviewFragment;
import com.example.singhealthapp.Views.Login.LoginActivity;
import com.example.singhealthapp.Views.Statistics.StatisticsFragment;
import com.google.android.material.navigation.NavigationView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.HashMap;

public class AuditorFragmentContainer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        HandlePhotoInterface, AuditChecklistFragment.HandlePhotoListener, Ping {

    private static final String TAG = "AuditorFragmentContain";
    ChecklistAdapter mChecklistAdapter;
    int mAdapterPosition;
    String mCurrentQuestion;
    Bitmap mCurrentPhotoBitmap;
    Uri mCurrentPhotoURI;

    HashMap<String, Bitmap> photoBitmapHashMap = new HashMap<>();

    DrawerLayout auditor_drawer;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: I'm here");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        EspressoCountingIdlingResource.increment();

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.a_fragmentcontainer_auditor);

        Toolbar auditor_toolbar = findViewById(R.id.auditor_toolbar);
        setSupportActionBar(auditor_toolbar);

        auditor_drawer = findViewById(R.id.auditor_drawer_layout);
        NavigationView navigationView = findViewById(R.id.auditor_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, auditor_drawer, auditor_toolbar, R.string.drawer_open, R.string.drawer_close);
        auditor_drawer.addDrawerListener(toggle);
        toggle.syncState();

        Log.d(TAG, "onCreate: is savedInstanceState null: "+(savedInstanceState == null));
        if (savedInstanceState == null) {
//            EspressoCountingIdlingResource.increment();
            TenantsPreviewFragment tenantsPreviewFragment = new TenantsPreviewFragment();
            String tag = tenantsPreviewFragment.getClass().getName();
            getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, tenantsPreviewFragment, tag)
                    .addToBackStack(tag)
                    .commit();
        } else {
            EspressoCountingIdlingResource.decrement();
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
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.auditor_fragment_container);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
            if (auditor_drawer.isDrawerOpen(GravityCompat.START)) {
                auditor_drawer.closeDrawer(GravityCompat.START);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("Do you want to log out? ");
                builder.setPositiveButton("OK", (dialog, id) -> {
                    dialog.dismiss();
                    clearData(); // clear user type (to avoid auto login) and token (for safety)
                    Intent intent = new Intent(AuditorFragmentContainer.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
                builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
                EspressoCountingIdlingResource.decrement();
                builder.show();
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
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
            case R.id.nav_Auditor_Statistics:
                StatisticsFragment statisticsFragment = new StatisticsFragment();
                newTag = statisticsFragment.getClass().getName();
                Log.d(TAG, "onNavigationItemSelected: current tag: "+currentTag);
                Log.d(TAG, "onNavigationItemSelected: new tag: "+newTag);
                if (currentTag.equals(newTag)) {
                    EspressoCountingIdlingResource.decrement();
                    break;
                }
                fragmentManager.beginTransaction().replace(R.id.auditor_fragment_container, statisticsFragment, newTag)
                        .addToBackStack(newTag)
                        .commit();
                break;

            case R.id.nav_Tenants:
                EspressoCountingIdlingResource.decrement();
                TenantsPreviewFragment tenantsPreviewFragment = new TenantsPreviewFragment();
                newTag = tenantsPreviewFragment.getClass().getName();
                Log.d(TAG, "onNavigationItemSelected: current tag: "+currentTag);
                Log.d(TAG, "onNavigationItemSelected: new tag: "+newTag);
                if (currentTag.equals(newTag)) {
//                    EspressoCountingIdlingResource.decrement();
                    break;
                }
                fragmentManager.beginTransaction().replace(R.id.auditor_fragment_container, tenantsPreviewFragment, newTag)
                        .addToBackStack(newTag)
                        .commit();
                break;

            case R.id.nav_Reports:
                ReportsPreviewFragment reportsPreviewFragment = new ReportsPreviewFragment();
                newTag = reportsPreviewFragment.getClass().getName();
                Log.d(TAG, "onNavigationItemSelected: current tag: "+currentTag);
                Log.d(TAG, "onNavigationItemSelected: new tag: "+newTag);
                if (currentTag.equals(newTag)) {
                    EspressoCountingIdlingResource.decrement();
                    break;
                }
                fragmentManager.beginTransaction().replace(R.id.auditor_fragment_container, reportsPreviewFragment, newTag)
                        .addToBackStack(newTag)
                        .commit();
                break;

            case R.id.nav_Add_Tenant:
                AddTenantFragment addTenantFragment = new AddTenantFragment();
                newTag = addTenantFragment.getClass().getName();
                Log.d(TAG, "onNavigationItemSelected: current tag: "+currentTag);
                Log.d(TAG, "onNavigationItemSelected: new tag: "+newTag);
                if (currentTag.equals(newTag)) {
                    EspressoCountingIdlingResource.decrement();
                    break;
                }
                fragmentManager.beginTransaction().replace(R.id.auditor_fragment_container, addTenantFragment, newTag)
                        .addToBackStack(newTag)
                        .commit();
                break;
        }
        auditor_drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clearData() {
        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("TOKEN_KEY", "");
        editor.putString("USER_TYPE_KEY", "");
        editor.apply();
    }

    public boolean takePhoto(ChecklistAdapter checklistAdapter, int adapterPosition, String question) {
        try {
            this.getCurrentFocus().clearFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // start picker to get image for cropping and then use the image in cropping activity
        try {
            mChecklistAdapter = checklistAdapter;
            mAdapterPosition = adapterPosition;
            mCurrentQuestion = question;
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
            EspressoCountingIdlingResource.increment();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "takePhoto: ", e);
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
            try {
                HandleImageOperations.createFile(this);
            } catch (IOException ex) {
                Log.e(TAG, "onActivityResult: error in creating file for image to go into", ex);
                return;
            }
            mCurrentPhotoURI = result.getUri();
            mCurrentPhotoBitmap = HandleImageOperations.getBitmap(resultCode, this, mCurrentPhotoURI);
            updatePhotoHashMap();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                result.getError().printStackTrace();
            }
        }
        EspressoCountingIdlingResource.decrement();
    }

    private void updatePhotoHashMap() {
        if (mCurrentPhotoBitmap == null) {
            Log.e(TAG, "createCases: mCurrentPhotoBitmap is null, question: "+mCurrentQuestion);
        }
        photoBitmapHashMap.put(mCurrentQuestion, mCurrentPhotoBitmap);
        mChecklistAdapter.photoTaken(mAdapterPosition);
    }

    @Override
    public HashMap<String, Bitmap> getPhotoBitmaps() {
        return photoBitmapHashMap;
    }

    @Override
    public void clearCurrentReportPhotoData() {
        photoBitmapHashMap.clear();
        mCurrentQuestion = null;
        mCurrentPhotoBitmap = null;
    }

    @Override
    @VisibleForTesting
    public void activateEspressoIdlingResource() {
        EspressoCountingIdlingResource.activate();
    }

    public interface OnPhotoTakenListener {
        void photoTaken(int position);
    }

}