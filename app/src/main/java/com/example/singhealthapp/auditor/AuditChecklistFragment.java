package com.example.singhealthapp.auditor;

import android.os.Bundle;
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
import com.example.singhealthapp.auditor.Adapters.ChecklistAdapter;

import java.util.ArrayList;

public class AuditChecklistFragment extends Fragment {
    private static final String TAG = "AuditChecklistFragment";

    private ChecklistAdapter checklistAdapter;

    Button submit_audit_button;

    private static final String TENANT_TYPE_KEY = "tenant_type_key";
    private String[] header_files;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Audit checklist");

        Bundle bundle = getArguments();
        String tenant_type = bundle.getString(TENANT_TYPE_KEY);
        View view = inflateFrgamentLayout(tenant_type, container, inflater);

        initRecyclerViews(view);

        submit_audit_button = view.findViewById(R.id.submit_audit_button);
        submit_audit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuditChecklistFragment.this.getParentFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, new AuditChecklistFragment()).commit();
            }
        });

        return view;
    }

    private void init_recyclerView(RecyclerView recyclerView, ArrayList<Checklist_item> list) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        checklistAdapter = new ChecklistAdapter(list);
        recyclerView.setAdapter(checklistAdapter);
    }

    private void addToChecklist(ArrayList<Checklist_item> list, String statement) {
        list.add(new Checklist_item(statement, ""));
        checklistAdapter.notifyDataSetChanged();
    }

    private void initChecklistSection(View view, String pathName) {
        /*
        * Reads a formatted text file and creates RecyclerViews for the checklists corresponding to the file
        * */
        boolean FIRST_SUB_HEADER = true;
        RecyclerView recyclerView = null;
        ArrayList<String> lines;
        ArrayList<Checklist_item> checklistArray = new ArrayList<>();
        QuestionBank qb = new QuestionBank(getActivity());
        lines = qb.getQuestions(pathName);
        Log.d(TAG, "initChecklistSection: first line: "+lines.get(0));
        for (String line : lines) {
            if (Character.compare(line.charAt(0), ('-')) == 0) { // it is the name of a sub-header
                if (!FIRST_SUB_HEADER) { // initialise the recyclerView with the completed checklist array
                    Log.d(TAG, "checklistArray first statement: "+checklistArray.get(0).getStatement());
                    Log.d(TAG, "initChecklistSection: current recyclerview: "+recyclerView.toString());
                    init_recyclerView(recyclerView, checklistArray);
                    checklistArray.clear();
                    Log.d(TAG, "checklistArray number of else after clearing: "+checklistArray.size());
                }
                Log.d(TAG, "initChecklistSection: getting recyclerview using sub-header: "+line.substring(1));
                try {
                    recyclerView = getCorrespondingRecyclerView(view, line.substring(1));
                    Log.d(TAG, "initChecklistSection: new recyclerview: "+recyclerView.toString());
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "initChecklistSection: ", e);
                    e.printStackTrace();
                }
                FIRST_SUB_HEADER = false;
            } else { // it is a question
                if (Character.compare(line.charAt(0), '>') == 0) {
                    Checklist_item item = checklistArray.get(checklistArray.size() - 1);
                    item.setStatement(item.getStatement()+"\n\n"+line);
                } else {
                    checklistArray.add(new Checklist_item(line, ""));
                }
            }
        }
        // init the last one
        Log.d(TAG, "initChecklistSection: current recyclerview: "+recyclerView.toString());
        Log.d(TAG, "checklistArray first statement: "+checklistArray.get(0).getStatement());
        init_recyclerView(recyclerView, checklistArray);
    }

    private RecyclerView getCorrespondingRecyclerView(View view, String subHeader) throws IllegalArgumentException {
        /*
        * Returns recyclerView corresponding to the subHeader given
        * */

        switch (subHeader) {
            case "Professionalism":
                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: professionalism");
                return view.findViewById(R.id.audit_checklist_recyclerview_professionalism);
            case "Staff Hygiene":
                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: Staff Hygiene");
                return view.findViewById(R.id.audit_checklist_recyclerview_staff_hygiene);
            case "General Environment Cleanliness":
                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: General Environment Cleanliness");
                return view.findViewById(R.id.audit_checklist_recyclerview_environment);
            case "Hand Hygiene Facilities":
                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: Hand Hygiene Facilities");
                return view.findViewById(R.id.audit_checklist_recyclerview_hand_hygiene);
            case "Storage & Preparation of Food":
                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: Storage & Preparation of Food");
                return view.findViewById(R.id.audit_checklist_recyclerview_food_prep);
            case "Storage of Food in Refrigerator/ Warmer":
                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: Storage of Food in Refrigerator/ Warmer");
                return view.findViewById(R.id.audit_checklist_recyclerview_food_storage);
            case "Food":
                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: Food");
                return view.findViewById(R.id.audit_checklist_recyclerview_food_hpb);
            case "Beverage":
                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: Beverage");
                return view.findViewById(R.id.audit_checklist_recyclerview_beverage_hpb);
            case "General Safety":
                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: General Safety");
                return view.findViewById(R.id.audit_checklist_recyclerview_general_safety);
            case "Fire & Emergency Safety":
                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: Fire & Emergency Safety");
                return view.findViewById(R.id.audit_checklist_recyclerview_fire_safety);
            case "Electrical Safety":
                Log.d(TAG, "getCorrespondingRecyclerView: returning recyclerview: Electrical Safety");
                return view.findViewById(R.id.audit_checklist_recyclerview_electricity_safety);
            default:
                throw new IllegalArgumentException();
        }
    }

    private View inflateFrgamentLayout(String tenant_type, ViewGroup container, LayoutInflater inflater) {
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

}
