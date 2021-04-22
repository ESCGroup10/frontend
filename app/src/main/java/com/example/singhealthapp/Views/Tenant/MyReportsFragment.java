package com.example.singhealthapp.Views.Tenant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.singhealthapp.HelperClasses.CentralisedToast;
import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.HelperClasses.CustomFragment;
import com.example.singhealthapp.HelperClasses.TenantReportPreviewNavigateListener;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Report;
import com.example.singhealthapp.Models.ReportPreview;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Auditor.ReportSummary.ReportSummaryFragment;
import com.example.singhealthapp.Views.ReportsPreview.ReportPreviewTenantAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyReportsFragment extends CustomFragment implements TenantReportPreviewNavigateListener {
    ReportPreviewTenantAdapter adapterUnresolved, adapterCompleted;
    private ArrayList<ReportPreview> reportPreviews, displayPreviews;
    private ArrayList<Report> reports, displayReports;
    private int userID;
    boolean unresolved, completed;
    String token, userType;
    private static DatabaseApiCaller apiCaller;

    private static final String TAG = "MyReportsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("My Reports");
        View view = inflater.inflate(R.layout.f_reports_all, container, false);
        loadFromSharedPreferences();
        initApiCaller();
        view.findViewById(R.id.reportPreviewSearchButton).setOnClickListener(v -> {
            if ( reportPreviews.isEmpty() ) return;
            TextView textView = view.findViewById(R.id.reportPreviewSearch);
            textView.setMaxLines(1);
            String text = textView.getText().toString();
            if ( text.isEmpty() ) {
                displayPreviews = new ArrayList<>(reportPreviews);
                displayReports = new ArrayList<>(reports);
            }
            else {
                String[] textArray = text.split(" ");
                displayReports.clear();
                displayPreviews.clear();
                for ( ReportPreview o : reportPreviews ){
                    for (String s : textArray) {
                        if (String.valueOf(o.getId()).contains(s) || String.valueOf(o.getTenant_id()).contains(s) || o.getReportDate().contains(s)) {
                            if (s.equals(textArray[textArray.length-1])) {
                                displayPreviews.add(o);
                                for (Report r : reports){
                                    if (r.getId() == o.getId()) {
                                        displayReports.add(r);
                                        break;
                                    }
                                }
                            }
                        }
                        else break;
                    }
                }
            }
            displayRecycleView(displayPreviews, displayReports);
        });

        view.findViewById(R.id.reportPreviewUnresolvedButton).setOnClickListener(v -> {
            if ( reportPreviews.isEmpty() ) return;
            unresolved = !unresolved;
            displayRecycleView(displayPreviews, displayReports);
        });

        view.findViewById(R.id.reportPreviewCompletedButton).setOnClickListener(v -> {
            if ( reportPreviews.isEmpty() ) return;
            completed = !completed;
            displayRecycleView(displayPreviews, displayReports);
        });
        return view;
    }

    private synchronized void initApiCaller() {
        apiCaller = new Retrofit.Builder()
                .baseUrl("https://esc10-303807.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);
        notifyAll();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: called");
        reportPreviews = new ArrayList<>();
        reports = new ArrayList<>();
        displayPreviews = new ArrayList<>();
        displayReports = new ArrayList<>();
        Button button1 = view.findViewById(R.id.reportPreviewUnresolvedButton);
        Button button2 = view.findViewById(R.id.reportPreviewCompletedButton);
        button1.setBackgroundColor(Color.rgb(115, 194, 239));
        button2.setBackgroundColor(Color.rgb(115, 194, 239));
        queuePreviews();
    }

    private synchronized void queuePreviews(){
        Log.d(TAG, "queuePreviews: called");
        while (token == null || apiCaller == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "queuePreviews: getting ReportPreview with token: "+token);
        Call<List<ReportPreview>> call = apiCaller.getReportPreview("Token " + token);
        call.enqueue(new Callback<List<ReportPreview>>() {
            @Override
            public void onResponse(Call<List<ReportPreview>> call, Response<List<ReportPreview>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Unsuccessful: response code " + response.code(), Toast.LENGTH_LONG).show();
                    return ;
                }
                if (response.body().isEmpty() || response.body() == null) return;
                for ( ReportPreview r : response.body()){
                    if (r.getTenant_id() == userID) {
                        reportPreviews.add(r);
                        displayPreviews.add(r);
                    }
                }
                queueReport(reportPreviews);
            }
            @Override
            public void onFailure(Call<List<ReportPreview>> call, Throwable t) {
                Log.d(TAG, "onFailure: queuePreviews");
                t.printStackTrace();
                CentralisedToast.makeText(getActivity(), "Unable to make request to server, server might be down", CentralisedToast.LENGTH_LONG);
            }
        });
    }

    private synchronized void queueReport(List<ReportPreview> reportPreviews){
        Log.d(TAG, "queueReport: called");
        while (token == null || apiCaller == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "queueReport: getting Report with token: "+token);
        Call<List<Report>> call = apiCaller.getReport("Token " + token);
        call.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Unsuccessful: response code " + response.code(), Toast.LENGTH_LONG).show();
                    return ;
                }
                for ( Report r : response.body()){
                    if (r.getTenant_id() == userID) {
                        reports.add(r);
                        displayReports.add(r);
                    }
                }
                unresolved = true;
                completed = true;
                displayRecycleView(reportPreviews, reports);
