package com.example.singhealthapp.auditor;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.HelperClasses.CentralisedToast;
import com.example.singhealthapp.Models.Checklist_item;
import com.example.singhealthapp.R;
import com.example.singhealthapp.StatusConfirmationFragment;
import com.example.singhealthapp.auditor.Adapters.ChecklistAdapter;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class AuditChecklistFragment extends Fragment {
    public static final String TAG = "AuditChecklistFragment";

    Button submit_audit_button;

    private static final String TENANT_TYPE_KEY = "tenant_type_key";
    private String[] header_files;

    private final String TITLE_KEY = "title_key";
    private final String MSG_KEY = "message_key";
    private final String BUTTON_TXT_KEY = "button_text_key";

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Audit checklist");

        Bundle bundle = getArguments();
        String tenant_type = bundle.getString(TENANT_TYPE_KEY);
        View view = inflateFragmentLayout(tenant_type, container, inflater);

        initRecyclerViews(view);

        submit_audit_button = view.findViewById(R.id.submit_audit_button);
        submit_audit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(TITLE_KEY, "Audit Submitted");
                bundle.putString(MSG_KEY, "Audit Complete!");
                bundle.putString(BUTTON_TXT_KEY, "Return");
                StatusConfirmationFragment statusConfirmationFragment = new StatusConfirmationFragment();
                statusConfirmationFragment.setArguments(bundle);
                Bundle bundleReply = statusConfirmationFragment.getArguments();
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

        return view;
    }

    private void init_recyclerView(RecyclerView recyclerView, ArrayList<Checklist_item> list) {
        ChecklistAdapter checklistAdapter;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        checklistAdapter = new ChecklistAdapter((TakePhotoInterface)getActivity(), list);
        recyclerView.setAdapter(checklistAdapter);
    }

    private void initChecklistSection(View view, String pathName) {
        /*
         * 1. Gets an ArrayList of questions for each sub-header in a section specified by the pathName
         * 2. Matches each ArrayList to a recyclerView
         * 3. Initialises the recyclerView
         * */
        boolean FIRST_SUB_HEADER = true;
        RecyclerView recyclerView = null;
        ArrayList<String> lines;
        ArrayList<Checklist_item> checklistArray = new ArrayList<>();
        QuestionBank qb = new QuestionBank(getActivity());
        lines = qb.getQuestions(pathName);
        for (String line : lines) {
            if (Character.compare(line.charAt(0), ('-')) == 0) { // it is the name of a sub-header
                if (!FIRST_SUB_HEADER) { // initialise the recyclerView with the completed checklist array
                    init_recyclerView(recyclerView, checklistArray);
                    checklistArray = new ArrayList<>();
                }
                try {
                    recyclerView = getCorrespondingRecyclerView(view, line.substring(1));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                FIRST_SUB_HEADER = false;
            } else { // it is a question
                if (Character.compare(line.charAt(0), '>') == 0) {
                    Checklist_item item = checklistArray.get(checklistArray.size() - 1);
                    item.setStatement(item.getStatement()+"\n"+line);
                } else {
                    checklistArray.add(new Checklist_item(line, ""));
                }
            }
        }
        init_recyclerView(recyclerView, checklistArray);
    }

    private RecyclerView getCorrespondingRecyclerView(View view, String subHeader) throws IllegalArgumentException {
        /*
        * Returns recyclerView corresponding to the subHeader given
        * */

        switch (subHeader) {
            case "Professionalism":
//                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: professionalism");
                return view.findViewById(R.id.audit_checklist_recyclerview_professionalism);
            case "Staff Hygiene":
//                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: Staff Hygiene");
                return view.findViewById(R.id.audit_checklist_recyclerview_staff_hygiene);
            case "General Environment Cleanliness":
//                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: General Environment Cleanliness");
                return view.findViewById(R.id.audit_checklist_recyclerview_environment);
            case "Hand Hygiene Facilities":
//                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: Hand Hygiene Facilities");
                return view.findViewById(R.id.audit_checklist_recyclerview_hand_hygiene);
            case "Storage & Preparation of Food":
//                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: Storage & Preparation of Food");
                return view.findViewById(R.id.audit_checklist_recyclerview_food_prep);
            case "Storage of Food in Refrigerator/ Warmer":
//                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: Storage of Food in Refrigerator/ Warmer");
                return view.findViewById(R.id.audit_checklist_recyclerview_food_storage);
            case "Food":
//                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: Food");
                return view.findViewById(R.id.audit_checklist_recyclerview_food_hpb);
            case "Beverage":
//                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: Beverage");
                return view.findViewById(R.id.audit_checklist_recyclerview_beverage_hpb);
            case "General Safety":
//                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: General Safety");
                return view.findViewById(R.id.audit_checklist_recyclerview_general_safety);
            case "Fire & Emergency Safety":
//                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: Fire & Emergency Safety");
                return view.findViewById(R.id.audit_checklist_recyclerview_fire_safety);
            case "Electrical Safety":
//                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: Electrical Safety");
                return view.findViewById(R.id.audit_checklist_recyclerview_electricity_safety);
            default:
                throw new IllegalArgumentException();
        }
    }

    private View inflateFragmentLayout(String tenant_type, ViewGroup container, LayoutInflater inflater) {
        // decides which fragment to inflate
        View view;
        if (tenant_type.toLowerCase().equals("fb")) {
            Log.d(TAG, "onCreateView: setting up for fb");
            view = inflater.inflate(R.layout.fragment_fb_audit_checklist, container, false);
            header_files = new String[]{"fb_food_hygiene.txt", "fb_healthier_choice.txt", "fb_professionalism_and_staff_hygiene.txt", "fb_workplace_safety_and_health.txt", "fb_housekeeping_and_general_cleanliness.txt"};
        } else if (tenant_type.toLowerCase().equals("nfb")) {
            Log.d(TAG, "onCreateView: setting up for nfb");
            view = inflater.inflate(R.layout.fragment_nfb_audit_checklist, container, false);
            header_files = new String[]{"nfb_professionalism_and_staff_hygiene.txt", "nfb_workplace_safety_and_health.txt", "nfb_housekeeping_and_general_cleanliness.txt"};
        } else {
            CentralisedToast.makeText(getContext(), "Error verifying tenant type, defaulting to Food and Beverage type", CentralisedToast.LENGTH_LONG);
            Log.d(TAG, "onCreateView: invalid tenant type: "+tenant_type);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

        }
    }

//    @Override
//    public void onPhotoReturnListener(ImageButton cameraButton) {
//        ActivityResultLauncher<Intent> takePhotoResultLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if (result.getResultCode() == Activity.RESULT_OK) {
//                            Intent data = result.getData();
//                            Bundle extras = data.getExtras();
//                            Bitmap imageBitmap = (Bitmap) extras.get("data");
//                            cameraButton.setBackground(null);
//                            cameraButton.setImageBitmap(imageBitmap);
//                        }
//                    }
//                });
//
//        openCameraForResult(takePhotoResultLauncher);
//    }
//
//    private void openCameraForResult(ActivityResultLauncher<Intent> takePhotoResultLauncher) {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        String chooser_title = "Take picture with";
//        Intent chooser = Intent.createChooser(takePictureIntent, chooser_title);
//        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
//            takePhotoResultLauncher.launch(chooser);
//        } else {
//            CentralisedToast.makeText(getContext(), "Unable to find camera", CentralisedToast.LENGTH_SHORT);
//        }
//    }
}
