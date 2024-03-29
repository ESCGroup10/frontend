package com.example.singhealthapp.Views.Auditor.Checklists;

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

import com.example.singhealthapp.HelperClasses.CustomFragment;
import com.example.singhealthapp.HelperClasses.CustomViewSettings;
import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.HelperClasses.HandlePhotoInterface;
import com.example.singhealthapp.Models.ChecklistItem;
import com.example.singhealthapp.R;

import java.util.ArrayList;

public class SafetyChecklistFragment extends CustomFragment {
    private static final String TAG = "SafetyChecklistFragment";

    RecyclerView safetyChecklistRecyclerViewPart1;
    RecyclerView safetyChecklistRecyclerViewPart2;

    private ArrayList<ChecklistItem> checklist_items_array_part1;
    private ArrayList<ChecklistItem> checklist_items_array_part2;

    private int tenantID;
    private String tenantCompany, tenantLocation, tenantInstitution, tenantType;

    Button start_audit_button;
    EditText overall_notes_editText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //TODO: implement shared pref (if have time)
        getActivity().setTitle("COVID safe measures checklist");
        View view = inflater.inflate(R.layout.f_checklist_safety, container, false);

        Bundle bundle = getArguments();
        tenantType = bundle.getString("TENANT_TYPE_KEY");
        tenantID = bundle.getInt("ID_KEY");
        tenantCompany = bundle.getString("COMPANY_KEY");
        tenantLocation = bundle.getString("LOCATION_KEY");
        tenantInstitution = bundle.getString("INSTITUTION_KEY");

        checklist_items_array_part1 = new ArrayList<>();
        checklist_items_array_part2 = new ArrayList<>();

        initSafetyChecklistPart1();
        initSafetyChecklistPart2();

        safetyChecklistRecyclerViewPart1 = view.findViewById(R.id.safety_checklist_recyclerview_part1);
        safetyChecklistRecyclerViewPart2 = view.findViewById(R.id.safety_checklist_recyclerview_part2);
        start_audit_button = view.findViewById(R.id.start_audit_button);
        overall_notes_editText = view.findViewById(R.id.overallReportNotesSafety);

        init_recyclerView(safetyChecklistRecyclerViewPart1, checklist_items_array_part1);
        init_recyclerView(safetyChecklistRecyclerViewPart2, checklist_items_array_part2);

        CustomViewSettings.makeScrollable(overall_notes_editText);

        start_audit_button.setOnClickListener(v -> {
            Bundle bundle1 = new Bundle();
            // if selected tenant type is F&B, then put F&B, if Non F&B, put Non F&B
            bundle1.putString("TENANT_TYPE_KEY", tenantType);
            bundle1.putInt("ID_KEY", tenantID);
            bundle1.putString("COMPANY_KEY", tenantCompany);
            bundle1.putString("LOCATION_KEY", tenantLocation);
            bundle1.putString("INSTITUTION_KEY", tenantInstitution);
            Log.d(TAG, "tenantType sending: "+tenantType);
            AuditChecklistFragment auditChecklistFragment = new AuditChecklistFragment();
            auditChecklistFragment.setArguments(bundle1);

            EspressoCountingIdlingResource.increment(12); //12 = number of recyclerViews created + 1 report created
            String tag = auditChecklistFragment.getClass().getName();
            SafetyChecklistFragment.this.getParentFragmentManager().beginTransaction()
                    .replace(R.id.auditor_fragment_container, auditChecklistFragment, tag)
                    .addToBackStack(tag)
                    .commit();
        });

        EspressoCountingIdlingResource.decrement();
        return view;
    }

    private void init_recyclerView(RecyclerView recyclerView, ArrayList<ChecklistItem> list) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        ChecklistAdapter checklistAdapter = new ChecklistAdapter((HandlePhotoInterface) getActivity(), list, false);
        recyclerView.setAdapter(checklistAdapter);
    }

    private void initSafetyChecklistPart1() {
        checklist_items_array_part1.add(new ChecklistItem("SafeEntry has been implemented for dine-in customers.", ""));
        checklist_items_array_part1.add(new ChecklistItem("Temperature screening is conducted for customers of outlets that are located outside of institution’s temperature screening zone.", ""));
        checklist_items_array_part1.add(new ChecklistItem("Table and seating arrangement adheres to the one-metre spacing between tables or groups. Where tables/seats are fixed, tables/seats should be marked out, ensuring at least one-metre spacing.", ""));
        checklist_items_array_part1.add(new ChecklistItem("Queue is demarcated to ensure at least one-metre spacing between customers such as entrances and cashier counters (e.g. through floor markers).", ""));
        checklist_items_array_part1.add(new ChecklistItem("Staff to ensure customers maintain safe distance of one-metre when queuing and seated.", ""));
        checklist_items_array_part1.add(new ChecklistItem("Staff to ensure customers wear a mask at all times, unless eating or drinking.", ""));
        checklist_items_array_part1.add(new ChecklistItem("Hand sanitizers are placed at high touch areas (i.e. tray return, collection point, outlet entrance/exit).", ""));
        checklist_items_array_part1.add(new ChecklistItem("Outlet promotes use of cashless payment modes.", ""));
    }

    private void initSafetyChecklistPart2() {
        checklist_items_array_part2.add(new ChecklistItem("All staff to wear a mask at all times, unless eating or drinking.", ""));
        checklist_items_array_part2.add(new ChecklistItem("Mask worn by staff is in the correct manner (i.e. cover nose and mouth, no hanging of mask under the chin/neck).", ""));
        checklist_items_array_part2.add(new ChecklistItem("All staff to record their temperature daily.", ""));
        checklist_items_array_part2.add(new ChecklistItem("Staff to maintain safe distance of one-metre (where possible) and not congregate, including at common areas, and during break/meal times.", ""));
        checklist_items_array_part2.add(new ChecklistItem("Check with supervisor that all staff record SafeEntry check-in and check-out (Note: Supervisor is accountable for adherence)", ""));
    }

}
