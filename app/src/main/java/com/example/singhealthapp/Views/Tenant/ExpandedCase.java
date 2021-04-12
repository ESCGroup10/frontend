package com.example.singhealthapp.Views.Tenant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.singhealthapp.HelperClasses.CentralisedToast;
import com.example.singhealthapp.HelperClasses.HandleImageOperations;
import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.Models.Case;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.R;
import com.example.singhealthapp.HelperClasses.IOnBackPressed;
import com.example.singhealthapp.Views.Auditor.StatusConfirmation.StatusConfirmationFragment;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;
import static android.view.View.VISIBLE;

public class ExpandedCase extends Fragment implements IOnBackPressed {
    private static final String TAG = "ExpandedCase";

    // UI stuff
    TextView companyTextView;
    TextView institutionTextView;
    TextView caseNumberTextView;
    TextView nonComplianceTypeTextView;
    TextView resolvedStatusTextView;
    ImageView unresolvedImageView;
    TextView unresolvedImageDateTextView;
    TextView unresolvedCommentsTextView;
    ImageView resolvedImageView;
    TextView resolvedImageDateTextView;
    TextView resolvedCommentsTextView;
    View unresolvedResolvedSeparator;
    Button resolveButton;
    LinearLayout resolvingCaseSection;
    ImageView cameraButton;
    ImageView uploadButton;
    Button confirmButton;
    Button rejectButton;
    Button acceptButton;
    TextView unresolvedImageViewPlaceholder;
    TextView resolvedImageViewPlaceholder;
    EditText resolvedCommentsEditText;

    private Bundle bundle;

    // database stuff
    Case thisCase;
    DatabaseApiCaller apiCaller;
    private String token;
    private int reportID;
    private int caseID;
    private String userType;

    private int reportNumber;
    private Integer caseNumber;
    private String company;
    private String institution;
    private String nonComplianceType;
    private boolean resolvedStatus;
    private String unresolvedComments;
    private String resolvedComments;
    private String unresolvedImageName;
    private String resolvedImageName;
    private String unresolvedImageDate;
    private String resolvedImageDate;

