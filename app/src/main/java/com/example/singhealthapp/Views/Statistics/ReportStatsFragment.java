package com.example.singhealthapp.Views.Statistics;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportStatsFragment extends Fragment implements StatisticsFragment.TenantIdUpdateListener {

    LineChart mChart;
    Button mExportButton;
    ArrayList<Entry> reportCount = new ArrayList<>();
    ArrayList<Entry> resolveCount = new ArrayList<>();

    String[] reportCases, resolvedCases;

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
        View view = inflater.inflate(R.layout.f_stats_reports, container, false);
        mChart = view.findViewById(R.id.reports_chart);
        mExportButton = view.findViewById(R.id.exportcases_button);

        mExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    exportData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    @Override
    public void onTenantIdUpdate(String tenantId, String token, DatabaseApiCaller apiCaller) throws IOException {
        mExportButton.setEnabled(false);
        Call<List<ReportedCases>> getReportedCases = apiCaller.getReportedCase("Token " + token, Integer.parseInt(tenantId));
        Call<List<ResolvedCases>> getResolvedCases = apiCaller.getResolvedCase("Token " + token, Integer.parseInt(tenantId));

        getReportedCases.enqueue(new Callback<List<ReportedCases>>() {
            @Override
            public void onResponse(Call<List<ReportedCases>> call, Response<List<ReportedCases>> response) {

                if (response.isSuccessful()) {
                    List<ReportedCases> responseBody = response.body();

                    resetArray(responseBody);

                    for (int i=0; i<responseBody.size(); i++) {
                        reportCount.add(new Entry(i, responseBody.get(i).getCount()));
                        reportCases[i] = String.valueOf(responseBody.get(i).getCount());
                    }

                    getResolvedCases.enqueue(new Callback<List<ResolvedCases>>() {
                        @Override
                        public void onResponse(Call<List<ResolvedCases>> call, Response<List<ResolvedCases>> response) {

                            if (response.isSuccessful()) {
                                List<ResolvedCases> responseBody = response.body();
                                for (int i = 0; i < responseBody.size(); i++) {
                                    resolveCount.add(new Entry(i, responseBody.get(i).getCount()));
                                    resolvedCases[i] = String.valueOf(responseBody.get(i).getCount());
                                }
                            }
                            plotChart();
                        }
                        @Override
                        public void onFailure(Call<List<ResolvedCases>> call, Throwable t) {
                            Toast.makeText(getActivity(), "" + t, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<List<ReportedCases>> call, Throwable t) {
                Toast.makeText(getActivity(), "" + t, Toast.LENGTH_SHORT).show();
            }
        });

        System.out.println("ReportStatsFragment UPDATED!" + tenantId);
    }

    private void resetArray(List<ReportedCases> responseBody) {
        reportCount.clear();
        resolveCount.clear();

        reportCases = new String[responseBody.size()];
        resolvedCases = new String[responseBody.size()];
    }



    private void plotChart() {

        if (!reportCount.isEmpty()) {
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

            mExportButton.setEnabled(true);
        } else {
            Toast.makeText(getActivity(), "No relevant data found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void exportData() throws IOException {
        //generate data
        StringBuilder data = new StringBuilder();
        data.append("ReportedCases,ResolvedCases");
        for (int i = 0; i < reportCases.length; i++) {
            data.append("\n" + reportCases[i] + "," + resolvedCases[i]);
        }

        try {
            //saving the file into device
            FileOutputStream out = getActivity().getApplicationContext().openFileOutput("datafile.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();

            //exporting
            Context context = getActivity().getApplicationContext();
            File filelocation = new File(getActivity().getApplicationContext().getFilesDir(), "datafile.csv");
            Uri path = FileProvider.getUriForFile(context, "com.example.android.fileprovider", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Reported & Resolved Cases");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Send mail"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
