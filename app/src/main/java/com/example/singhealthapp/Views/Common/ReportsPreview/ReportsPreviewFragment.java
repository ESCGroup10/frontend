package com.example.singhealthapp.Views.Common.ReportsPreview;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.HelperClasses.AuditorReportPreviewNavigateListener;
import com.example.singhealthapp.HelperClasses.CustomFragment;
import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Report;
import com.example.singhealthapp.Models.ReportPreview;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Common.ReportSummary.ReportSummaryFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReportsPreviewFragment extends CustomFragment implements AuditorReportPreviewNavigateListener {
    private static final String TAG = "ReportsPreviewFragment";
    ReportPreviewAuditorAdapter adapterUnresolved, adapterCompleted;
    private ArrayList<ReportPreview> reportPreviews, displayPreviews;
    private ArrayList<Report> reports, displayReports;
    boolean unresolved, completed;
    String token, userType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loadFromSharedPreferences();
        getActivity().setTitle("Reports");
        View view = inflater.inflate(R.layout.f_reports_all, container, false);
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
            if ( reportPreviews.isEmpty() ) {
                return;
            } else {
                Log.d(TAG, "onCreateView: reportPreviews len: "+reportPreviews.size());
            }
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

    @Override
    public synchronized void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        EspressoCountingIdlingResource.decrement();
        reportPreviews = new ArrayList<>();
        reports = new ArrayList<>();
        displayPreviews = new ArrayList<>();
        displayReports = new ArrayList<>();
        Button button1 = view.findViewById(R.id.reportPreviewUnresolvedButton);
        Button button2 = view.findViewById(R.id.reportPreviewCompletedButton);
        button1.setBackgroundColor(Color.rgb(115, 194, 239));
        button2.setBackgroundColor(Color.rgb(115, 194, 239));
        while (token == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        queuePreviews(token);
    }

    private void queuePreviews(String token){
//        Log.d(TAG, "queuePreviews: called");
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://esc10-303807.et.r.appspot.com/").addConverterFactory(GsonConverterFactory.create()).build();
        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);
//        Log.d(TAG, "queuePreviews: getting report preview with token: "+token);
        Call<List<ReportPreview>> call = apiCaller.getReportPreview("Token " + token);
        call.enqueue(new Callback<List<ReportPreview>>() {
            @Override
            public void onResponse(Call<List<ReportPreview>> call, Response<List<ReportPreview>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Unsuccessful: response code " + response.code(), Toast.LENGTH_LONG).show();
                    return ;
                }
                Log.d(TAG, "queuePreviews onResponse: "+response.code());
//                Log.d(TAG, "queuePreviews response body: "+response.body().toString());
                reportPreviews.addAll(response.body());
                displayPreviews.addAll(response.body());
//                Log.d(TAG, "queueReport len of reportPreviews: "+reportPreviews.size());
//                Log.d(TAG, "queueReport len of displayPreviews: "+displayPreviews.size());
                queueReport(token, response.body());
            }
            @Override
            public void onFailure(Call<List<ReportPreview>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void queueReport(String token, List<ReportPreview> reportPreviews){
//        Log.d(TAG, "queueReport: called");
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://esc10-303807.et.r.appspot.com").addConverterFactory(GsonConverterFactory.create()).build();
        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);
//        Log.d(TAG, "queuePreviews: getting report with token: "+token);
        Call<List<Report>> call = apiCaller.getReport("Token " + token);
        call.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Unsuccessful: response code " + response.code(), Toast.LENGTH_LONG).show();
                    return ;
                }
//                Log.d(TAG, "queueReport onResponse: tenant id gotten from queueReport: "+response.body().get(0).getTenant_id());
//                System.out.println(response.body().get(0).getTenant_id());
//                Log.d(TAG, "queueReport response body: "+response.body().toString());
                reports.addAll(response.body());
                displayReports.addAll(response.body());
//                Log.d(TAG, "queueReport len of reports: "+reports.size());
//                Log.d(TAG, "queueReport len of displayReports: "+displayReports.size());
                unresolved = true;
                completed = true;
                displayRecycleView(displayPreviews, response.body());
//                TextView textView = getView().findViewById(R.id.reportPreviewSearch);
//                if(!textView.getText().toString().isEmpty()) getView().findViewById(R.id.reportPreviewSearchButton).performClick();
            }
            @Override
            public void onFailure(Call<List<Report>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
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
        for ( Report o : reports ){
            if (! isDataValid(o)) {
//                Log.d(TAG, "displayRecycleView: adding to invalid");
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
//                Log.d(TAG, "displayRecycleView: adding to unresolvedPreview");
                unresolvedPreview.add(o);
            }
            else {
//                Log.d(TAG, "completedPreview: adding to unresolvedPreview");
                completedPreview.add(o);
            }
        }
        if ( ! unresolved ) {
            unresolvedPreview.clear();
            unresolvedReports.clear();
        }
        if ( ! completed ) {
            completedPreview.clear();
            completedReports.clear();
        }
//        Log.d(TAG, "displayRecycleView: unresolved: "+unresolved);
//        Log.d(TAG, "displayRecycleView: completed: "+completed);
//        Log.d(TAG, "displayRecycleView: unresolvedPreview size: "+unresolvedPreview.size());
//        Log.d(TAG, "displayRecycleView: unresolvedReports size: "+unresolvedReports.size());
//        Log.d(TAG, "displayRecycleView: completedPreview size: "+completedPreview.size());
//        Log.d(TAG, "displayRecycleView: completedReports size: "+completedReports.size());
        adapterUnresolved = new ReportPreviewAuditorAdapter(unresolvedPreview, unresolvedReports, this, token);
        try {
            RecyclerView view = (RecyclerView) getView().findViewById(R.id.reportPreviewRecyclerViewUnresolved);
            view.setLayoutManager(new LinearLayoutManager(getActivity()));
            view.setItemAnimator(new DefaultItemAnimator());
            view.setAdapter(adapterUnresolved);
        } catch (Exception e) {
            System.out.println("Unresolved recycleView not set");
        }
        adapterCompleted = new ReportPreviewAuditorAdapter(completedPreview, completedReports, this, token);
        try {
            RecyclerView view = (RecyclerView) getView().findViewById(R.id.reportPreviewRecyclerView);
            view.setLayoutManager(new LinearLayoutManager(getActivity()));
            view.setItemAnimator(new DefaultItemAnimator());
            view.setAdapter(adapterCompleted);
        } catch (Exception e) {
            System.out.println("Completed recycleView not set");
        }
    }

    private synchronized void loadFromSharedPreferences() {
        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
            String USER_ID_KEY = "USER_ID_KEY";
            userType = sharedPreferences.getString("USER_TYPE_KEY", null);
            token = sharedPreferences.getString("TOKEN_KEY", null);
        }
        catch (Exception ignored){
        }
        Log.d(TAG, "loadToken: token loaded");
        notifyAll();
    }

    @Override
    public void navigateFromRecyclerView(Report report, String token) {
        ReportSummaryFragment reportSummaryFragment = new ReportSummaryFragment(report, token);
        String tag = reportSummaryFragment.getClass().getName();
        getParentFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, reportSummaryFragment, tag)
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
}