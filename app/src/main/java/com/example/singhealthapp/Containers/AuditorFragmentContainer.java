package com.example.singhealthapp.Containers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.test.espresso.IdlingResource;

import com.example.singhealthapp.HelperClasses.CentralisedToast;
import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.HelperClasses.HandlePhotoInterface;
import com.example.singhealthapp.HelperClasses.Ping;
import com.example.singhealthapp.HelperClasses.SetIdlingResource;
import com.example.singhealthapp.HelperClasses.SimpleIdlingResource;
import com.example.singhealthapp.Models.Case;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Auditor.AddTenant.AddTenantFragment;
import com.example.singhealthapp.Views.Auditor.Checklists.AuditChecklistFragment;
import com.example.singhealthapp.Views.Auditor.Checklists.ChecklistAdapter;
import com.example.singhealthapp.Views.Auditor.Checklists.SafetyChecklistFragment;
import com.example.singhealthapp.Views.Auditor.InterfacesAndAbstractClasses.IOnBackPressed;
import com.example.singhealthapp.Views.Auditor.Reports.ReportsFragment;
import com.example.singhealthapp.Views.Auditor.SearchTenant.SearchTenantFragment;
import com.example.singhealthapp.Views.Login.LoginActivity;
import com.example.singhealthapp.Views.Statistics.StatisticsFragment;
import com.example.singhealthapp.Views.TestFragment;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class AuditorFragmentContainer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        HandlePhotoInterface, AuditChecklistFragment.HandlePhotoListener, Ping {

    // for espresso tests using idling resource, will be null during execution
    @Nullable
    private SimpleIdlingResource mIdlingResource;
    private SetIdlingResource setIdlingResource = new SetIdlingResource(mIdlingResource);

    private static final String TAG = "AuditorFragmentContain";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
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
            EspressoCountingIdlingResource.increment();
            getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new SearchTenantFragment(), "getTenant").commit();
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
//        setIdlingResource.toFalse();
        EspressoCountingIdlingResource.increment();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.auditor_fragment_container);
        try {
            if (getSupportFragmentManager().findFragmentByTag("safetyChecklist").isVisible()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new SearchTenantFragment()).commit();
                return;
            }
        }
        catch (Exception ignored){ }
        try {
            if (getSupportFragmentManager().findFragmentByTag("auditChecklist").isVisible()) {
                // TODO: can go to safety fragment if it implements shared pref
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new SearchTenantFragment()).commit();
                return;
            }
        }
        catch (Exception ignored){ }
        try {
            if (getSupportFragmentManager().findFragmentByTag("addTenant").isVisible()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new SearchTenantFragment()).commit();
                return;
            }
        }
        catch (Exception ignored){ }
        try {
            if (getSupportFragmentManager().findFragmentByTag("getReport").isVisible()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new SearchTenantFragment()).commit();
                return;
            }
        }
        catch (Exception ignored){ }
        try {
            if (getSupportFragmentManager().findFragmentByTag("viewReport").isVisible()) {
                getSupportFragmentManager().beginTransaction()
                        .replace(getSupportFragmentManager().findFragmentByTag("viewReport").getId(), new ReportsFragment(), "getReport").commit();
                return;
            }
        }
        catch (Exception ignored){ }
        try {
            if (getSupportFragmentManager().findFragmentByTag("tenantsFragment").isVisible()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new SearchTenantFragment()).commit();
                return;
            }
        }
        catch (Exception ignored){ }
        try {
            if (getSupportFragmentManager().findFragmentByTag("safetyFragment").isVisible()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new SearchTenantFragment()).commit();
                return;
            }
        }
        catch (Exception ignored){ }
        try {
            if (getSupportFragmentManager().findFragmentByTag("viewCase").isVisible()) {
                getSupportFragmentManager().beginTransaction()
                        .replace(getSupportFragmentManager().findFragmentByTag("viewCase").getId()
                                , new ReportsFragment(), "getReport").commit();
                return;
            }
        }
        catch (Exception ignored){ }

        if (auditor_drawer.isDrawerOpen(GravityCompat.START)) {
            auditor_drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Do you want to log out? ");
            builder.setPositiveButton("OK", (dialog, id) -> {
                //AuditorFragmentContainer.super.onBackPressed();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        setIdlingResource.toFalse();
        EspressoCountingIdlingResource.increment();
        switch (item.getItemId()) {
            case R.id.nav_Auditor_Statistics:
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new StatisticsFragment()).commit();
                break;

            case R.id.nav_Tenants:
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new SearchTenantFragment()).commit();
                //getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new SafetyChecklistFragment()).commit();
                break;

            case R.id.nav_Reports:
                getSupportFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new ReportsFragment(), "getReport").commit();
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
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createTempPhotoFile();
            } catch (IOException ex) {
                Log.d(TAG, "takePhoto: error in creating file for image to go into");
            }
            if (photoFile != null) {
                mCurrentPhotoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoURI);
//                setIdlingResource.toFalse(); // to be set true in onActivityResult
                EspressoCountingIdlingResource.increment();
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                mChecklistAdapter = checklistAdapter;
                mAdapterPosition = adapterPosition;
                mCurrentQuestion = question;
                return true;
            } else {
                Log.d(TAG, "takePhoto: error getting uri for file or starting intent");
                CentralisedToast.makeText(this, "Unable to store or take photo", CentralisedToast.LENGTH_SHORT);
            }
        } else {
            CentralisedToast.makeText(this, "Camera does not exist", CentralisedToast.LENGTH_SHORT);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
        * Gets photo taken and updates recyclerview with a thumbnail
        * */
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            mChecklistAdapter.photoTaken(mAdapterPosition);
            if(Build.VERSION.SDK_INT < 28) {
                try {
                    mCurrentPhotoBitmap = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(),
                            mCurrentPhotoURI
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), mCurrentPhotoURI);
                try {
                    mCurrentPhotoBitmap = ImageDecoder.decodeBitmap(source);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            updatePhotoHashMap();
        }
//        setIdlingResource.toTrue(); //it was set false in takePhoto method
        EspressoCountingIdlingResource.decrement();
    }

    private void loadToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN_KEY", null);
    }

    public File createTempPhotoFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    private void updatePhotoHashMap() {
        if (mCurrentPhotoBitmap == null) {
            Log.e(TAG, "createCases: mCurrentPhotoBitmap is null, question: "+mCurrentQuestion);
        }
        photoBitmapHashMap.put(mCurrentQuestion, mCurrentPhotoBitmap);
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

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    public void setIdlingResourcePing() {
//        setIdlingResource.toTrue();
        EspressoCountingIdlingResource.decrement();
    }

    public interface OnPhotoTakenListener {
        void photoTaken(int position);
    }
}