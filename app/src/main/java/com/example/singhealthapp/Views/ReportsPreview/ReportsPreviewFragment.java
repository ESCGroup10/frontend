package com.example.singhealthapp.Views.ReportsPreview;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.HelperClasses.AuditorReportPreviewNavigateListener;
import com.example.singhealthapp.HelperClasses.CustomFragment;
import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.HelperClasses.Ping;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Report;
import com.example.singhealthapp.Models.ReportPreview;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Auditor.ReportSummary.ReportSummaryFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReportsPreviewFragment extends CustomFragment implements AuditorReportPreviewNavigateListener {
    ReportPreviewAdapter adapterUnresolved, adapterCompleted;
    private ArrayList<ReportPreview> reportPreviews, displayPreviews;
    private ArrayList<Report> reports, displayReports;
    boolean unresolved, completed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        reportPreviews = new ArrayList<>();
        reports = new ArrayList<>();
        displayPreviews = new ArrayList<>();
        displayReports = new ArrayList<>();
        Button button1 = view.findViewById(R.id.reportPreviewUnresolvedButton);
        Button button2 = view.findViewById(R.id.reportPreviewCompletedButton);
        button1.setBackgroundColor(Color.rgb(115, 194, 239));
        button2.setBackgroundColor(Color.rgb(115, 194, 239));
        queuePreviews(loadToken());
    }

    private void queuePreviews(String token){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://esc10-303807.et.r.appspot.com/").addConverterFactory(GsonConverterFactory.create()).build();
        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);
        Call<List<ReportPreview>> call = apiCaller.getReportPreview("Token " + token);
        call.enqueue(new Callback<List<ReportPreview>>() {
            @Override
            public void onResponse(Call<List<ReportPreview>> call, Response<List<ReportPreview>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Unsuccessful: response code " + response.code(), Toast.LENGTH_LONG).show();
                    return ;
                }
                System.out.println("response " + response.code());
                reportPreviews.addAll(response.body());
                displayPreviews.addAll(response.body());
                queueReport(token, response.body());
            }
            @Override
            public void onFailure(Call<List<ReportPreview>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void queueReport(String token, List<ReportPreview> reportPreviews){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://esc10-303807.et.r.appspot.com").addConverterFactory(GsonConverterFactory.create()).build();
        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);
        Call<List<Report>> call = apiCaller.getReport("Token " + token);
        call.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Unsuccessful: response code " + response.code(), Toast.LENGTH_LONG).show();
                    return ;
                }
                System.out.println(response.body().get(0).getTenant_id());
                reports.addAll(response.body());
                displayReports.addAll(response.body());
                unresolved = true;
                completed = true;
                displayRecycleView(displayPreviews, response.body());
                TextView textView = (TextView) getView().findViewById(R.id.reportPreviewSearch);
                if(!textView.getText().toString().isEmpty()) getView().findViewById(R.id.reportPreviewSearchButton).performClick();
            }
            @Override
            public void onFailure(Call<List<Report>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void displayRecycleView(List<ReportPreview> reportPreviews, List<Report> reports){
        ArrayList<ReportPreview> unresolvedPreview = new ArrayList<>();
        ArrayList<Report> unresolvedReports = new ArrayList<>();
        ArrayList<ReportPreview> completedPreview = new ArrayList<>();
        ArrayList<Report> completedReports = new ArrayList<>();
        ArrayList<Integer> Invalid = new ArrayList<>();
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
        adapterUnresolved = new ReportPreviewAdapter(unresolvedPreview, unresolvedReports, (AuditorReportPreviewNavigateListener)this, loadToken());
        try {
            RecyclerView view = (RecyclerView) getView().findViewById(R.id.reportPreviewRecyclerViewUnresolved);
            view.setLayoutManager(new LinearLayoutManager(getActivity()));
            view.setItemAnimator(new DefaultItemAnimator());
            view.setAdapter(adapterUnresolved);
        } catch (Exception e) {
            System.out.println("Unresolved recycleView not set");
        }
        adapterCompleted = new ReportPreviewAdapter(completedPreview, completedReports, (AuditorReportPreviewNavigateListener)this, loadToken());
        try {
            RecyclerView view = (RecyclerView) getView().findViewById(R.id.reportPreviewRecyclerView);
            view.setLayoutManager(new LinearLayoutManager(getActivity()));
            view.setItemAnimator(new DefaultItemAnimator());
            view.setAdapter(adapterCompleted);
            EspressoCountingIdlingResource.decrement();
        } catch (Exception e) {
            System.out.println("Completed recycleView not set");
        }
    }

    private String loadToken() {
        String token = null;
        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
            token = sharedPreferences.getString("TOKEN_KEY", null);
        }
        catch (Exception ignored){
        }
        return token;
    }

    @Override
    public void navigateFromRecyclerView(Report report, String token) {
        ReportSummaryFragment reportSummaryFragment = new ReportSummaryFragment(report, token);
        getParentFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, reportSummaryFragment,
                reportSummaryFragment.getClass().getName())
                .addToBackStack(null)
                .commit();
    }

    boolean isDataValid(Report r){
        if (r.getFoodhygiene_score() > 1 || r.getFoodhygiene_score() < 0) return false;
        if (r.getHealthierchoice_score() > 1 || r.getHealthierchoice_score() < 0) return false;
        if (r.getHousekeeping_score() > 1 || r.getHousekeeping_score() < 0) return false;
        if (r.getStaffhygiene_score() > 1 || r.getStaffhygiene_score() < 0) return false;
        if (r.getSafety_score() > 1 || r.getSafety_score() < 0) return false;
        return true;
    }
}