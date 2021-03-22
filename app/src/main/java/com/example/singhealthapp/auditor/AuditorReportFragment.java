package com.example.singhealthapp.auditor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.singhealthapp.DatabaseApiCaller;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Token;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuditorReportFragment extends Fragment {
    Report report;
    View view;
    TextView company, location;
    HorizontalBarChart chart1, chart2, chart3, chart4, chart5;
    ArrayList<BarEntry> barEntries;
    BarData barData;
    BarDataSet barDataSet;
    float[][] data;
    int[] colors = new int[]{Color.GREEN, Color.GRAY};

    public AuditorReportFragment(Report report) {
        this.report = report;
        data = new float[][]{new float[]{report.getStaffhygiene_score()*100f, 100f - report.getStaffhygiene_score()*100f},
                new float[]{report.getHousekeeping_score()*100f, 100f - report.getHousekeeping_score()*100f},
                new float[]{report.getSafety_score()*100f,  100f - report.getSafety_score()*100f},
                new float[]{report.getHealthierchoice_score()*100f,  100f - report.getHealthierchoice_score()*100f},
                new float[]{report.getFoodhygiene_score()*100f,  100f - report.getFoodhygiene_score()*100f}};
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Report " + report.getId());
        view = inflater.inflate(R.layout.fragment_auditor_report, container, false);

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

        company = view.findViewById(R.id.reportCompany);
        company.setText("COMPANY: " + report.getCompany());
        location = view.findViewById(R.id.reportLocation);
        location.setText("LOCATION: " + report.getLocation());

        return view;
    }

    protected void barChartOperation(HorizontalBarChart chart, int i){
        barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, data[i]));
        barDataSet = new BarDataSet(barEntries, "");
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