    // Camera stuff
    Uri mImageURI;
    final int REQUEST_IMAGE_CAPTURE = 0;
    final int REQUEST_IMAGE_FROM_GALLERY = 1;
    Bitmap mImageBitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.f_case_expanded, container, false);
        loadUserType();
        findAllViews(view);

        loadToken();
        initApiCaller();
        loadCompanyAndInstitution();

        bundle = getArguments();
        try {
            company = bundle.getString("COMPANY_KEY");
        } catch (Exception e) {
            company = "Kopitiam";
        }
        try {
            reportNumber = bundle.getInt("REPORT_NUMBER_KEY");
        } catch (Exception e) {
            reportNumber = 1;
        }
        try {
            caseNumber = bundle.getInt("CASE_NUMBER_KEY");
        } catch (Exception e) {
            caseNumber = 1;
        }
        try {
            resolvedStatus = bundle.getBoolean("RESOLVED_STATUS_KEY");
        } catch (Exception e) {
            resolvedStatus = false;
        }
        try {
            reportID = bundle.getInt("REPORT_ID_KEY");
        } catch (Exception e) {
            reportID = 242;
        }
        try {
            caseID = bundle.getInt("CASE_ID_KEY");
        } catch (Exception e) {
            caseID = 58;
        }
        getActivity().setTitle("Report "+reportNumber);
        getCase();
        setAllViewsFromBundle();
        setOnClickListeners();

        return view;
    }

    private synchronized void loadUserType() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        userType = sharedPreferences.getString("USER_TYPE_KEY", null);
        notifyAll();
    }

    private synchronized void findAllViews(View view) {
        while (userType == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "findAllViews: called");
        companyTextView = view.findViewById(R.id.companyTextView);
        institutionTextView = view.findViewById(R.id.institutionTextView);
        caseNumberTextView = view.findViewById(R.id.caseNumberTextView);
        nonComplianceTypeTextView = view.findViewById(R.id.nonComplianceTypeTextView);
        resolvedStatusTextView = view.findViewById(R.id.resolvedStatusTextView);
        unresolvedImageView = view.findViewById(R.id.unresolvedImageView);
        unresolvedImageDateTextView = view.findViewById(R.id.unresolvedImageDateTextView);
        unresolvedCommentsTextView = view.findViewById(R.id.unresolvedCommentsTextView);
        unresolvedResolvedSeparator = view.findViewById(R.id.unresolvedResolvedSeparator);
        resolvedImageView = view.findViewById(R.id.resolvedImageView);
        resolvedImageDateTextView = view.findViewById(R.id.resolvedImageDateTextView);
        resolvedCommentsTextView = view.findViewById(R.id.resolvedCommentsTextView);
        unresolvedImageViewPlaceholder = view.findViewById(R.id.unresolvedImageViewPlaceholder);
        resolvedImageViewPlaceholder = view.findViewById(R.id.resolvedImageViewPlaceholder);
        if (userType.equals("Auditor")) {
            acceptButton = view.findViewById(R.id.acceptButton);
            rejectButton = view.findViewById(R.id.rejectButton);
        } else {
            resolveButton = view.findViewById(R.id.resolveButton);
            resolvingCaseSection = view.findViewById(R.id.resolvingCaseSection);
            cameraButton = view.findViewById(R.id.cameraButton);
            uploadButton = view.findViewById(R.id.uploadButton);
            confirmButton = view.findViewById(R.id.confirmButton);
            resolvedCommentsEditText = view.findViewById(R.id.resolvedCommentsEditText);
        }
    }

    private void setOnClickListeners() {
        if (!userType.equals("Auditor")) {
            resolveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resolveButton.setVisibility(View.GONE);
                    resolvingCaseSection.setVisibility(View.VISIBLE);
                }
            });

            cameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = HandleImageOperations.createFile(getActivity());
                        } catch (IOException ex) {
                            Log.d(TAG, "takePhoto: error in creating file for image to go into");
                        }
                        if (photoFile != null) {
                            mImageURI = FileProvider.getUriForFile(getActivity(),
                                    "com.example.android.fileprovider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageURI);
                            EspressoCountingIdlingResource.increment();
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        } else {
                            Log.d(TAG, "takePhoto: error getting uri for file or starting intent");
                            CentralisedToast.makeText(getActivity(), "Unable to store or take photo", CentralisedToast.LENGTH_SHORT);
                        }
                    } else {
                        CentralisedToast.makeText(getActivity(), "Camera does not exist", CentralisedToast.LENGTH_SHORT);
                    }
                }
            });

            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , REQUEST_IMAGE_FROM_GALLERY);
                }
            });

            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mImageBitmap == null) {
                        CentralisedToast.makeText(getActivity(), "Please set an image before submitting your resolution!", CentralisedToast.LENGTH_SHORT);
                        return;
                    }
                    createUpdatedCase(true);
                    Call<Void> patchCall = apiCaller.patchCase("Token " + token, caseID, thisCase);

                    patchCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                            Log.d(TAG, "patchCall onResponse: " + response);
                            Log.d(TAG, "patchCall onResponse: code: " + response.code());
                            submit();
                        }

                        @Override
                        public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                            Log.d(TAG, "deleteReport onFailure: " + t.toString());
                            Toast.makeText(getActivity(), "Failed to update database", Toast.LENGTH_SHORT).show();
                        }
                    });
                    // upload non-null bitmap to database
                    HandleImageOperations.uploadImageToDatabase(mImageBitmap, resolvedImageName);
                }
            });
        } else {
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: update case to be resolved
                    createUpdatedCase(true);
                    Call<Void> patchCall = apiCaller.patchCase("Token " + token, caseID, thisCase);

                    patchCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                            Log.d(TAG, "patchCall onResponse: code: " + response.code());
                            CentralisedToast.makeText(getActivity(), "Resolution accepted successfully", CentralisedToast.LENGTH_SHORT);
                        }

                        @Override
                        public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                            t.printStackTrace();
                            CentralisedToast.makeText(getActivity(), "Failed to accept resolution, try again.",
                                    CentralisedToast.LENGTH_SHORT);
                        }
                    });
                }
            });

            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createUpdatedCase(false);
                    Call<Void> patchCall = apiCaller.patchCase("Token " + token, caseID, thisCase);

                    patchCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                            Log.d(TAG, "patchCall onResponse: code: " + response.code());
                            CentralisedToast.makeText(getActivity(), "Resolution rejected successfully", CentralisedToast.LENGTH_SHORT);
                        }

                        @Override
                        public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                            t.printStackTrace();
                            CentralisedToast.makeText(getActivity(), "Failed to reject resolution, try again.",
                                    CentralisedToast.LENGTH_SHORT);
                        }
                    });
                }
            });
        }
    }

    private void createUpdatedCase (boolean acccept) {
        if (userType.equals("Auditor")) {
            if (acccept) {
                thisCase.setIs_resolved(true);
                return;
            } else {
                // TODO: add additional comments to the resolved image
                // TODO: write another textview for that? maybe a scrollable textview bulletin
                return;
            }
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String datetime = dateFormat.format(new Date());
        thisCase.setResolved_date(datetime);
        thisCase.setResolved_photo(resolvedImageName);
        resolvedComments = resolvedCommentsEditText.getText().toString();
        thisCase.setResolved_comments(resolvedComments);
    }

    private void submit() {
        Bundle bundle = new Bundle();
        //keys
        String TITLE_KEY = "title_key";
        bundle.putString(TITLE_KEY, "Case Resolution Submitted");
        String MSG_KEY = "message_key";
        bundle.putString(MSG_KEY, "Thank you!");
        String BUTTON_TXT_KEY = "button_text_key";
        bundle.putString(BUTTON_TXT_KEY, "Return to reports");
        StatusConfirmationFragment statusConfirmationFragment = new StatusConfirmationFragment();
        statusConfirmationFragment.setArguments(bundle);
        ExpandedCase.this.getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, statusConfirmationFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        /**
         * Usage: Get image bitmap
         * */
        Bitmap oldBitmap = mImageBitmap;
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                mImageBitmap = HandleImageOperations.getBitmap(resultCode, getActivity(), mImageURI);
                break;
            case REQUEST_IMAGE_FROM_GALLERY:
                if (resultCode == RESULT_OK && data != null) {
                    Uri selectedImage = data.getData();
                    try {
                        mImageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    } catch (FileNotFoundException e) {
                        Log.d(TAG, "onActivityResult: file not found");
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.d(TAG, "onActivityResult: interrupted exception");
                        e.printStackTrace();
                    }
                }
                break;
        }
        if (mImageBitmap == oldBitmap) {
            Log.d(TAG, "onActivityResult: photo not taken");
        } else {
            Log.d(TAG, "onActivityResult: image bitmap received");
        }
    }

    private void setAllViewsFromBundle() {
        Log.d(TAG, "setAllViews: called");
        setHalfBoldTextViews(companyTextView, company);
        setHalfBoldTextViews(institutionTextView, institution);
        caseNumberTextView.setText((String)(caseNumberTextView.getText() + caseNumber.toString()));
        setHalfBoldTextViews(resolvedStatusTextView, (resolvedStatus?"Resolved":"Unresolved"));
    }

    private void setHalfBoldTextViews(TextView mytextview, String textToAdd) {
        String originalText = mytextview.getText().toString();
        if(Build.VERSION.SDK_INT < 24) {
            String sourceString = "<b>" + originalText + "</b> " + textToAdd;
            mytextview.setText(Html.fromHtml(sourceString));
        } else {
            int INT_END = originalText.length();
            SpannableStringBuilder str = new SpannableStringBuilder(originalText + textToAdd);
            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, INT_END, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mytextview.setText(str);
        }
    }

    private String getDate(String date) {
        return date.substring(0, 10) + " " + date.substring(11, 19);
    }

    private void setAllViewsFromDatabase() {
        Log.d(TAG, "setAllViewsFromDatabase: called");
        setHalfBoldTextViews(nonComplianceTypeTextView, nonComplianceType);
        unresolvedImageDateTextView.setText((String)(unresolvedImageDateTextView.getText() + getDate(unresolvedImageDate)));
        unresolvedCommentsTextView.setText((String)(unresolvedCommentsTextView.getText() + unresolvedComments));
        unresolvedImageView.setVisibility(View.VISIBLE);
        HandleImageOperations.retrieveImageFromDatabase(getActivity(), unresolvedImageName, unresolvedImageView, unresolvedImageViewPlaceholder, 300, 300);
        if (resolvedStatus) {
            if (userType.equals("Auditor")) {
                if (resolvedStatus) {
                    acceptButton.setVisibility(View.VISIBLE);
                    rejectButton.setVisibility(View.VISIBLE);
                }
            }
            resolvedImageDateTextView.setText((String)(resolvedImageDateTextView.getText() + getDate(resolvedImageDate)));
            resolvedCommentsTextView.setText((String)(resolvedCommentsTextView.getText() + resolvedComments));
            resolvedImageView.setVisibility(View.VISIBLE);
            HandleImageOperations.retrieveImageFromDatabase(getActivity(), resolvedImageName, resolvedImageView, resolvedImageViewPlaceholder, 300, 300);
            unresolvedResolvedSeparator.setVisibility(VISIBLE);
            resolvedImageDateTextView.setVisibility(VISIBLE);
            resolvedCommentsTextView.setVisibility(VISIBLE);
        } else {
            if (!userType.equals("Auditor")) {
                resolveButton.setVisibility(VISIBLE);
            }
        }
    }

    private synchronized void loadCompanyAndInstitution() {
        while (userType.equals(null)) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "loadCompanyAndInstitution: called");
        if (!userType.equals("Auditor")) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
            company = sharedPreferences.getString("COMPANY_KEY", "default company");
            institution = sharedPreferences.getString("INSTITUTION_KEY", "default institution");
        } else {
            if (userType.equals("Auditor")) {
                try {
                    institution = bundle.getString("INSTITUTION_KEY");
                } catch (Exception e) {
                    institution = "SGH";
                }
            }
        }
    }

    private synchronized void loadToken() {
        Log.d(TAG, "loadToken: called");
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN_KEY", null);
        notifyAll();
    }

    private synchronized void initApiCaller() {
        /**
         * This method is synchronised in order to make other methods wait until apiCaller is instantiated before trying to use it
         * as well as wait for the token to be initialised
         * */
        Log.d(TAG, "initApiCaller: called");
        while (token == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "initApiCaller: token: "+token);
        apiCaller = new Retrofit.Builder()
                .baseUrl("https://esc10-303807.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);
        notifyAll();
    }

    private synchronized void getCase() {
        /**
         * This method is synchronised to ensure that apiCaller is instantiated before we try to use it
         * Usage: retrieves the required fields from specified case in database
         * */
        Log.d(TAG, "getCase: called");
        while (apiCaller == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "getCase: apiCaller: "+apiCaller.toString());
        Call<List<Case>> myCaseList;
        Log.d(TAG, "getCase: token: "+token);
        Log.d(TAG, "getCase: reportID: "+reportID);
        myCaseList = apiCaller.getCasesById("Token " + token, reportID, (resolvedStatus?1:0));
        myCaseList.enqueue(new Callback<List<Case>>() {
            @Override
            public void onResponse(@NotNull Call<List<Case>> call, @NotNull Response<List<Case>> response) {
                Log.d(TAG, "getCase response code: " + response.code());
                System.out.println("getCase response code: " + response.code());
                for (Case c : response.body()) {
                    if (c.getId() == caseID) {
                        thisCase = c;
                    }
                }
                if (thisCase != null) {
                    nonComplianceType = thisCase.getNon_compliance_type();
                    unresolvedComments = thisCase.getUnresolved_comments();
                    unresolvedImageDate = thisCase.getUnresolved_date();
                    unresolvedImageName = thisCase.getUnresolved_photo();

                    // if the case is resolved, get the data, else leave relevant fields as null
                    if (resolvedStatus) {
                        resolvedImageName = thisCase.getResolved_photo();
                        resolvedImageDate = thisCase.getResolved_date();
                        resolvedComments = thisCase.getResolved_comments();
                    } else {
                        resolvedImageName = unresolvedImageName + "_resolved";
                    }
                    setAllViewsFromDatabase();
                } else {
                    System.out.println("onResponse: no cases match given caseID");
                    Log.d(TAG, "onResponse: no cases match given caseID");
                }
            }
            @Override
            public void onFailure(@NotNull Call<List<Case>> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        if (userType.equals("Auditor")) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.auditor_fragment_container, new MyReportsFragment(), "getReport").commit();
        } else {
            if (resolvingCaseSection.getVisibility() == View.VISIBLE) {
                resolvingCaseSection.setVisibility(View.GONE);
                resolveButton.setVisibility(View.VISIBLE);
            } else {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MyReportsFragment(), "getReport").commit();
            }
        }
        return true;
    }

}