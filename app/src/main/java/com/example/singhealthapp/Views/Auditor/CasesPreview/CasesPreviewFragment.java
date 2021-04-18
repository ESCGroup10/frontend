package com.example.singhealthapp.Views.Auditor.CasesPreview;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.singhealthapp.HelperClasses.CasePreviewNavigateListener;
import com.example.singhealthapp.HelperClasses.CustomFragment;
import com.example.singhealthapp.HelperClasses.TextAestheticsAndParsing;
import com.example.singhealthapp.Models.Case;
import com.example.singhealthapp.Models.Report;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Tenant.CaseExpanded;

import java.util.List;

public class CasesPreviewFragment extends CustomFragment implements CasePreviewNavigateListener {
    CasePreviewAdapter unresolvedAdapter, resolvedAdapter;
    List<Case> unresolvedCases, resolvedCases;
    int id;
    TextView companyView, locationView;
    String company, location;
    View view;
    private Report report;
    private String token, userType;

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
        loadUserType();

        unresolvedAdapter = new CasePreviewAdapter(unresolvedCases, report, (CasePreviewNavigateListener)this);
        try {
            RecyclerView view1 = (RecyclerView) view.findViewById(R.id.casePreviewRecyclerViewUnresolved);
            view1.setLayoutManager(new LinearLayoutManager(getActivity()));
            view1.setItemAnimator(new DefaultItemAnimator());
            view1.setAdapter(unresolvedAdapter);
        } catch (Exception e) {
            System.out.println("Unresolved recycleView not set");
        }

        resolvedAdapter = new CasePreviewAdapter(resolvedCases, report, (CasePreviewNavigateListener)this);
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

    @Override
    public synchronized void navigateFromRecyclerView(Report report, Case thisCase) {
        while (userType == null) {
            try {
                wait(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Bundle args = new Bundle();
        args.putString("COMPANY_KEY", report.getCompany());
        if (report.getTenant_display_id() != null) args.putInt("REPORT_NUMBER_KEY", report.getTenant_display_id());
        else args.putInt("REPORT_NUMBER_KEY", report.getId());
        args.putInt("CASE_NUMBER_KEY", thisCase.getId());
        args.putBoolean("RESOLVED_STATUS_KEY", thisCase.isIs_resolved());
        args.putInt("REPORT_ID_KEY", report.getId());
        args.putInt("CASE_ID_KEY", thisCase.getId());
        args.putBoolean("PENDING_KEY", ((!thisCase.isIs_resolved()) && (!(thisCase.getResolved_photo().equals("")))));
        CaseExpanded caseExpanded = new CaseExpanded();
        caseExpanded.setArguments(args);

        System.out.println("userType: "+userType);
        System.out.println((userType.equals("Auditor")?R.id.auditor_fragment_container:R.id.fragment_container));
        getParentFragmentManager().beginTransaction()
                .replace((userType.equals("Auditor")?R.id.auditor_fragment_container:R.id.fragment_container), caseExpanded,
                        caseExpanded.getClass().getName())
                .addToBackStack(null).commit();
    }

    private synchronized void loadUserType() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        userType = sharedPreferences.getString("USER_TYPE_KEY", null);
        notifyAll();
    }
}