//                TextView textView = (TextView) getView().findViewById(R.id.reportPreviewSearch);
//                if(!textView.getText().toString().isEmpty()) getView().findViewById(R.id.reportPreviewSearchButton).performClick();
                Log.d(TAG, "queueReport onResponse: success");
            }
            @Override
            public void onFailure(Call<List<Report>> call, Throwable t) {
                Log.d(TAG, "onFailure: queueReport");
                t.printStackTrace();
                CentralisedToast.makeText(getActivity(), "Unable to make request to server, server might be down", CentralisedToast.LENGTH_LONG);
            }
        });
    }

    protected void displayRecycleView(List<ReportPreview> reportPreviews, List<Report> reports){
        Log.d(TAG, "displayRecycleView: called");
        ArrayList<ReportPreview> unresolvedPreview = new ArrayList<>();
        ArrayList<Report> unresolvedReports = new ArrayList<>();
        ArrayList<ReportPreview> completedPreview = new ArrayList<>();
        ArrayList<Report> completedReports = new ArrayList<>();
        ArrayList<Integer> Invalid = new ArrayList<>();
//        Log.d(TAG, "displayRecycleView: size of reports: "+reports.size());
//        Log.d(TAG, "displayRecycleView: size of reportPreviews: "+reportPreviews.size());
//        reports = DateOperations.organiseReportByDate(reports);
//        reportPreviews = DateOperations.organiseReportPreviewByDate(reportPreviews);
//        Log.d(TAG, "displayRecycleView: size of reports: "+reports.size());
//        Log.d(TAG, "displayRecycleView: size of reportPreviews: "+reportPreviews.size());
        for ( Report o : reports ){
            if (! isDataValid(o)) {
                Invalid.add(o.getId());
                continue;
            }
            if ( ! o.isStatus() ) {
                unresolvedReports.add(o);
            }
            else completedReports.add(o);
        }
        for ( ReportPreview o : reportPreviews ){
            if (Invalid.contains(o.getId())) continue;
            if ( ! o.isStatus() ) {
                unresolvedPreview.add(o);
            }
            else completedPreview.add(o);
        }
        if ( ! unresolved ) {
            unresolvedPreview.clear();
            unresolvedReports.clear();
        }
        if ( ! completed ) {
            completedPreview.clear();
            completedReports.clear();
        }
        Log.d(TAG, "displayRecycleView: unresolved: "+unresolved);
        Log.d(TAG, "displayRecycleView: completed: "+completed);
        Log.d(TAG, "displayRecycleView: unresolvedPreview size: "+unresolvedPreview.size());
        Log.d(TAG, "displayRecycleView: unresolvedReports size: "+unresolvedReports.size());
        Log.d(TAG, "displayRecycleView: completedPreview size: "+completedPreview.size());
        Log.d(TAG, "displayRecycleView: completedReports size: "+completedReports.size());

        adapterUnresolved = new ReportPreviewTenantAdapter(unresolvedPreview, unresolvedReports, this, token);
        try {
            RecyclerView view = getView().findViewById(R.id.reportPreviewRecyclerViewUnresolved);
            view.setLayoutManager(new LinearLayoutManager(getActivity()));
            view.setItemAnimator(new DefaultItemAnimator());
            view.setAdapter(adapterUnresolved);
        } catch (Exception e) {
            System.out.println("Unresolved recycleView not set");
        }
        adapterCompleted = new ReportPreviewTenantAdapter(completedPreview, completedReports, this, token);
        try {
            RecyclerView view = getView().findViewById(R.id.reportPreviewRecyclerView);
            view.setLayoutManager(new LinearLayoutManager(getActivity()));
            view.setItemAnimator(new DefaultItemAnimator());
            view.setAdapter(adapterCompleted);
            EspressoCountingIdlingResource.decrement();
        } catch (Exception e) {
            System.out.println("Completed recycleView not set");
        }
    }

    @Override
    public void navigateFromRecyclerView(Report report, String token) {
        Log.d(TAG, "navigateFromRecyclerView: called");
        ReportSummaryFragment reportSummaryFragment = new ReportSummaryFragment(report, token);
        String tag = reportSummaryFragment.getClass().getName();
        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, reportSummaryFragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    boolean isDataValid(Report r){
        if (r.getHousekeeping_score() > 100 || r.getHousekeeping_score() < 0) return false;
        if (r.getStaffhygiene_score() > 100 || r.getStaffhygiene_score() < 0) return false;
        if (r.getSafety_score() > 100 || r.getSafety_score() < 0) return false;
        if (userType.equals("F&B")) {
            if (r.getFoodhygiene_score() > 100 || r.getFoodhygiene_score() < 0) return false;
            if (r.getHealthierchoice_score() > 100 || r.getHealthierchoice_score() < 0) return false;
        }
        return true;
    }

    private synchronized void loadFromSharedPreferences() {
        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
            String USER_ID_KEY = "USER_ID_KEY";
            userID = sharedPreferences.getInt(USER_ID_KEY, -1);
            userType = sharedPreferences.getString("USER_TYPE_KEY", null);
            token = sharedPreferences.getString("TOKEN_KEY", null);
        }
        catch (Exception ignored){
        }
        Log.d(TAG, "loadToken: token loaded");
        notifyAll();
    }
}