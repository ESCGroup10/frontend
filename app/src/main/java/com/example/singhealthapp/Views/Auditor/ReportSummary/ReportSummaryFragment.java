package com.example.singhealthapp.Views.Auditor.ReportSummary;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.singhealthapp.HelperClasses.BackStackInfo;
import com.example.singhealthapp.HelperClasses.CustomFragment;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Case;
import com.example.singhealthapp.Models.Report;
import com.example.singhealthapp.Models.User;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Auditor.CasesPreview.CasesPreviewFragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReportSummaryFragment extends CustomFragment {
    private static final String TAG = "ReportSummaryFragment";
    Report report;
    View view;
    TextView company, location, resolved, unresolved, rejected;
    HorizontalBarChart chart1, chart2, chart3, chart4, chart5, chart6;
    ArrayList<BarEntry> barEntries;
    BarData barData;
    BarDataSet barDataSet;
    float[][] data;
    int[] colors;
    private final String token;
    private String userType, reportUserType;
    private Object userTypeLock = new Object();
    List<Case> resolvedCases, unresolvedCases, rejectedCases;

    public ReportSummaryFragment(Report report, String token) {
        this.report = report;
        this.token = token;
        data = new float[][]{new float[]{report.getStaffhygiene_score()*100f, 100f - report.getStaffhygiene_score()*100f},
                new float[]{report.getHousekeeping_score()*100f, 100f - report.getHousekeeping_score()*100f},
                new float[]{report.getSafety_score()*100f,  100f - report.getSafety_score()*100f},
                new float[]{report.getHealthierchoice_score()*100f,  100f - report.getHealthierchoice_score()*100f},
                new float[]{report.getFoodhygiene_score()*100f,  100f - report.getFoodhygiene_score()*100f}};
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadUserType();
        BackStackInfo.printBackStackInfo(getParentFragmentManager(), this);
        if (report.getTenant_display_id() == null){
            getActivity().setTitle("Report " + report.getId());
        }
        else getActivity().setTitle("Report " + report.getTenant_display_id());
        System.out.println(report.getTenant_display_id());
        view = inflater.inflate(R.layout.f_report_summary, container, false);
        company = view.findViewById(R.id.reportCompany);
        company.setText(("" + report.getCompany()));
        location = view.findViewById(R.id.reportLocation);
        location.setText(("" + report.getLocation()));

        chart1 = view.findViewById(R.id.reportBarChart1);
        barChartOperation(chart1, 0);
        chart2 = view.findViewById(R.id.reportBarChart2);
        barChartOperation(chart2, 1);
        chart3 = view.findViewById(R.id.reportBarChart3);
        chart4 = view.findViewById(R.id.reportBarChart4);
        chart5 = view.findViewById(R.id.reportBarChart5);
        barChartOperation(chart5, 4);
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://esc10-303807.et.r.appspot.com/").addConverterFactory(GsonConverterFactory.create()).build();
        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);
        Call<List<User>> typeCall = apiCaller.getUsers("Token " + token);
        typeCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.body().isEmpty()) {
                    reportUserType = "Non F&B";
                    return;
                }
                for (User u : response.body()){
                    if (u.getId() == report.getTenant_id() || u.getId() == response.body().get(response.body().size()-1).getId()) {
                        reportUserType = u.getType();
                        if ( reportUserType.equals("Non F&B")) {
                            TextView textView1 = view.findViewById(R.id.chart1Text);
                            TextView textView2 = view.findViewById(R.id.chart2Text);
                            TextView textView5 = view.findViewById(R.id.chart5Text);
                            textView1.setText("Professionalism and Staff Hygiene (20%)");
                            textView2.setText("Housekeeping & General Cleanliness (40%)");
                            textView5.setText("Workplace Safety & Health (40%)");
                        }
                        else {
                            chart3.setVisibility(View.VISIBLE);
                            chart4.setVisibility(View.VISIBLE);
                            view.findViewById(R.id.chart3Text).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.chart4Text).setVisibility(View.VISIBLE);
                            barChartOperation(chart3, 2);
                            barChartOperation(chart4, 3);
                        }
                        chart6 = view.findViewById(R.id.reportBarChart6);
                        barEntries = new ArrayList<>();
                        float[] dataFloat = getTotalScore(report);
                        barEntries.add(new BarEntry(0, dataFloat));
                        System.out.println("score: " + Arrays.toString(dataFloat));
                        barDataSet = new BarDataSet(barEntries, "");
                        if (dataFloat[0] >= 95) colors = new int[]{Color.rgb(159, 221, 88), Color.rgb(170, 170, 170)};
                        else colors = new int[]{Color.rgb(221, 57, 48), Color.rgb(170, 170, 170)};
                        barDataSet.setColors(colors);
                        barDataSet.setValueTextSize(10);
                        barDataSet.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getBarStackedLabel(float value, BarEntry stackedEntry) {
                                if ( value == dataFloat[0] ) return String.valueOf((int) value);
                                return "";
                            }
                        });
                        barData = new BarData(barDataSet);
                        chart6.setData(barData);
                        reportSetBarData(chart6);
                        break;
                    }
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
            }
        });
        resolved = view.findViewById(R.id.auditorReportResolved);
        unresolved = view.findViewById(R.id.auditorReportUnresolved);
        rejected = view.findViewById(R.id.auditorReportNull);
        resolvedCases = new ArrayList<>();
        unresolvedCases = new ArrayList<>();
        rejectedCases = new ArrayList<>();
        Button button = view.findViewById(R.id.auditorReportViewCases);
        Call<List<Case>> call = apiCaller.getCasesById("Token " + token, report.getId(), 1);
        call.enqueue(new Callback<List<Case>>() {
            @Override
            public void onResponse(Call<List<Case>> call, Response<List<Case>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Unsuccessful: response code " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                if (response.body().isEmpty()) resolved.setText("0");
                else resolved.setText(String.valueOf(response.body().size()));
                Log.d(TAG, "onResponse: " + "size of response body: " + response.body().size());
                Log.d(TAG, "onResponse: " + "response body: " + response.body());
                resolvedCases.addAll(response.body());
                call = apiCaller.getCasesById("Token " + token, report.getId(), 0);
                synchronized (userTypeLock) {
                    while (userType == null) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    call.enqueue(new Callback<List<Case>>() {
                        @Override
                        public void onResponse(Call<List<Case>> call, Response<List<Case>> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(getContext(), "Unsuccessful: response code " + response.code(), Toast.LENGTH_LONG).show();
                                return;
                            }
                            Log.d(TAG, "onResponse: " + "size of response body: " + response.body().size());
                            Log.d(TAG, "onResponse: " + "response body: " + response.body());
                            if (response.body().isEmpty()) {
                                unresolved.setText("0");
                                rejected.setText("0");
                            }
                            else {
                                for (Case c : response.body()) {
                                    if (c.getRejected_comments().isEmpty()) unresolvedCases.add(c);
                                    else rejectedCases.add(c);
                                }
                                unresolved.setText(String.valueOf(unresolvedCases.size()));
                                rejected.setText(String.valueOf(rejectedCases.size()));
                            }
                            if (!resolved.getText().toString().equals("0") || !unresolved.getText().toString().equals("0") || !rejected.getText().toString().equals("0")) {
                                CasesPreviewFragment casesPreviewFragment = new CasesPreviewFragment(unresolvedCases, resolvedCases, rejectedCases,
                                        report.getId(), report.getCompany(), report.getLocation(), report, token);
                                button.setEnabled(true);
                                button.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                                        .replace((userType.equals("Auditor") ? R.id.auditor_fragment_container : R.id.fragment_container), casesPreviewFragment, casesPreviewFragment.getClass().getName())
                                        .addToBackStack(null).commit());
                            }
                        }
                        @Override
                        public void onFailure(Call<List<Case>> call, Throwable t) {
                            unresolved.setText("error");
                            rejected.setText("error");
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<List<Case>> call, Throwable t) {
                resolved.setText("error");
            }
        });
        return view;
    }

    protected void barChartOperation(HorizontalBarChart chart, int i){
        barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, data[i]));
        barDataSet = new BarDataSet(barEntries, "");
        if (data[i][0] >= 70)
            colors = new int[]{Color.rgb(159, 221, 88), Color.rgb(170, 170, 170)};
        else if (data[i][0] >= 35)
            colors = new int[]{Color.rgb(237, 135, 40), Color.rgb(170, 170, 170)};
        else colors = new int[]{Color.rgb(221, 57, 48), Color.rgb(170, 170, 170)};
        barDataSet.setColors(colors);
        barDataSet.setValueTextSize(8);
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarStackedLabel(float value, BarEntry stackedEntry) {
                if ( value == data[i][0] ) return String.valueOf((int) value);
                return "";
            }
        });
        barData = new BarData(barDataSet);
        chart.setData(barData);
        reportSetBarData(chart);
    }

    void reportSetBarData(BarChart chart){
        chart.animateXY(800, 1300);
        chart.setPinchZoom(false);
        chart.setClickable(false);
        chart.setTouchEnabled(true);
        chart.setDoubleTapToZoomEnabled(false);
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setDrawLabels(false);
        chart.getAxisRight().setDrawAxisLine(false);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.invalidate();
    }

    float[] getTotalScore(Report report){
        float total;
        if (reportUserType.equals("F&B"))
            total =  (float) (report.getStaffhygiene_score()*0.1f + report.getHousekeeping_score()*0.2f + report.getSafety_score()*0.2f
                    + report.getHealthierchoice_score()*0.15f + report.getFoodhygiene_score()*0.35f);
        else total =  (float) (report.getStaffhygiene_score()*0.2f + report.getHousekeeping_score()*0.4f + report.getSafety_score()*0.4f);
        return new float[]{total*100f, 100f-total*100f};
    }

    private synchronized void loadUserType() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        userType = sharedPreferences.getString("USER_TYPE_KEY", null);
        notifyAll();
    }

}