package com.example.singhealthapp.auditor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.singhealthapp.R;
import com.example.singhealthapp.auditor.Adapters.CasePreviewAdapter;

import java.util.ArrayList;
import java.util.List;

public class CaseFragment extends Fragment {
    CasePreviewAdapter unresolvedAdapter, resolvedAdapter;
    List<Case> unresolvedCases, resolvedCases;
    int id;
    TextView companyView, locationView;
    String company, location;
    View view;

    public CaseFragment(List<Case> unresolvedCases, List<Case> resolvedCases, int id, String company, String location){
        this.unresolvedCases = unresolvedCases;
        this.resolvedCases = resolvedCases;
        this.id = id;
        this.company = company;
        this.location = location;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Report " + id + " Cases");
        view = inflater.inflate(R.layout.fragment_case, container, false);
        System.out.println( unresolvedCases.size() );

        unresolvedAdapter = new CasePreviewAdapter(unresolvedCases);
        try {
            RecyclerView view1 = (RecyclerView) view.findViewById(R.id.casePreviewRecyclerViewUnresolved);
            view1.setLayoutManager(new LinearLayoutManager(getActivity()));
            view1.setItemAnimator(new DefaultItemAnimator());
            view1.setAdapter(unresolvedAdapter);
        } catch (Exception e) {
            System.out.println("Unresolved recycleView not set");
        }

        resolvedAdapter = new CasePreviewAdapter(resolvedCases);
        try {
            RecyclerView view2 = (RecyclerView) view.findViewById(R.id.casePreviewRecyclerViewResolved);
            view2.setLayoutManager(new LinearLayoutManager(getActivity()));
            view2.setItemAnimator(new DefaultItemAnimator());
            view2.setAdapter(resolvedAdapter);
        } catch (Exception e) {
            System.out.println("resolved recycleView not set");
        }

        companyView = view.findViewById(R.id.reportCaseCompany);
        locationView = view.findViewById(R.id.reportCaseLocation);
        companyView.setText("Company: " + company);
        locationView.setText("Location: " + location);
        return view;
    }
}