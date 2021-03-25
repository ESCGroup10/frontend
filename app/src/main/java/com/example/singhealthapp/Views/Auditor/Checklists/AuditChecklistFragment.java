package com.example.singhealthapp.Views.Auditor.Checklists;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.Containers.AuditorFragmentContainer;
import com.example.singhealthapp.HelperClasses.CentralisedToast;
import com.example.singhealthapp.HelperClasses.TakePhotoInterface;
import com.example.singhealthapp.HelperClasses.QuestionBank;
import com.example.singhealthapp.Models.Case;
import com.example.singhealthapp.Models.ChecklistItem;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Auditor.StatusConfirmation.StatusConfirmationFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuditChecklistFragment extends Fragment {
    public static final String TAG = "AuditChecklistFragment";

    Button submit_audit_button;
    EditText overall_notes_editText;

    private static final String TENANT_TYPE_KEY = "tenant_type_key";
    private String[] header_files;
    private ArrayList<ChecklistAdapter> checklistAdapterArrayList = new ArrayList<>();
    private ArrayList<String> recyclerViewNameArrayList = new ArrayList<>();
    String formattedDate;

    //keys
    private final String TITLE_KEY = "title_key";
    private final String MSG_KEY = "message_key";
    private final String BUTTON_TXT_KEY = "button_text_key";
    private final String USER_ID_KEY = "USER_ID_KEY";
    private final String TOKEN_KEY = "TOKEN_KEY";

    private static DatabaseApiCaller apiCaller;
    private String token;
    private int userID;
    private int tenantID;
    String tenantType;

    //scores
    private float staff_hygiene_score = 0;
    private float housekeeping_score = 0;
    private float safety_score = 0;
    private float healthierchoice_score = 0;
    private float foodhygiene_score = 0;
    private float original_staff_hygiene_score = 0;
    private float original_housekeeping_score = 0;
    private float original_safety_score = 0;
    private float original_healthierchoice_score = 0;
    private float original_foodhygiene_score = 0;

    private int staff_hygiene_percent_weightage = 0;
    private int housekeeping_percent_weightage = 0;
    private int safety_percent_weightage = 0;
    private int healthierchoice_percent_weightage = 0;
    private int foodhygiene_percent_weightage = 0;

    Call<ResponseBody> reportCall;
    Call<ResponseBody> caseCall;

    HandlePhotoListener mActivityCallback;
    HashMap questionAndPhotoPathHashMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Audit checklist");

        Bundle bundle = getArguments();
        tenantType = bundle.getString(TENANT_TYPE_KEY);
        View view = inflateFragmentLayout(tenantType, container, inflater);
        initScoresAndPercentages(tenantType);

        initRecyclerViews(view);

        submit_audit_button = view.findViewById(R.id.submit_audit_button);
        submit_audit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int reportID = createReport();
                if (reportID == -1) {
                    Log.d(TAG, "onClick: error in calculating scores");
                    return;
                } else if (reportID == -2) {
                    Log.d(TAG, "onClick: error in getting response from database");
                    return;
                } else {
                    createCases(reportID);
                    clearAllPhotosFromContainerActivity();
                }
                Bundle bundle = new Bundle();
                bundle.putString(TITLE_KEY, "Audit Submitted");
                bundle.putString(MSG_KEY, "Audit Complete!");
                bundle.putString(BUTTON_TXT_KEY, "Return");
                StatusConfirmationFragment statusConfirmationFragment = new StatusConfirmationFragment();
                statusConfirmationFragment.setArguments(bundle);
                Log.d(TAG, "title: "+bundle.getString(TITLE_KEY));
                Log.d(TAG, "msg: "+bundle.getString(MSG_KEY));
                Log.d(TAG, "button text: "+bundle.getString(BUTTON_TXT_KEY));
                AuditChecklistFragment.this.getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.auditor_fragment_container, statusConfirmationFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        overall_notes_editText = view.findViewById(R.id.overallReportNotes);

        return view;
    }

    public interface HandlePhotoListener {
        HashMap getPhotoPathHashMap();
        void clearPhotoPathHashMap();
    }

    private String getOverallNotes() {
        return overall_notes_editText.getText().toString();
    }

    public interface OnAuditSubmitListener {
        ArrayList<String> sendCases();
    }

    private void initApiCaller() {
        apiCaller = new Retrofit.Builder()
                .baseUrl("https://esc10-303807.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);
    }

    private String company;
    private String location;
    private String outletType;

    public int createReport() {
        /*
        * Usage:
        * - Creates a report in the db
        * Returns:
        * - db generated report ID
        * */
        initApiCaller();
        loadToken();
        loadUserID();
        try {
            calculateScores();
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "calculateScores IllegalArgumentException: "+ex);
            return -1;
        }
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        formattedDate = df.format(c);
        reportCall = apiCaller.postNewReport("Token "+token, userID, tenantID, company, location, tenantType,
                false, getOverallNotes(), formattedDate, null, staff_hygiene_score,
                housekeeping_score, safety_score, healthierchoice_score, foodhygiene_score);

        int reportID=-2;
        reportCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String body = response.body().toString();
                Log.d(TAG, "onResponse: body: "+body);
                //reportID = body
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.toString());
            }
        });
        return reportID;
    }

    private void createCases(int reportID) {
        for (int i=0; i<checklistAdapterArrayList.size(); i+=2) {
            String non_compliance_type = recyclerViewNameArrayList.get(i);
            ArrayList<String> caseList = checklistAdapterArrayList.get(i).sendCases();
            for (int j=0; j<caseList.size(); j+=2) {
                String question = caseList.remove(j);
                String comments = caseList.remove(j);
                questionAndPhotoPathHashMap = getAllPhotos();
                if (formattedDate == null) {
                    Log.d(TAG, "createCases error: call createReport before createCases! i="+i+" j="+j);
                    return;
                }
                caseCall = apiCaller.postCase("Token "+token, reportID, question, false, non_compliance_type,
                        (String)questionAndPhotoPathHashMap.get(question), comments, formattedDate, null,
                        null, null);
            }
        }

    }

    private void calculateScores() throws IllegalArgumentException {
        if (checklistAdapterArrayList.size()/2 != recyclerViewNameArrayList.size()) {
            Log.d(TAG, "calculateScores: something went wrong when creating recyclerviews");
            return;
        }
        for (int i=0; i<checklistAdapterArrayList.size(); i+=2) {
            String name = recyclerViewNameArrayList.get(i);
            ArrayList<String> caseList = checklistAdapterArrayList.get(i).sendCases();

            switch (name) {
                case "Professionalism":
                    staff_hygiene_score-=(caseList.size()/2);
                    break;
                case "Staff Hygiene":
                    staff_hygiene_score-=(caseList.size()/2);
                    break;
                case "General Environment Cleanliness":
                    housekeeping_score-=(caseList.size()/2);
                    break;
                case "Hand Hygiene Facilities":
                    housekeeping_score-=(caseList.size()/2);
                    break;
                case "Storage & Preparation of Food":
                    foodhygiene_score-=(caseList.size()/2);
                    break;
                case "Storage of Food in Refrigerator/ Warmer":
                    foodhygiene_score-=(caseList.size()/2);
                    break;
                case "Food":
                    healthierchoice_score-=(caseList.size()/2);
                    break;
                case "Beverage":
                    healthierchoice_score-=(caseList.size()/2);
                    break;
                case "General Safety":
                    safety_score-=(caseList.size()/2);
                    break;
                case "Fire & Emergency Safety":
                    safety_score-=(caseList.size()/2);
                    break;
                case "Electrical Safety":
                    safety_score-=(caseList.size()/2);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        staff_hygiene_score = staff_hygiene_score*staff_hygiene_percent_weightage/original_staff_hygiene_score;
        housekeeping_score = housekeeping_score*housekeeping_percent_weightage/original_housekeeping_score;
        safety_score = safety_score*safety_percent_weightage/original_safety_score;
        healthierchoice_score = healthierchoice_percent_weightage*healthierchoice_percent_weightage/original_healthierchoice_score;
        foodhygiene_score = foodhygiene_score*foodhygiene_percent_weightage/original_foodhygiene_score;
    }

    private void init_recyclerView(RecyclerView recyclerView, ArrayList<ChecklistItem> list, String recyclerViewName) {
        ChecklistAdapter checklistAdapter;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        checklistAdapter = new ChecklistAdapter((TakePhotoInterface)getActivity(), list);
        recyclerView.setAdapter(checklistAdapter);
        checklistAdapterArrayList.add(checklistAdapter);
        recyclerViewNameArrayList.add(recyclerViewName);
    }

    private void initChecklistSection(View view, String pathName) {
        /*
         * 1. Gets an ArrayList of questions for each sub-header in a section specified by the pathName
         * 2. Matches each ArrayList to a recyclerView
         * 3. Initialises the recyclerView
         * */
        boolean FIRST_SUB_HEADER = true;
        RecyclerView recyclerView = null;
        String currentRecyclerViewName = null;
        ArrayList<String> lines;
        ArrayList<ChecklistItem> checklistArray = new ArrayList<>();
        QuestionBank qb = new QuestionBank(getActivity());
        lines = qb.getQuestions(pathName);
        for (String line : lines) {
            if (Character.compare(line.charAt(0), ('-')) == 0) { // it is the name of a sub-header
                if (!FIRST_SUB_HEADER) { // initialise the recyclerView with the completed checklist array
                    init_recyclerView(recyclerView, checklistArray, currentRecyclerViewName);
                    checklistArray = new ArrayList<>();
                }
                try {
                    recyclerView = getCorrespondingRecyclerView(view, line.substring(1));
                    currentRecyclerViewName = line.substring(1);
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

    private RecyclerView getCorrespondingRecyclerView(View view, String subHeader) throws IllegalArgumentException {
        /*
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
        if (tenantType.toLowerCase().equals("fb")) {
            Log.d(TAG, "onCreateView: setting up for fb");
            view = inflater.inflate(R.layout.fragment_fb_audit_checklist, container, false);
            header_files = new String[]{"fb_food_hygiene.txt", "fb_healthier_choice.txt", "fb_professionalism_and_staff_hygiene.txt", "fb_workplace_safety_and_health.txt", "fb_housekeeping_and_general_cleanliness.txt"};
        } else if (tenantType.toLowerCase().equals("nfb")) {
            Log.d(TAG, "onCreateView: setting up for nfb");
            view = inflater.inflate(R.layout.fragment_nfb_audit_checklist, container, false);
            header_files = new String[]{"nfb_professionalism_and_staff_hygiene.txt", "nfb_workplace_safety_and_health.txt", "nfb_housekeeping_and_general_cleanliness.txt"};
        } else {
            CentralisedToast.makeText(getContext(), "Error verifying tenant type, defaulting to Food and Beverage type", CentralisedToast.LENGTH_LONG);
            Log.d(TAG, "onCreateView: invalid tenant type: "+tenantType);
            view = inflater.inflate(R.layout.fragment_fb_audit_checklist, container, false);
        }

        return view;
    }

    private void initRecyclerViews(View view) {
        // initialize and fill all recyclerViews using text files in assets directory
        for (String pathName : header_files) {
            Log.d(TAG, "onCreateView: init checklist section for: "+pathName);
            initChecklistSection(view, pathName);
        }
    }

    private void loadToken() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        token = sharedPreferences.getString(TOKEN_KEY, null);
    }

    private void loadUserID() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        userID = sharedPreferences.getInt(USER_ID_KEY, -1);
    }

    private void initScoresAndPercentages(String tenantType) {
        if (tenantType.toLowerCase().equals("fb")) {
            staff_hygiene_score = 13;
            housekeeping_score = 17;
            safety_score = 18;
            healthierchoice_score = 11;
            foodhygiene_score = 37;
            original_staff_hygiene_score = 13;
            original_housekeeping_score = 17;
            original_safety_score = 18;
            original_healthierchoice_score = 11;
            original_foodhygiene_score = 37;
            staff_hygiene_percent_weightage = 10;
            housekeeping_percent_weightage = 20;
            safety_percent_weightage = 20;
            healthierchoice_percent_weightage = 15;
            foodhygiene_percent_weightage = 35;
        } else if (tenantType.toLowerCase().equals("nfb")) {
            staff_hygiene_score = 6;
            housekeeping_score = 12;
            safety_score = 16;
            original_staff_hygiene_score = 6;
            original_housekeeping_score = 12;
            original_safety_score = 16;
            staff_hygiene_percent_weightage = 20;
            housekeeping_percent_weightage = 40;
            safety_percent_weightage = 40;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mActivityCallback = (HandlePhotoListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement HeadlineListener");
        }
    }

    private HashMap getAllPhotos() {
        return mActivityCallback.getPhotoPathHashMap();
    }

    private void clearAllPhotosFromContainerActivity() {
        mActivityCallback.clearPhotoPathHashMap();
    }
}
