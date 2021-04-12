package com.example.singhealthapp.Views.Auditor.CasesPreview;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.singhealthapp.HelperClasses.TextAestheticsAndParsing;
import com.example.singhealthapp.Models.Case;
import com.example.singhealthapp.Models.Report;
import com.example.singhealthapp.R;

import java.util.List;

public class CasesPreviewFragment extends Fragment {
    CasePreviewAdapter unresolvedAdapter, resolvedAdapter;
    List<Case> unresolvedCases, resolvedCases;
    int id;
    TextView companyView, locationView;
    String company, location;
    View view;
    private Report report;
    private String token;

    public CasesPreviewFragment(List<Case> unresolvedCases, List<Case> resolvedCases, int id, String company, String location, Report report, String token){
        this.unresolvedCases = unresolvedCases;
        this.resolvedCases = resolvedCases;
        this.id = id;
        this.company = company;
        this.location = location;
        this.report = report;
        this.token = token;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (report.getTenant_display_id() != null) getActivity().setTitle("Report " + report.getTenant_display_id() + " Cases");
        else getActivity().setTitle("Report " + id + " Cases");
        view = inflater.inflate(R.layout.f_cases_all, container, false);
        System.out.println( unresolvedCases.size() );

        unresolvedAdapter = new CasePreviewAdapter(unresolvedCases, report, getActivity());
        try {
            RecyclerView view1 = (RecyclerView) view.findViewById(R.id.casePreviewRecyclerViewUnresolved);
            view1.setLayoutManager(new LinearLayoutManager(getActivity()));
            view1.setItemAnimator(new DefaultItemAnimator());
            view1.setAdapter(unresolvedAdapter);
        } catch (Exception e) {
            System.out.println("Unresolved recycleView not set");
        }

        resolvedAdapter = new CasePreviewAdapter(resolvedCases, report, getActivity());
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
        TextAestheticsAndParsing.setHalfBoldTextViews(companyView, "Company: ", company);
        TextAestheticsAndParsing.setHalfBoldTextViews(locationView, "Location: ", location);
        return view;
    }

    public String getToken() {
        return token;
    }
    public Report getReport() {
        return report;
    }
}