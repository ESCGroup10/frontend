package com.example.singhealthapp.Views.Tenant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.singhealthapp.HelperClasses.CentralisedToast;
import com.example.singhealthapp.HelperClasses.CustomFragment;
import com.example.singhealthapp.HelperClasses.CustomViewSettings;
import com.example.singhealthapp.HelperClasses.DateOperations;
import com.example.singhealthapp.HelperClasses.HandleImageOperations;
import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.HelperClasses.TextAestheticsAndParsing;
import com.example.singhealthapp.Models.Case;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.R;
import com.example.singhealthapp.HelperClasses.IOnBackPressed;
import com.example.singhealthapp.Views.Auditor.StatusConfirmation.StatusConfirmationFragment;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.example.singhealthapp.HelperClasses.DateOperations.convertDatabaseDateToReadableDate;

public class CaseExpanded extends CustomFragment implements IOnBackPressed {
    private static final String TAG = "CaseExpanded";

    // UI stuff
    TextView companyTextView, institutionTextView, nonComplianceTypeTextView, resolvedStatusTextView, unresolvedImageDateTextView,
            unresolvedCommentsTextView, resolvedImageDateTextView, resolvedCommentsTextView, unresolvedImageViewPlaceholder,
            resolvedImageViewPlaceholder, rejectedCommentsTextView, resolvingRejectedImageViewPlaceholder, questionTextView;
    ImageView unresolvedImageView, resolvedImageView, cameraButton, uploadButton, resolvingRejectedImageView;
    Button resolveButton, confirmButton, rejectButton, acceptButton, resolvingRejectedButton;
    LinearLayout resolvingCaseSection, auditorButtonsLinearLayout;
    EditText resolvedCommentsEditText, rejectedCommentsEditText;
    CardView resolvedCardView, resolvingRejectedImageCardView;

    private Bundle bundle;

    // database stuff
    Case thisCase;
    DatabaseApiCaller apiCaller;
    private String token, userType, company, institution, nonComplianceType, unresolvedComments, resolvedComments, unresolvedImageName,
            resolvedImageName, unresolvedImageDate, resolvedImageDate;
    private int reportID, caseID, reportNumber;
    private Integer caseNumber;
    private boolean RESOLVED;

    // Camera stuff
    Uri mImageURI;
    final int REQUEST_IMAGE_CAPTURE = 0;
    final int REQUEST_IMAGE_FROM_GALLERY = 1;
    Bitmap resolutionImageBitmap;

