package com.example.singhealthapp.Views.Auditor.Checklists;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.HelperClasses.CentralisedToast;
import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.HelperClasses.HandleImageOperations;
import com.example.singhealthapp.HelperClasses.HandlePhotoInterface;
import com.example.singhealthapp.HelperClasses.QuestionBank;
import com.example.singhealthapp.Models.Case;
import com.example.singhealthapp.Models.ChecklistItem;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Report;
import com.example.singhealthapp.R;
import com.example.singhealthapp.HelperClasses.IOnBackPressed;
import com.example.singhealthapp.Views.Auditor.TenantsPreview.TenantsPreviewFragment;
import com.example.singhealthapp.Views.Auditor.StatusConfirmation.StatusConfirmationFragment;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuditChecklistFragment extends Fragment implements IOnBackPressed {
    public static final String TAG = "AuditChecklistFragment";

    Button submit_audit_button;
    EditText overall_notes_editText;
    FloatingActionButton ProfessionalismAndStaffHygieneFAB, HousekeepingAndGeneralCleanlinessFAB, FoodHygieneFAB, HealthierChoiceFAB,
            WorkplaceSafetyAndHealthFAB;
    TextView ProfessionalismAndStaffHygieneTextView, HousekeepingAndGeneralCleanlinessTextView, FoodHygieneTextView, HealthierChoiceTextView,
            WorkplaceSafetyAndHealthTextView;
    NestedScrollView nestedScrollView;

    private String[] header_files;
    private ArrayList<ChecklistAdapter> checklistAdapterArrayList = new ArrayList<>();
    private ArrayList<String> recyclerViewNameArrayList = new ArrayList<>();
    private int numCases;
    private String passFail;
    private boolean endOfView, stopCreatingCases = false;
    private ArrayList<Integer> submittedCaseIDs = new ArrayList<>();

    private static DatabaseApiCaller apiCaller;
    private Report thisReport;
    private int userID, tenantID;
    private String token, tenantType, tenantCompany, tenantLocation, tenentInstitution;
    private int reportID = -2;

    //scores
    private float staffhygiene_score, housekeeping_score, safety_score, healthierchoice_score, foodhygiene_score, original_staffhygiene_score,
            original_housekeeping_score, original_safety_score, original_healthierchoice_score, original_foodhygiene_score = 0;

    private float staffhygiene_weightage, housekeeping_weightage, safety_weightage, healthierchoice_weightage, foodhygiene_weightage = 0;

    private Call<Report> reportCall;
    private Call<Case> caseCall;

    private HandlePhotoListener mActivityCallback;
    private HashMap<String, Bitmap> photoBitmapHashMap;
    private int photoNameCounter = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Audit checklist");

        Bundle bundle = getArguments();
        tenantType = bundle.getString("TENANT_TYPE_KEY");
        tenantID = bundle.getInt("ID_KEY");
        tenantCompany = bundle.getString("COMPANY_KEY");
        tenantLocation = bundle.getString("LOCATION_KEY");
        tenentInstitution = bundle.getString("INSTITUTION_KEY");
        Log.d(TAG, "onCreateView: inst: "+tenentInstitution);
        View view = inflateFragmentLayout(tenantType, container, inflater);
        initScoresAndPercentages(tenantType);

        initRecyclerViews(view);

        findAllViews(view);

        setAllListeners();
        initApiCaller();
        loadToken();
        loadUserID();
        new Thread(() -> createReport(false)).start();

        return view;
    }

    private void findAllViews(View view) {
        nestedScrollView = view.findViewById(R.id.nested_scroll_view);

        ProfessionalismAndStaffHygieneTextView = view.findViewById(R.id.ProfessionalismAndStaffHygieneTextView);
        HousekeepingAndGeneralCleanlinessTextView = view.findViewById(R.id.HousekeepingAndGeneralCleanlinessTextView);
        WorkplaceSafetyAndHealthTextView = view.findViewById(R.id.WorkplaceSafetyAndHealthTextView);

        ProfessionalismAndStaffHygieneFAB = view.findViewById(R.id.ProfessionalismAndStaffHygieneFAB);
        HousekeepingAndGeneralCleanlinessFAB = view.findViewById(R.id.HousekeepingAndGeneralCleanlinessFAB);
        WorkplaceSafetyAndHealthFAB = view.findViewById(R.id.WorkplaceSafetyAndHealthFAB);

        submit_audit_button = view.findViewById(R.id.submit_audit_button);
        overall_notes_editText = view.findViewById(R.id.overallReportNotes);

        if (tenantType.equals("F&B")) {
            FoodHygieneFAB = view.findViewById(R.id.FoodHygieneFAB);
            HealthierChoiceFAB = view.findViewById(R.id.HealthierChoiceFAB);
            FoodHygieneTextView = view.findViewById(R.id.FoodHygieneTextView);
            HealthierChoiceTextView = view.findViewById(R.id.HealthierChoiceTextView);
        } else {
            EspressoCountingIdlingResource.decrement();
            EspressoCountingIdlingResource.decrement();
        }
    }

    private void setAllListeners() {
        ProfessionalismAndStaffHygieneFAB.setOnClickListener(v -> focusOnView(ProfessionalismAndStaffHygieneTextView));
        HousekeepingAndGeneralCleanlinessFAB.setOnClickListener(v -> focusOnView(HousekeepingAndGeneralCleanlinessTextView));
        if (tenantType.equals("F&B")) {
            FoodHygieneFAB.setOnClickListener(v -> focusOnView(FoodHygieneTextView));
            HealthierChoiceFAB.setOnClickListener(v -> focusOnView(HealthierChoiceTextView));
        }
        WorkplaceSafetyAndHealthFAB.setOnClickListener(v -> focusOnView(WorkplaceSafetyAndHealthTextView));
        submit_audit_button.setOnClickListener(v -> {
            int count = 0;
            while (reportID < 0) {
                CentralisedToast.makeText(getContext(), "Report not created, \nretrying...", CentralisedToast.LENGTH_SHORT);
                createReport(false);
                count++;
                if (count > 2) {
                    CentralisedToast.makeText(getContext(), "Report could not be created, \nplease check your internet connection.", CentralisedToast.LENGTH_SHORT);
                    return;
                }
            }
            print_scores("Before score calculation");
            try {
                calculateScores();
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                Log.d(TAG, "calculateScores IllegalArgumentException");
            }
            print_scores("After score calculation");
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle("Confirm Submission?")
                    .setMessage("Total non compliance cases: "+numCases+"\nResult: "+passFail)
                    .setCancelable(false)
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resetPhotoNameCounter();
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (createCases()) {
                                submit();
                                createReport(true);
                                clearAllPhotosFromContainerActivity();
                            }
                            dialog.dismiss();
                        }
                    })
                    .show();
        });
    }

    private void print_scores(String msg) {
        Log.d(TAG, "print_scores: "+msg);
        Log.d(TAG, "print_scores{" +
                "staffhygiene_score=" + staffhygiene_score +
                ", housekeeping_score=" + housekeeping_score +
                ", safety_score=" + safety_score +
                ", healthierchoice_score=" + healthierchoice_score +
                ", foodhygiene_score=" + foodhygiene_score +
                '}');
    }

    private void updateToFinalReport() {
        // set all scores
        if (!tenantType.equals("Non F&B")) {
            thisReport.setFoodhygiene_score(round(foodhygiene_score, 2));
            thisReport.setHealthierchoice_score(round(healthierchoice_score, 2));
        }
        thisReport.setHousekeeping_score(round(housekeeping_score, 2));
        thisReport.setSafety_score(round(safety_score, 2));
        thisReport.setStaffhygiene_score(round(staffhygiene_score, 2));
        // check final report
        Log.d(TAG, "updateToFinalReport: "+thisReport.toString());
    }

    private final void focusOnView(TextView tv){
        nestedScrollView.post(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.scrollTo(0, tv.getBottom()-200);
            }
        });
    }

    private void deleteReport() {
        if (reportID > 0) {
            Call<Void> deleteRequest = apiCaller.deleteReport("Token "+token, reportID);
            deleteRequest.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                    Log.d(TAG, "deleteReport onResponse: code: "+response.code());
                }

                @Override
                public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                    Log.d(TAG, "deleteReport onFailure: "+t);
                }
            });
        }
    }

    private void submit() {
        Bundle bundle = new Bundle();
        //keys
        String TITLE_KEY = "title_key";
        bundle.putString(TITLE_KEY, "Confirmation");
        String MSG_KEY = "message_key";
        bundle.putString(MSG_KEY, "Audit Successful!");
        String BUTTON_TXT_KEY = "button_text_key";
        bundle.putString(BUTTON_TXT_KEY, "Return");
        StatusConfirmationFragment statusConfirmationFragment = new StatusConfirmationFragment();
        statusConfirmationFragment.setArguments(bundle);
        AuditChecklistFragment.this.getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.auditor_fragment_container, statusConfirmationFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onBackPressed() {
        if (reportID > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle("Are you sure you want to leave?\nOngoing report will be deleted!")
                    .setCancelable(false)
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteReport();
                            deleteRecentlySubmittedCases();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.auditor_fragment_container, new TenantsPreviewFragment())
                                    .commit();
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
        return true;
    }

    public interface HandlePhotoListener {
        HashMap<String, Bitmap> getPhotoBitmaps();
        void clearCurrentReportPhotoData();
    }

    public interface OnAuditSubmitListener {
        ArrayList<String> sendCases();
        int getNumCases();
    }

    private String getOverallNotes() {
        Log.d(TAG, "getOverallNotes: "+overall_notes_editText.getText().toString());
        return overall_notes_editText.getText().toString();
    }

    private void initApiCaller() {
        apiCaller = new Retrofit.Builder()
                .baseUrl("https://esc10-303807.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);
    }

    private float round(double num, int dec) {
        DecimalFormat df = new DecimalFormat("0.00");
        return Float.parseFloat(df.format(num));
    }

    private void createReport(boolean patch) {
        if (patch) {
            updateToFinalReport();
            Call<Void> patchReport = apiCaller.patchReport("Token " + token, reportID, thisReport);
            patchReport.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                    Log.d(TAG, "patchReport onResponse: code: " + response.code());
                }

                @Override
                public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                    Log.d(TAG, "patchReport onFailure: " + t.toString());
                    Toast.makeText(getActivity(), "Failed to update report scores", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                if (tenantType.equals("F&B")) {
                    reportCall = apiCaller.postNewReport("Token " + token, userID, tenantID, tenantCompany, tenantLocation, tenentInstitution,
                            tenantType, false, getOverallNotes(), null, null, round(staffhygiene_score, 2),
                            round(housekeeping_score, 2), round(safety_score, 2), round(healthierchoice_score, 2),
                            round(foodhygiene_score, 2));
                } else {
                    reportCall = apiCaller.postNewReport("Token " + token, userID, tenantID, tenantCompany, tenantLocation, tenentInstitution,
                            tenantType, false, getOverallNotes(), null, null, round(staffhygiene_score, 2),
                            round(housekeeping_score, 2), round(safety_score, 2), -1, -1);
                }
                reportCall.enqueue(new Callback<Report>() {
                    @Override
                    public void onResponse(Call<Report> call, Response<Report> response) {
                        // sometimes, response comes back as null the first time
                        Log.d(TAG, "onResponse: code: " + response.code());
                        try {
                            thisReport = response.body();
                            reportID = response.body().getId();
                            EspressoCountingIdlingResource.decrement();
                        } catch (Exception e) {
                            Log.d(TAG, "onResponse: report may not have been created");
                            Log.d(TAG, "onResponse: reportID: " + reportID);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<Report> call, Throwable t) {
                        t.printStackTrace();
                        Log.d(TAG, "createReport onFailure: " + t);
                        reportID = -1;
                    }
                });
            }, 500);
        }
    }

    private String getUniquePhotoName() {
        if (reportID > 0) {
            photoNameCounter++;
            return ""+reportID+"_"+photoNameCounter;
        } else {
            Log.d(TAG, "getUniquePhotoName: reportID="+reportID);
            return "";
        }
    }

    private boolean createCases() {
        stopCreatingCases = false;
        photoBitmapHashMap = getAllPhotos();
        for (int i=0; i<checklistAdapterArrayList.size(); i++) { //loop through all the recyclerView adapters
            String non_compliance_type = recyclerViewNameArrayList.get(i); //get the non-compliance type associated with this adapter
            ArrayList<String> caseList = checklistAdapterArrayList.get(i).sendCases(); //get the cases from the recyclerView associated with this adapter
            for (int j=0; j<caseList.size(); j+=2) { //extract the question and comments
                String question = caseList.get(j);
                String comments = caseList.get(j+1);
                Bitmap photoBitmap = photoBitmapHashMap.get(question); // get bitmap using question
                if (photoBitmap == null) {
                    Log.e(TAG, "createCases: photobitmap is null, questions: "+question);
                    handleNullPhoto(question); //delete submitted cases, reset photoNameCounter and prompt photo taking
                    stopCreatingCases = true;
                    return false;
                }
                String photoName = getUniquePhotoName();
                if (photoName.equals("")) {
                    Log.d(TAG, "createCases: error creating unique photo name");
                    stopCreatingCases = true;
                    return false;
                }

                // get date
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                String datetime = dateFormat.format(new Date());

                caseCall = apiCaller.postCase("Token "+token, reportID, tenantID, question, 0, non_compliance_type,
                        photoName, comments, datetime, "");
                caseCall.enqueue(new Callback<Case>() {
                    @Override
                    public void onResponse(@NotNull Call<Case> call, @NotNull Response<Case> response) {
                        Log.d(TAG, "createCases response code: "+response.code());
                        int caseID = response.body().getId();
                        synchronized(submittedCaseIDs) { // make sure we don't have undesirable modifications
                            submittedCaseIDs.add(caseID); // keep track of the caseIDs that have been created
                        }
                        //upload non-null bitmap to database
                        HandleImageOperations.uploadImageToDatabase(photoBitmap, photoName);
                        if (stopCreatingCases) {
                            deleteCase(caseID);
                        }
                    }
                    @Override
                    public void onFailure(@NotNull Call<Case> call, @NotNull Throwable t) {
                        Log.d(TAG, "createCases onFailure: "+t);
                    }
                });
            }
        }
        return true;
    }

    private void deleteCase(int caseID) {
        Log.d(TAG, "deleteCase: called");
        Call<Void> deleteRequest = apiCaller.deleteCase("Token "+token, caseID);
        deleteRequest.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                Log.d(TAG, "deleteCase onResponse: code: "+response.code());
            }

            @Override
            public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                Log.d(TAG, "deleteCase onFailure: "+t);
            }
        });
    }

    private void resetPhotoNameCounter() {
        photoNameCounter = 0;
    }

    private void deleteRecentlySubmittedCases() {
        Log.d(TAG, "deleteRecentlySubmittedCases: called");
        for (int caseID : submittedCaseIDs) {
            Log.d(TAG, "deleteRecentlySubmittedCases: id: "+caseID);
            deleteCase(caseID);
        }
    }

    private void handleNullPhoto(String question) {
        CentralisedToast.makeText(getContext(), "Please take a photo for all non-compliance cases.", CentralisedToast.LENGTH_LONG);
        deleteRecentlySubmittedCases();
        resetPhotoNameCounter();
        ArrayList<View> outViews = new ArrayList<View>();
        getView().findViewsWithText(outViews, question, View.FIND_VIEWS_WITH_TEXT);
        if (outViews.size() > 1) {
            Log.d(TAG, "onClick: question may be contained in another question!");
        }
        Log.d(TAG, "handleNullPhoto: view qn found: "+((TextView)outViews.get(0)).getText());
        View viewToFocusOn = outViews.get(0);
        viewToFocusOn.getParent().requestChildFocus(viewToFocusOn,viewToFocusOn);
        ((CardView)viewToFocusOn.getParent().getParent()).setCardBackgroundColor(0x4Deb3434);
        // TODO: fix crash when user comes back from camera intent after clicking camera after handleNullPhoto is invoked
//        viewToFocusOn.clearFocus(); // otherwise the app will crash once it returns from a camera intent
//        Thread t = new Thread(() -> {
//            int focusTryCount = 0;
//            while (focusTryCount < 20 && !viewToFocusOn.requestFocus()) {
//                focusTryCount++;
//                System.out.println("failed to focus");
//            }
//            System.out.println("finished trying to focus");
//        });
    }

    private void reInitScores() {
        staffhygiene_score=original_staffhygiene_score;
        housekeeping_score=original_housekeeping_score;
        foodhygiene_score=original_foodhygiene_score;
        healthierchoice_score=original_healthierchoice_score;
        safety_score=original_safety_score;
    }

    private void calculateScores() throws IllegalArgumentException {
        numCases = 0;
        reInitScores();
        if (checklistAdapterArrayList.size() != recyclerViewNameArrayList.size()) {
            Log.d(TAG, "calculateScores: something went wrong when creating recyclerViews");
            return;
        }
        for (int i=0; i<checklistAdapterArrayList.size(); i++) {
            String name = recyclerViewNameArrayList.get(i);
            int currentChecklistNumCases = checklistAdapterArrayList.get(i).getNumCases();
            numCases += currentChecklistNumCases;

            switch (name) {
                case "Professional & Staff Hygiene":
                    staffhygiene_score-=currentChecklistNumCases;
                    break;
                case "Housekeeping & General Cleanliness":
                    housekeeping_score-=currentChecklistNumCases;
                    break;
                case "Healthier Choice":
                    foodhygiene_score-=currentChecklistNumCases;
                    break;
                case "Food Hygiene":
                    healthierchoice_score-=currentChecklistNumCases;
                    break;
                case "Workplace Safety & Health":
                    safety_score-=currentChecklistNumCases;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        // calculate scores
        staffhygiene_score = staffhygiene_score* staffhygiene_weightage /original_staffhygiene_score;
        housekeeping_score = housekeeping_score* housekeeping_weightage /original_housekeeping_score;
        safety_score = safety_score* safety_weightage /original_safety_score;
        if (tenantType.equals("F&B")) {
            healthierchoice_score = healthierchoice_score * healthierchoice_weightage / original_healthierchoice_score;
            foodhygiene_score = foodhygiene_score * foodhygiene_weightage / original_foodhygiene_score;
        }

        // decide whether pass or fail
        if (staffhygiene_score+housekeeping_score+safety_score+healthierchoice_score+foodhygiene_score < 0.95) {
            passFail = "Fail";
        } else {
            passFail = "Pass";
        }
    }

    private synchronized void init_recyclerView(RecyclerView recyclerView, ArrayList<ChecklistItem> list, String recyclerViewName) {
        ChecklistAdapter checklistAdapter;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        checklistAdapter = new ChecklistAdapter((HandlePhotoInterface)getActivity(), list, true);
        recyclerView.setAdapter(checklistAdapter);
        checklistAdapterArrayList.add(checklistAdapter);
        recyclerViewNameArrayList.add(recyclerViewName);
        EspressoCountingIdlingResource.decrement();
    }

    private String getRecyclerViewName(String subHeader) {
        switch (subHeader) {
            case "Professionalism": case "Staff Hygiene":
                return "Professional & Staff Hygiene";
            case "General Environment Cleanliness":case "Hand Hygiene Facilities":
                return "Housekeeping & General Cleanliness";
            case "Storage & Preparation of Food":case "Storage of Food in Refrigerator/ Warmer":
                return "Healthier Choice";
            case "Food":case "Beverage":
                return "Food Hygiene";
            case "General Safety":case "Fire & Emergency Safety":case "Electrical Safety":
                return "Workplace Safety & Health";
            default:
                Log.d(TAG, "getRecyclerViewName: error, check subHeaders");
                throw new IllegalArgumentException();

        }
    }

    private class CreateRecyclerViewThread implements Runnable {
        private View view;
        private String pathName;
        private boolean FIRST_SUB_HEADER = true;
        RecyclerView recyclerView = null;
        String currentRecyclerViewName = null;
        ArrayList<String> lines;
        ArrayList<ChecklistItem> checklistArray = new ArrayList<>();

        public CreateRecyclerViewThread(View view, String pathName) {
            this.view = view;
            this.pathName = pathName;
        }

        @Override
        public void run() {
            QuestionBank qb = new QuestionBank(getActivity());
            synchronized(qb) {
                lines = qb.getQuestions(pathName);
            }
            for (String line : lines) {
                if (Character.compare(line.charAt(0), ('-')) == 0) { // it is the name of a sub-header
                    if (!FIRST_SUB_HEADER) { // initialise the recyclerView with the completed checklist array
                        init_recyclerView(recyclerView, checklistArray, currentRecyclerViewName);
                        checklistArray = new ArrayList<>();
                    }
                    try {
                        recyclerView = getCorrespondingRecyclerView(view, line.substring(1));
                        currentRecyclerViewName = getRecyclerViewName(line.substring(1));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    FIRST_SUB_HEADER = false;
                } else { // it is a question
                    if (Character.compare(line.charAt(0), '>') == 0) {
                        ChecklistItem item = checklistArray.get(checklistArray.size() - 1);
                        item.setStatement(item.getStatement()+"\n"+line);
                    } else {
                        checklistArray.add(new ChecklistItem(line, ""));
                    }
                }
            }
            init_recyclerView(recyclerView, checklistArray, currentRecyclerViewName);
        }
    }

    private RecyclerView getCorrespondingRecyclerView(View view, String subHeader) throws IllegalArgumentException {
        /**
        * Returns recyclerView corresponding to the subHeader given
        * */
        switch (subHeader) {
            case "Professionalism":
                return view.findViewById(R.id.audit_checklist_recyclerview_professionalism);
            case "Staff Hygiene":
                return view.findViewById(R.id.audit_checklist_recyclerview_staff_hygiene);
            case "General Environment Cleanliness":
                return view.findViewById(R.id.audit_checklist_recyclerview_environment);
            case "Hand Hygiene Facilities":
                return view.findViewById(R.id.audit_checklist_recyclerview_hand_hygiene);
            case "Storage & Preparation of Food":
                return view.findViewById(R.id.audit_checklist_recyclerview_food_prep);
            case "Storage of Food in Refrigerator/ Warmer":
                return view.findViewById(R.id.audit_checklist_recyclerview_food_storage);
            case "Food":
                return view.findViewById(R.id.audit_checklist_recyclerview_food_hpb);
            case "Beverage":
                return view.findViewById(R.id.audit_checklist_recyclerview_beverage_hpb);
            case "General Safety":
                return view.findViewById(R.id.audit_checklist_recyclerview_general_safety);
            case "Fire & Emergency Safety":
                return view.findViewById(R.id.audit_checklist_recyclerview_fire_safety);
            case "Electrical Safety":
                return view.findViewById(R.id.audit_checklist_recyclerview_electricity_safety);
            default:
                throw new IllegalArgumentException();
        }
    }

    private View inflateFragmentLayout(String tenantType, ViewGroup container, LayoutInflater inflater) {
        // decides which fragment to inflate
        View view;
        if (tenantType.equals("F&B")) {
            Log.d(TAG, "onCreateView: setting up for F&B");
            view = inflater.inflate(R.layout.f_checklist_audit_fb, container, false);
            header_files = new String[]{"F&B_food_hygiene.txt", "F&B_healthier_choice.txt", "F&B_professionalism_and_staff_hygiene.txt", "F&B_workplace_safety_and_health.txt", "F&B_housekeeping_and_general_cleanliness.txt"};
        } else if (tenantType.equals("Non F&B")) {
            Log.d(TAG, "onCreateView: setting up for Non F&B");
            view = inflater.inflate(R.layout.f_checklist_audit_nfb, container, false);
            header_files = new String[]{"Non_F&B_professionalism_and_staff_hygiene.txt", "Non_F&B_workplace_safety_and_health.txt", "Non_F&B_housekeeping_and_general_cleanliness.txt"};
        } else {
            Log.d(TAG, "onCreateView: invalid tenant type: "+tenantType);
            Log.d(TAG, "inflateFragmentLayout: maybe check file names?");
            view = inflater.inflate(R.layout.f_checklist_audit_fb, container, false);
        }

        return view;
    }

    private void initRecyclerViews(View view) {
        ArrayList<Thread> threadArrayList = new ArrayList<>();
        // initialize and fill all recyclerViews using text files in assets directory
        for (String pathName : header_files) {
            CreateRecyclerViewThread createRecyclerViewThread = new CreateRecyclerViewThread(view, pathName);
            Thread thread = new Thread(createRecyclerViewThread);
            threadArrayList.add(thread);
            thread.start();
        }
    }

    private void loadToken() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        String TOKEN_KEY = "TOKEN_KEY";
        token = sharedPreferences.getString(TOKEN_KEY, null);
    }

    private void loadUserID() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        String USER_ID_KEY = "USER_ID_KEY";
        userID = sharedPreferences.getInt(USER_ID_KEY, -1);
    }

    private void initScoresAndPercentages(String tenantType) {
        if (tenantType.equals("F&B")) {
            staffhygiene_score = 13;
            housekeeping_score = 17;
            safety_score = 18;
            healthierchoice_score = 11;
            foodhygiene_score = 37;
            original_staffhygiene_score = 13;
            original_housekeeping_score = 17;
            original_safety_score = 18;
            original_healthierchoice_score = 11;
            original_foodhygiene_score = 37;
            staffhygiene_weightage = 0.10f;
            housekeeping_weightage = 0.20f;
            safety_weightage = 0.20f;
            healthierchoice_weightage = 0.15f;
            foodhygiene_weightage = 0.35f;
        } else if (tenantType.equals("Non F&B")) {
            staffhygiene_score = 6;
            housekeeping_score = 12;
            safety_score = 16;
            original_staffhygiene_score = 6;
            original_housekeeping_score = 12;
            original_safety_score = 16;
            staffhygiene_weightage = 0.20f;
            housekeeping_weightage = 0.40f;
            safety_weightage = 0.40f;
        } else {
            Log.d(TAG, "initScoresAndPercentages: not set, tenant type: "+tenantType);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mActivityCallback = (HandlePhotoListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement HandlePhotoListener");
        }
    }

    private HashMap<String, Bitmap> getAllPhotos() {
        return mActivityCallback.getPhotoBitmaps();
    }

    private void clearAllPhotosFromContainerActivity() {
        mActivityCallback.clearCurrentReportPhotoData();
    }
}
