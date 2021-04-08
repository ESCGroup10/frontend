package com.example.singhealthapp.Views.Statistics;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.ReportedCases;
import com.example.singhealthapp.Models.ResolvedCases;
import com.example.singhealthapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportStatsFragment extends Fragment implements StatisticsFragment.TenantIdUpdateListener {

    LineChart mChart;
    ArrayList<Entry> reportCount = new ArrayList<>();
    ArrayList<Entry> resolveCount = new ArrayList<>();

    public static ReportStatsFragment getInstance() {
        return new ReportStatsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        System.out.println("Context Report attach");

        StatisticsFragment.registerTenantIdUpdateListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StatisticsFragment.unregisterTenantIdUpdateListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats_reports, container, false);
        mChart = view.findViewById(R.id.reports_chart);
        return view;
    }

    @Override
    public void onTenantIdUpdate(String tenantId, String token, DatabaseApiCaller apiCaller) throws IOException {
        Call<List<ReportedCases>> getReportedCases = apiCaller.getReportedCase("Token " + token);
        Call<List<ResolvedCases>> getResolvedCases = apiCaller.getResolvedCase("Token " + token);

        getReportedCases.enqueue(new Callback<List<ReportedCases>>() {
            @Override
            public void onResponse(Call<List<ReportedCases>> call, Response<List<ReportedCases>> response) {
                if (response.code() == 200) {
                    List<ReportedCases> responseBody = response.body();

                    reportCount.clear();
                    resolveCount.clear();
                    for (int i=0; i<responseBody.size(); i++) { reportCount.add(new Entry(i, responseBody.get(i).getCount())); }

                    getResolvedCases.enqueue(new Callback<List<ResolvedCases>>() {
                        @Override
                        public void onResponse(Call<List<ResolvedCases>> call, Response<List<ResolvedCases>> response) {
                            if (response.code() == 200) {
                                List<ResolvedCases> responseBody = response.body();
                                for (int i = 0; i < responseBody.size(); i++) { resolveCount.add(new Entry(i, responseBody.get(i).getCount())); }
                            }
                            plotChart();
                        }
                        @Override
                        public void onFailure(Call<List<ResolvedCases>> call, Throwable t) { }
                    });
                }
            }
            @Override
            public void onFailure(Call<List<ReportedCases>> call, Throwable t) { }
        });

        System.out.println("ReportStatsFragment UPDATED!" + tenantId);
    }

    private void plotChart() {
        LineDataSet set1, set2;
        set1 = new LineDataSet(reportCount, "No. of Reported Cases");
        set1.setColor(Color.RED);
        set1.setCircleColor(Color.RED);

        set2 = new LineDataSet(resolveCount, "No. of Resolved Cases");
        set1.setColor(Color.GREEN);
        set1.setCircleColor(Color.GREEN);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);

        LineData data = new LineData(dataSets);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getXAxis().setDrawLabels(false);
        mChart.setData(data);
        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }
}
