package com.example.singhealthapp.Views.Auditor.ReportSummary;

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

import com.example.singhealthapp.HelperClasses.CustomFragment;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Case;
import com.example.singhealthapp.Models.Report;
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
    private String userType, tenantType;
    private final Object lock = new Object();
    List<Case> resolvedCases, unresolvedCases, rejectedCases;
    private static DatabaseApiCaller apiCaller;

    public ReportSummaryFragment(Report report, String token) {
        this.report = report;
        this.token = token;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadUserType();
        initApiCaller();
        if (report.getTenant_display_id() == null){
            getActivity().setTitle("Report " + report.getId());
        }
        else getActivity().setTitle("Report " + report.getTenant_display_id());
        Log.d(TAG, "onCreateView: Tenant_display_id: "+report.getTenant_display_id());

        view = inflater.inflate(R.layout.f_report_summary, container, false);
        company = view.findViewById(R.id.reportCompany);
        company.setText(("" + report.getCompany()));
        location = view.findViewById(R.id.reportLocation);
        location.setText(("" + report.getLocation()));

        resolved = view.findViewById(R.id.auditorReportResolved);
        unresolved = view.findViewById(R.id.auditorReportUnresolved);
        rejected = view.findViewById(R.id.auditorReportNull);
        resolvedCases = new ArrayList<>();
        unresolvedCases = new ArrayList<>();
        rejectedCases = new ArrayList<>();
        Button button = view.findViewById(R.id.auditorReportViewCases);
        Log.d(TAG, "onCreateView: before lock");
        synchronized (lock) {
            while (userType == null || apiCaller == null) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "onCreateView: got past lock");
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
                                tenantType = report.getOutlet_type();
                                initData();
                                setBar6();
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
                                        .addToBackStack(casesPreviewFragment.getClass().getName())
                                        .commit());
                            }
                        }
                        @Override
                        public void onFailure(Call<List<Case>> call, Throwable t) {
                            unresolved.setText("error");
                            rejected.setText("error");
                        }
                    });
                }
            @Override
            public void onFailure(Call<List<Case>> call, Throwable t) {
                resolved.setText("error");
            }
        });
    }
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
        synchronized (lock) {
            while (tenantType == null) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (tenantType.equals("F&B"))
                total = (float) (report.getStaffhygiene_score() * 0.1f + report.getHousekeeping_score() * 0.2f + report.getSafety_score() * 0.2f
                        + report.getHealthierchoice_score() * 0.15f + report.getFoodhygiene_score() * 0.35f);
            else
                total = (float) (report.getStaffhygiene_score() * 0.2f + report.getHousekeeping_score() * 0.4f + report.getSafety_score() * 0.4f);
            return new float[]{total, 100f - total};
        }
    }

    private void loadUserType() {
        synchronized (lock) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
            userType = sharedPreferences.getString("USER_TYPE_KEY", null);
            if (!userType.equals("Auditor")) {
                tenantType = userType;
            }
            lock.notifyAll();
        }
    }

    private void initApiCaller() {
        synchronized (lock) {
            apiCaller = new Retrofit.Builder()
                    .baseUrl("https://esc10-303807.et.r.appspot.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(DatabaseApiCaller.class);
            lock.notifyAll();
        }
    }

    private void initData() {
        Log.d(TAG, "initData: before lock");
        synchronized (lock) {
            while (tenantType == null) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "initData: got past lock");
            if (tenantType.equals("F&B")) {
                data = new float[][]{new float[]{report.getStaffhygiene_score(), 100f - report.getStaffhygiene_score()},
                        new float[]{report.getHousekeeping_score(), 100f - report.getHousekeeping_score()},
                        new float[]{report.getSafety_score(), 100f - report.getSafety_score()},
                        new float[]{report.getHealthierchoice_score(), 100f - report.getHealthierchoice_score()},
                        new float[]{report.getFoodhygiene_score(), 100f - report.getFoodhygiene_score()}};
                chart1 = view.findViewById(R.id.reportBarChart1);
                chart2 = view.findViewById(R.id.reportBarChart2);
                chart3 = view.findViewById(R.id.reportBarChart3);
                chart4 = view.findViewById(R.id.reportBarChart4);
                chart5 = view.findViewById(R.id.reportBarChart5);

                chart3.setVisibility(View.VISIBLE);
                chart4.setVisibility(View.VISIBLE);
                view.findViewById(R.id.chart3Text).setVisibility(View.VISIBLE);
                view.findViewById(R.id.chart4Text).setVisibility(View.VISIBLE);
                barChartOperation(chart1, 0);
                barChartOperation(chart2, 1);
                barChartOperation(chart3, 2);
                barChartOperation(chart4, 3);
                barChartOperation(chart5, 4);
            } else if (tenantType.equals("Non F&B")) {
                data = new float[][]{new float[]{report.getStaffhygiene_score(), 100f - report.getStaffhygiene_score()},
                        new float[]{report.getHousekeeping_score(), 100f - report.getHousekeeping_score()},
                        new float[]{report.getSafety_score(), 100f - report.getSafety_score()}};
                chart1 = view.findViewById(R.id.reportBarChart1);
                chart2 = view.findViewById(R.id.reportBarChart2);
                chart5 = view.findViewById(R.id.reportBarChart5);

                TextView textView1 = view.findViewById(R.id.chart1Text);
                TextView textView2 = view.findViewById(R.id.chart2Text);
                TextView textView5 = view.findViewById(R.id.chart5Text);
                textView1.setText("Professionalism and Staff Hygiene (20%)");
                textView2.setText("Housekeeping & General Cleanliness (40%)");
                textView5.setText("Workplace Safety & Health (40%)");
                barChartOperation(chart1, 0);
                barChartOperation(chart2, 1);
                barChartOperation(chart5, 2);
            }
        }
    }

    private void setBar6() {
        chart6 = view.findViewById(R.id.reportBarChart6);
        barEntries = new ArrayList<>();
        float[] dataFloat = getTotalScore(report);
        barEntries.add(new BarEntry(0, dataFloat));
        Log.d(TAG, "onCreateView: score: "+ Arrays.toString(dataFloat));
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
    }

}