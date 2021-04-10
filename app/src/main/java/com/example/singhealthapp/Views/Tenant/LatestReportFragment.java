package com.example.singhealthapp.Views.Tenant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.singhealthapp.Models.Case;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Report;
import com.example.singhealthapp.R;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LatestReportFragment extends Fragment {
    HorizontalBarChart chart1, chart2, chart3, chart4, chart5;
    ArrayList<BarEntry> barEntries;
    BarData barData;
    BarDataSet barDataSet;
    float[][] data;
    int[] colors;
    TextView resolved, unresolved, date, resolvedText;
    private String token;
    private int userId;
    Report report;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Latest Report");
        View view = inflater.inflate(R.layout.fragment_latest_report, container, false);

        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
            token = sharedPreferences.getString("TOKEN_KEY", null);
            userId = sharedPreferences.getInt("USER_ID_KEY", 0);
        }
        catch (Exception ignored){
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://esc10-303807.et.r.appspot.com").addConverterFactory(GsonConverterFactory.create()).build();
        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);
        Call<List<Report>> call = apiCaller.getReport("Token " + token);
        int finalUserId = userId;
        if ( ! token.isEmpty() ) call.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Unsuccessful: response code " + response.code(), Toast.LENGTH_LONG).show();
                    return ;
                }
                ArrayList<Report> reports = new ArrayList<>();
                for (Report r : response.body()){
                    if (r.getTenant_id() == finalUserId) reports.add(r);
                }
                if (reports.size() > 0) {
                    report = reports.get(reports.size()-1);
                    data = new float[][]{new float[]{report.getStaffhygiene_score()*100f, 100f - report.getStaffhygiene_score()*100f},
                            new float[]{report.getHousekeeping_score()*100f, 100f - report.getHousekeeping_score()*100f},
                            new float[]{report.getSafety_score()*100f,  100f - report.getSafety_score()*100f},
                            new float[]{report.getHealthierchoice_score()*100f,  100f - report.getHealthierchoice_score()*100f},
                            new float[]{report.getFoodhygiene_score()*100f,  100f - report.getFoodhygiene_score()*100f}};
                    chart1 = view.findViewById(R.id.reportBarChart1);
                    barChartOperation(chart1, 0);
                    chart2 = view.findViewById(R.id.reportBarChart2);
                    barChartOperation(chart2, 1);
                    chart3 = view.findViewById(R.id.reportBarChart3);
                    barChartOperation(chart3, 2);
                    chart4 = view.findViewById(R.id.reportBarChart4);
                    barChartOperation(chart4, 3);
                    chart5 = view.findViewById(R.id.reportBarChart5);
                    barChartOperation(chart5, 4);
                    date = view.findViewById(R.id.dateLatestReport);
                    date.setText(report.getReport_date().substring(0,10) + " " + report.getReport_date().substring(11, 19));
                    resolvedText = view.findViewById(R.id.resolvedLatestReport);
                    if (report.isStatus()) {
                        resolvedText.setText("COMPLETED");
                        resolvedText.setTextColor(Color.rgb(159, 221, 88));
                    }
                    else {
                        resolvedText.setText("UNRESOLVED");
                        resolvedText.setTextColor(Color.rgb(239, 117, 119));
                    }
                    resolved = view.findViewById(R.id.auditorReportResolved);
                    unresolved = view.findViewById(R.id.auditorReportUnresolved);
                    Call<List<Case>> caseCall = apiCaller.getCasesById("Token " + token, report.getId(), 1);
                    caseCall.enqueue(new Callback<List<Case>>(){
                        @Override
                        public void onResponse(Call<List<Case>> call, Response<List<Case>> response) {
                            resolved.setText(String.valueOf(response.body().size()));
                            Call<List<Case>> caseCall = apiCaller.getCasesById("Token " + token, report.getId(), 0);
                            caseCall.enqueue(new Callback<List<Case>>(){
                                @Override
                                public void onResponse(Call<List<Case>> call, Response<List<Case>> response) {
                                    unresolved.setText(String.valueOf(response.body().size()));
                                }
                                @Override
                                public void onFailure(Call<List<Case>> call, Throwable t) {
                                }
                            });
                        }
                        @Override
                        public void onFailure(Call<List<Case>> call, Throwable t) {
                        }
                    });
                }
                else report = null;
            }
            @Override
            public void onFailure(Call<List<Report>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    protected void barChartOperation(HorizontalBarChart chart, int i){
        barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, data[i]));
        barDataSet = new BarDataSet(barEntries, "");
        if ( data[i][0] >= 70 ) colors = new int[]{Color.rgb(159, 221, 88), Color.rgb(170, 170, 170)};
        else if ( data[i][0] >= 35 ) colors = new int[]{Color.rgb(237, 135, 40), Color.rgb(170, 170, 170)};
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

}