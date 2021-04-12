package com.example.singhealthapp.Containers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.singhealthapp.HelperClasses.HandleImageOperations;
import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.HelperClasses.HandlePhotoInterface;
import com.example.singhealthapp.HelperClasses.Ping;
import com.example.singhealthapp.Models.Case;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Auditor.AddTenant.AddTenantFragment;
import com.example.singhealthapp.Views.Auditor.ReportSummary.ReportSummaryFragment;
import com.example.singhealthapp.Views.Auditor.CasesPreview.CasesPreviewFragment;
import com.example.singhealthapp.Views.Auditor.Checklists.AuditChecklistFragment;
import com.example.singhealthapp.Views.Auditor.Checklists.ChecklistAdapter;
import com.example.singhealthapp.HelperClasses.IOnBackPressed;
import com.example.singhealthapp.Views.Auditor.TenantsPreview.TenantsPreviewFragment;
import com.example.singhealthapp.Views.ReportsPreview.ReportsPreviewFragment;
import com.example.singhealthapp.Views.Login.LoginActivity;
import com.example.singhealthapp.Views.Statistics.StatisticsFragment;
import com.example.singhealthapp.Views.TestFragment;
import com.google.android.material.navigation.NavigationView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class AuditorFragmentContainer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        HandlePhotoInterface, AuditChecklistFragment.HandlePhotoListener, Ping {

    private static final String TAG = "AuditorFragmentContain";
    ChecklistAdapter mChecklistAdapter;
    int mAdapterPosition;
    String mCurrentQuestion;
    Bitmap mCurrentPhotoBitmap;
    Uri mCurrentPhotoURI;

    private static Call<List<Case>> getCaseCall;
    private static Call<ResponseBody> delCaseCall;
    private static String token;
    HashMap<String, Bitmap> photoBitmapHashMap = new HashMap<>();

    //keys
    private final String USER_TYPE_KEY = "USER_TYPE_KEY";
    private final String USER_ID_KEY = "USER_ID_KEY";
    private final String TOKEN_KEY = "TOKEN_KEY";

    DrawerLayout auditor_drawer;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        if (savedInstanceState == null) {
            EspressoCountingIdlingResource.increment();
            getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new TenantsPreviewFragment(), "getTenant").commit();
        }

        loadToken();
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
        EspressoCountingIdlingResource.increment();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.auditor_fragment_container);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
            try {
                if (getSupportFragmentManager().findFragmentByTag("safetyChecklist").isVisible()) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new TenantsPreviewFragment()).commit();
                    return;
                }
            } catch (Exception ignored) {
            }
            try {
                if (getSupportFragmentManager().findFragmentByTag("auditChecklist").isVisible()) {
                    // TODO: can go to safety fragment if it implements shared pref
                    ((IOnBackPressed) getSupportFragmentManager().findFragmentByTag("auditChecklist")).onBackPressed();
//                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new TenantsPreviewFragment()).commit();
                    return;
                }
            } catch (Exception ignored) {
            }
            try {
                if (getSupportFragmentManager().findFragmentByTag("addTenant").isVisible()) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new TenantsPreviewFragment()).commit();
                    return;
                }
            } catch (Exception ignored) {
            }
            try {
                if (getSupportFragmentManager().findFragmentByTag("getReport").isVisible()) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new TenantsPreviewFragment()).commit();
                    return;
                }
            } catch (Exception ignored) {
            }
            try {
                if (getSupportFragmentManager().findFragmentByTag("viewReport").isVisible()) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(getSupportFragmentManager().findFragmentByTag("viewReport").getId(), new ReportsPreviewFragment(), "getReport").commit();
                    return;
                }
            } catch (Exception ignored) {
            }
            try {
                if (getSupportFragmentManager().findFragmentByTag("tenantsFragment").isVisible()) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new TenantsPreviewFragment()).commit();
                    return;
                }
            } catch (Exception ignored) {
            }
            try {
                if (getSupportFragmentManager().findFragmentByTag("safetyFragment").isVisible()) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new TenantsPreviewFragment()).commit();
                    return;
                }
            } catch (Exception ignored) {
            }
            try {
                if (getSupportFragmentManager().findFragmentByTag("viewCase").isVisible()) {
                    CasesPreviewFragment casesPreviewFragment = (CasesPreviewFragment) getSupportFragmentManager().findFragmentByTag("viewCase");
                    getSupportFragmentManager().beginTransaction()
                            .replace(getSupportFragmentManager().findFragmentByTag("viewCase").getId()
                                    , new ReportSummaryFragment(casesPreviewFragment.getReport(), casesPreviewFragment.getToken()), "viewReport").commit();
                    return;
                }
            } catch (Exception ignored) {
            }

            if (auditor_drawer.isDrawerOpen(GravityCompat.START)) {
                auditor_drawer.closeDrawer(GravityCompat.START);
            } else {
                EspressoCountingIdlingResource.decrement();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("Do you want to log out? ");
                builder.setPositiveButton("OK", (dialog, id) -> {
                    dialog.dismiss();
                    clearData(); // clear user type (to avoid auto login) and token (for safety)
                    Intent intent = new Intent(AuditorFragmentContainer.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
                builder.setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.dismiss();
                });
                builder.show();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        EspressoCountingIdlingResource.increment();
        switch (item.getItemId()) {
            case R.id.nav_Auditor_Statistics:
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new StatisticsFragment()).commit();
                break;

            case R.id.nav_Tenants:
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new TenantsPreviewFragment()).commit();
                break;

            case R.id.nav_Reports:
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new ReportsPreviewFragment(), "getReport").commit();
                break;

            case R.id.nav_Add_Tenant:
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new AddTenantFragment(), "addTenant").commit();
                break;

            case R.id.nav_Test:
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new TestFragment()).commit();
                break;
        }
        auditor_drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clearData() {
        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY, "");
        editor.putString(USER_TYPE_KEY, "");
        editor.commit();
    }

    public boolean takePhoto(ChecklistAdapter checklistAdapter, int adapterPosition, String question) {
        /**
        * Params
        * - checklistAdapter: The ChecklistAdapter that called this method
        * - adapterPosition: The position of the item in the adapter that called this method
        * Description
        * - Opens camera app to take photo
        * - Updates global variables mChecklistAdapter and mAdapterPosition
        * */
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
        /**
        * Usage: Get image bitmap
        * */
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                File photoFile = null;
            try {
                photoFile = HandleImageOperations.createFile(this);
            } catch (IOException ex) {
                Log.e(TAG, "onActivityResult: error in creating file for image to go into", ex);
                return;
            }
            mCurrentPhotoURI = result.getUri();
            mCurrentPhotoBitmap = HandleImageOperations.getBitmap(resultCode, this, mCurrentPhotoURI);
            updatePhotoHashMap();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        EspressoCountingIdlingResource.decrement();
    }

    private void loadToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN_KEY", null);
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

    public interface OnPhotoTakenListener {
        void photoTaken(int position);
    }

    @Override
    public void decrementCountingIdlingResource() {
        EspressoCountingIdlingResource.decrement();
    }

    @Override
    public void incrementCountingIdlingResource(int numResources) {
        for (int i = 0; i < numResources; i++) {
            EspressoCountingIdlingResource.increment();
        }
    }

}