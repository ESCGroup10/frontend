package com.example.singhealthapp.auditor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

public class SafetyChecklistFragment extends Fragment {

    RecyclerView safetyChecklistRecyclerViewPart1;
    RecyclerView safetyChecklistRecyclerViewPart2;

    private ArrayList<Checklist_item> checklist_items_array_part1;
    private ArrayList<Checklist_item> checklist_items_array_part2;
    private ChecklistAdapter checklistAdapter;

    private static final String TENANT_TYPE_KEY = "tenant_type_key";

    Button start_audit_button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("COVID safe measures checklist");
        View view = inflater.inflate(R.layout.fragment_safety_checklist, container, false);

        checklist_items_array_part1 = new ArrayList<>();
        checklist_items_array_part2 = new ArrayList<>();

        initSafetyChecklistPart1();
        initSafetyChecklistPart2();

        safetyChecklistRecyclerViewPart1 = view.findViewById(R.id.safety_checklist_recyclerview_part1);
        safetyChecklistRecyclerViewPart2 = view.findViewById(R.id.safety_checklist_recyclerview_part2);
        start_audit_button = view.findViewById(R.id.start_audit_button);

        init_recyclerView(safetyChecklistRecyclerViewPart1, checklist_items_array_part1);
        init_recyclerView(safetyChecklistRecyclerViewPart2, checklist_items_array_part2);

        start_audit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                // if selected tenant type is fb, then put fb, if nfb, put nfb
                bundle.putString(TENANT_TYPE_KEY, "fb");
                AuditChecklistFragment auditChecklistFragment = new AuditChecklistFragment();
                auditChecklistFragment.setArguments(bundle);
                SafetyChecklistFragment.this.getParentFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, auditChecklistFragment).commit();
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

    private void initSafetyChecklistPart1() {
        checklist_items_array_part1.add(new Checklist_item("SafeEntry has been implemented for dine-in customers.", ""));
        checklist_items_array_part1.add(new Checklist_item("Temperature screening is conducted for customers of outlets that are located outside of institution’s temperature screening zone.", ""));
        checklist_items_array_part1.add(new Checklist_item("Table and seating arrangement adheres to the one-metre spacing between tables or groups. Where tables/seats are fixed, tables/seats should be marked out, ensuring at least one-metre spacing.", ""));
        checklist_items_array_part1.add(new Checklist_item("Queue is demarcated to ensure at least one-metre spacing between customers such as entrances and cashier counters (e.g. through floor markers).", ""));
        checklist_items_array_part1.add(new Checklist_item("Staff to ensure customers maintain safe distance of one-metre when queuing and seated.", ""));
        checklist_items_array_part1.add(new Checklist_item("Staff to ensure customers wear a mask at all times, unless eating or drinking.", ""));
        checklist_items_array_part1.add(new Checklist_item("Hand sanitizers are placed at high touch areas (i.e. tray return, collection point, outlet entrance/exit).", ""));
        checklist_items_array_part1.add(new Checklist_item("Outlet promotes use of cashless payment modes.", ""));
    }

    private void initSafetyChecklistPart2() {
        checklist_items_array_part2.add(new Checklist_item("All staff to wear a mask at all times, unless eating or drinking.", ""));
        checklist_items_array_part2.add(new Checklist_item("Mask worn by staff is in the correct manner (i.e. cover nose and mouth, no hanging of mask under the chin/neck).", ""));
        checklist_items_array_part2.add(new Checklist_item("All staff to record their temperature daily.", ""));
        checklist_items_array_part2.add(new Checklist_item("Staff to maintain safe distance of one-metre (where possible) and not congregate, including at common areas, and during break/meal times.", ""));
        checklist_items_array_part2.add(new Checklist_item("Check with supervisor that all staff record SafeEntry check-in and check-out (Note: Supervisor is accountable for adherence)", ""));
    }

    private void addToChecklist(ArrayList<Checklist_item> list, String statement) {
        list.add(new Checklist_item(statement, ""));
        checklistAdapter.notifyDataSetChanged();
    }
}