    // flags
    private boolean PENDING;
    private boolean REJECTED = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.f_case_expanded, container, false);
        loadUserType();
        findAllViews(view);
        setInitVisibility();

        loadToken();
        initApiCaller();
        loadCompanyAndInstitution();

        bundle = getArguments();
        company = bundle.getString("COMPANY_KEY");
        reportNumber = bundle.getInt("REPORT_NUMBER_KEY");
        caseNumber = bundle.getInt("CASE_NUMBER_KEY");
        RESOLVED = bundle.getBoolean("RESOLVED_STATUS_KEY");
        reportID = bundle.getInt("REPORT_ID_KEY");
        caseID = bundle.getInt("CASE_ID_KEY");
        PENDING = bundle.getBoolean("PENDING_KEY");

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
        // for all users
        companyTextView = view.findViewById(R.id.companyTextView);
        institutionTextView = view.findViewById(R.id.institutionTextView);
        nonComplianceTypeTextView = view.findViewById(R.id.nonComplianceTypeTextView);
        resolvedStatusTextView = view.findViewById(R.id.resolvedStatusTextView);
        unresolvedImageView = view.findViewById(R.id.unresolvedImageView);
        unresolvedImageDateTextView = view.findViewById(R.id.unresolvedImageDateTextView);
        unresolvedCommentsTextView = view.findViewById(R.id.unresolvedCommentsTextView);
        resolvedCardView = view.findViewById(R.id.resolvedCardView);
        resolvedImageView = view.findViewById(R.id.resolvedImageView);
        resolvedImageDateTextView = view.findViewById(R.id.resolvedImageDateTextView);
        resolvedCommentsTextView = view.findViewById(R.id.resolvedCommentsTextView);
        unresolvedImageViewPlaceholder = view.findViewById(R.id.unresolvedImageViewPlaceholder);
        resolvedImageViewPlaceholder = view.findViewById(R.id.resolvedImageViewPlaceholder);
        questionTextView = view.findViewById(R.id.questionTextView);

        // for auditor
        acceptButton = view.findViewById(R.id.acceptButton);
        rejectButton = view.findViewById(R.id.rejectButton);
        rejectedCommentsEditText = view.findViewById(R.id.rejectedCommentsEditText);
        auditorButtonsLinearLayout = view.findViewById(R.id.auditorButtonsLinearLayout);

        // for tenants
        resolveButton = view.findViewById(R.id.resolveButton);
        resolvingCaseSection = view.findViewById(R.id.resolvingCaseSection);
        cameraButton = view.findViewById(R.id.cameraButton);
        uploadButton = view.findViewById(R.id.uploadButton);
        confirmButton = view.findViewById(R.id.confirmButton);
        resolvedCommentsEditText = view.findViewById(R.id.resolvedCommentsEditText);
        rejectedCommentsTextView = view.findViewById(R.id.rejectedCommentsTextView);
        resolvingRejectedButton = view.findViewById(R.id.rejectedResolveButton);
        resolvingRejectedImageCardView = view.findViewById(R.id.resolvingRejectedImageCardView);
        resolvingRejectedImageView = view.findViewById(R.id.resolvingRejectedImageView);
        resolvingRejectedImageViewPlaceholder = view.findViewById(R.id.resolvingRejectedImageViewPlaceholder);
    }

    private void setInitVisibility() {
        // for all users:
        // unresolved section
        unresolvedImageView.setVisibility(GONE);
        rejectedCommentsEditText.setVisibility(GONE);

        // resolved section
        resolveButton.setVisibility(GONE);
        resolvedCardView.setVisibility(GONE);
        resolvedImageView.setVisibility(GONE);
        resolvedImageViewPlaceholder.setVisibility(GONE);
        auditorButtonsLinearLayout.setVisibility(GONE);
        rejectedCommentsTextView.setVisibility(GONE);
        resolvingRejectedButton.setVisibility(GONE);

        // resolving case section
        resolvingCaseSection.setVisibility(GONE);

        if (userType.equals("Auditor")) {
            if (PENDING || RESOLVED) {
                resolvedCardView.setVisibility(VISIBLE);
                resolvedImageViewPlaceholder.setVisibility(VISIBLE);
            }
            if (PENDING) {
                auditorButtonsLinearLayout.setVisibility(VISIBLE);
            }
        }
        if (!userType.equals("Auditor")) {
            if (PENDING || RESOLVED) {
                resolvedCardView.setVisibility(VISIBLE);
                resolvedImageViewPlaceholder.setVisibility(VISIBLE);
            } else {
                resolveButton.setVisibility(VISIBLE);
            }
        }

        // resolving rejected case section
        resolvingRejectedImageCardView.setVisibility(GONE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnClickListeners() {
        if (!userType.equals("Auditor")) {
            resolveButton.setOnClickListener(v -> {
                resolveButton.setVisibility(GONE);
                resolvingCaseSection.setVisibility(View.VISIBLE);
            });

            resolvingRejectedButton.setOnClickListener(v -> {
                resolvingRejectedButton.setVisibility(GONE);
                resolvingCaseSection.setVisibility(View.VISIBLE);
            });

            CustomViewSettings.makeScrollable(resolvedCommentsEditText);

            cameraButton.setOnClickListener(v -> {
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
            });

            uploadButton.setOnClickListener(v -> {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , REQUEST_IMAGE_FROM_GALLERY);
            });

            confirmButton.setOnClickListener(v -> {
                if (resolutionImageBitmap == null) {
                    CentralisedToast.makeText(getActivity(), "Please set an image before submitting your resolution!",
                            CentralisedToast.LENGTH_SHORT);
                    return;
                }
                createUpdatedCase(true);
                Call<Void> patchCall = apiCaller.patchCase("Token " + token, caseID, thisCase);

            patchCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                    Log.d(TAG, "patchCall onResponse: " + response);
                    Log.d(TAG, "patchCall onResponse: code: " + response.code());
                    TenantSubmit("Case Resolution Submitted", "Thank you!",
                            "SingHealth staff will review your submission within 3 working days.",
                            "Return to My Reports");
                }

                    @Override
                    public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                        Log.d(TAG, "patchCall onFailure: " + t.toString());
                        Toast.makeText(getActivity(), "Failed to update database", Toast.LENGTH_SHORT).show();
                    }
                });
                // upload non-null bitmap to database
                HandleImageOperations.uploadImageToDatabase(resolutionImageBitmap, resolvedImageName);
            });
        } else {
            CustomViewSettings.makeScrollable(rejectedCommentsEditText);

            acceptButton.setOnClickListener(v -> {
                // TODO: update case to be resolved
                createUpdatedCase(true);
                Call<Void> patchCall = apiCaller.patchCase("Token " + token, caseID, thisCase);

                patchCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                        Log.d(TAG, "patchCall onResponse: code: " + response.code());
                        AuditorSubmit("Case Resolution Accepted", "Thank you!", "", "Return to case previews");
                    }

                    @Override
                    public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                        t.printStackTrace();
                        CentralisedToast.makeText(getActivity(), "Failed to accept resolution, try again.",
                                CentralisedToast.LENGTH_SHORT);
                    }
                });
            });

            rejectButton.setOnClickListener(v -> {
                if (rejectButton.getText().equals("Reject")) {
                    rejectedCommentsEditText.setVisibility(VISIBLE);
                    acceptButton.setVisibility(GONE);
                    rejectButton.setText("Confirm reject");
                } else {
                    createUpdatedCase(false);
                    Call<Void> patchCall = apiCaller.patchCase("Token " + token, caseID, thisCase);

                    patchCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                            Log.d(TAG, "patchCall onResponse: code: " + response.code());
                            AuditorSubmit("Case Resolution Rejected", "Thank you!", "", "Return to case previews");
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

    private void createUpdatedCase (boolean accept) {
        if (userType.equals("Auditor")) {
            if (accept) {
                thisCase.setIs_resolved(true);
            } else {
                thisCase.setRejected_comments(rejectedCommentsEditText.getText().toString());
            }
            return;
        }
        String datetime = DateOperations.getCurrentDatabaseDate();
        thisCase.setRejected_comments(""); // set this to empty since each new submission is not rejected
        thisCase.setResolved_date(datetime);
        thisCase.setResolved_photo(resolvedImageName);
        thisCase.setResolved_comments(resolvedCommentsEditText.getText().toString());
    }

    private void TenantSubmit(String title, String msg, String additionalMsg, String buttonText) {
        Bundle bundle = new Bundle();
        //keys
        bundle.putString("TITLE_KEY", title);
        bundle.putString("MSG_KEY", msg);
        if (!additionalMsg.equals("")) {
            bundle.putString("ADDITION_MSG_KEY", additionalMsg);
        }
        bundle.putString("BUTTON_TXT_KEY", buttonText);
        StatusConfirmationFragment statusConfirmationFragment = new StatusConfirmationFragment();
        statusConfirmationFragment.setArguments(bundle);
        CaseExpanded.this.getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, statusConfirmationFragment, statusConfirmationFragment.getClass().getName())
                .addToBackStack(null)
                .commit();
    }

    private void AuditorSubmit(String title, String msg, String additionalMsg, String buttonText) {
        Bundle bundle = new Bundle();
        //keys
        bundle.putString("TITLE_KEY", title);
        bundle.putString("MSG_KEY", msg);
        if (!additionalMsg.equals("")) {
            bundle.putString("ADDITION_MSG_KEY", additionalMsg);
        }
        bundle.putString("BUTTON_TXT_KEY", buttonText);
        StatusConfirmationFragment statusConfirmationFragment = new StatusConfirmationFragment();
        statusConfirmationFragment.setArguments(bundle);
        CaseExpanded.this.getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.auditor_fragment_container, statusConfirmationFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        /**
         * Usage: Get image bitmap
         * */
        Bitmap oldBitmap = resolutionImageBitmap;

        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                resolutionImageBitmap = HandleImageOperations.getBitmap(resultCode, getActivity(), mImageURI);
                break;
            case REQUEST_IMAGE_FROM_GALLERY:
                if (resultCode == RESULT_OK && data != null) {
                    Uri selectedImage = data.getData();
                    try {
                        resolutionImageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
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
        if (resolutionImageBitmap == oldBitmap) {
            Log.d(TAG, "onActivityResult: photo not taken");
        } else {
            Log.d(TAG, "onActivityResult: image bitmap received");
            displayResolvedImage();
        }
    }

    private void displayResolvedImage() {
        if (REJECTED) {
            resolvingRejectedImageCardView.setVisibility(View.VISIBLE);
            resolvingRejectedImageViewPlaceholder.setVisibility(View.VISIBLE);
            getActivity().runOnUiThread(() -> {
                resolvingRejectedImageView.setImageBitmap(resolutionImageBitmap);
                resolvingRejectedImageViewPlaceholder.setVisibility(GONE);
                resolvingRejectedImageView.setVisibility(View.VISIBLE);
            });
        } else {
            resolvedCardView.setVisibility(View.VISIBLE);
            resolvedImageViewPlaceholder.setVisibility(View.VISIBLE);
            getActivity().runOnUiThread(() -> {
                resolvedImageView.setImageBitmap(resolutionImageBitmap);
                resolvedImageViewPlaceholder.setVisibility(GONE);
                resolvedImageView.setVisibility(View.VISIBLE);
            });
        }
    }

    private void setAllViewsFromBundle() {
        Log.d(TAG, "setAllViews: called");
        companyTextView.setText(company);
        institutionTextView.setText(institution);
        String resolvedStatus = RESOLVED ?"Resolved":"Unresolved";
        resolvedStatus = PENDING?"Pending resolution":resolvedStatus;
        resolvedStatusTextView.setText(resolvedStatus);
    }

    private void setAllViewsFromDatabase() {
        Log.d(TAG, "setAllViewsFromDatabase: called");
        nonComplianceTypeTextView.setText(nonComplianceType);
        unresolvedImageDateTextView.setText((unresolvedImageDateTextView.getText() + convertDatabaseDateToReadableDate(unresolvedImageDate)));
        unresolvedCommentsTextView.setText((unresolvedComments.equals("")?unresolvedCommentsTextView.getText():unresolvedComments));
        try {
            HandleImageOperations.retrieveImageFromDatabase(getActivity(), unresolvedImageName, unresolvedImageView, unresolvedImageViewPlaceholder, 500, 300);
        } catch (ExceptionInInitializerError e) {
            unresolvedImageViewPlaceholder.setText("Unable to retrieve image from Database");
        }
        if (RESOLVED || PENDING) {
            if (userType.equals("Auditor")) {
                if (!RESOLVED && resolvedImageName!=null) { // resolution pending approval
                    auditorButtonsLinearLayout.setVisibility(View.VISIBLE);
                }
            } else {
                resolveButton.setVisibility(View.GONE);
            }
            resolvedCardView.setVisibility(View.VISIBLE);
            resolvedImageDateTextView.setVisibility(VISIBLE);
            resolvedCommentsTextView.setVisibility(VISIBLE);
            resolvedImageViewPlaceholder.setVisibility(View.VISIBLE);
            resolvedImageDateTextView.setText((resolvedImageDateTextView.getText() + convertDatabaseDateToReadableDate(resolvedImageDate)));
            resolvedCommentsTextView.setText((resolvedComments.equals("")?resolvedCommentsTextView.getText():resolvedComments));
            HandleImageOperations.retrieveImageFromDatabase(getActivity(), resolvedImageName, resolvedImageView, resolvedImageViewPlaceholder, 500, 300);
        } else { // if not resolved or pending
            if (!userType.equals("Auditor")) {
                Log.d(TAG, "setAllViewsFromDatabase: handling for tenant");
                if (REJECTED) {
                    Log.d(TAG, "setAllViewsFromDatabase: rejected!");
                    resolveButton.setVisibility(View.GONE);
                    resolvedCardView.setVisibility(View.VISIBLE);
                    resolvedImageDateTextView.setVisibility(VISIBLE);
                    resolvedCommentsTextView.setVisibility(VISIBLE);
                    resolvedImageViewPlaceholder.setVisibility(VISIBLE);
                    rejectedCommentsTextView.setVisibility(VISIBLE);
                    resolvingRejectedButton.setVisibility(VISIBLE);
                    resolvedImageDateTextView.setText((resolvedImageDateTextView.getText() + convertDatabaseDateToReadableDate(resolvedImageDate)));
                    resolvedCommentsTextView.setText((resolvedComments.equals("")?resolvedCommentsTextView.getText():resolvedComments));
                    HandleImageOperations.retrieveImageFromDatabase(getActivity(), resolvedImageName, resolvedImageView, resolvedImageViewPlaceholder, 500, 300);
                } else {
                    resolveButton.setVisibility(VISIBLE);
                }
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
        myCaseList = apiCaller.getCasesById("Token " + token, reportID, (RESOLVED ?1:0));
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
                Log.d(TAG, "onResponse: resolved photo: "+thisCase.getResolved_photo());
                Log.d(TAG, "onResponse: resolved date: "+thisCase.getResolved_date());
                if (thisCase != null) {
                    nonComplianceType = thisCase.getNon_compliance_type();
                    unresolvedComments = thisCase.getUnresolved_comments();
                    unresolvedImageDate = thisCase.getUnresolved_date();
                    unresolvedImageName = thisCase.getUnresolved_photo();
                    questionTextView.setText(thisCase.getQuestion());
                    if (!thisCase.getRejected_comments().equals("")) { // currently rejected by auditor, if auditor, treat as unresolved, not pending
                        Log.d(TAG, "onResponse: rejected comments is not empty");
                        if (userType.equals("Auditor")) {
                            Log.d(TAG, "onResponse: handling rejected for auditor");
                            PENDING = false;
                            RESOLVED = false;
                            resolvedStatusTextView.setText("Unresolved");

                        } else { // currently rejected by auditor, if tenant, treat as unresolved, REJECTED
                            Log.d(TAG, "onResponse: handling rejected for tenant");
                            REJECTED = true;
                            PENDING = false;
                            TextAestheticsAndParsing.setHalfBoldTextViews(rejectedCommentsTextView, thisCase.getRejected_comments());
                            resolvedStatusTextView.setText("Rejected");
                        }
                    }

                    if (!RESOLVED && !PENDING) { // if unresolved and not pending resolution
                        resolvedImageName = unresolvedImageName + "_resolved";
                        if (REJECTED) {
                            resolvedImageName = thisCase.getResolved_photo();
                            resolvedImageDate = thisCase.getResolved_date();
                            resolvedComments = thisCase.getResolved_comments();
                        }
                    } else { // if either pending or resolved
                        Log.d(TAG, "onResponse: pending: "+PENDING+"\nresolved: "+RESOLVED);
                        resolvedImageName = thisCase.getResolved_photo();
                        resolvedImageDate = thisCase.getResolved_date();
                        resolvedComments = thisCase.getResolved_comments();
                    }
                    setAllViewsFromDatabase();
                } else {
                    CentralisedToast.makeText(getActivity(), "Unable to find case "+caseNumber+".\n It might have been deleted.", CentralisedToast.LENGTH_SHORT);
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
        if (!userType.equals("Auditor") && resolvingCaseSection.getVisibility() == View.VISIBLE) {
            // undo pressing resolve button
            resolvingCaseSection.setVisibility(GONE);
            resolveButton.setVisibility(View.VISIBLE);
            return true;
        } else {
            return super.onBackPressed();
        }
    }

}