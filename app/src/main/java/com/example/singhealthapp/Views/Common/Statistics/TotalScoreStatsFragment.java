package com.example.singhealthapp.Views.Common.Statistics;

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

import com.example.singhealthapp.HelperClasses.CustomFragment;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Report;
import com.example.singhealthapp.Models.User;
import com.example.singhealthapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TotalScoreStatsFragment extends CustomFragment implements StatisticsFragment.TenantIdUpdateListener {

    LineChart mChart;
    Button mExportButton;

    DatabaseApiCaller apiCaller;
    Call<List<User>> getUserCall;
    String token, tenantId, userType;

    ArrayList<String> reportIds = new ArrayList<>();
    ArrayList<Entry> scores = new ArrayList<>();
    ArrayList<Entry> foodHygiene = new ArrayList<>();
    ArrayList<Entry> healthierChoice = new ArrayList<>();
    ArrayList<Entry> safety = new ArrayList<>();
    ArrayList<Entry> staffHygiene = new ArrayList<>();
    ArrayList<Entry> houseKeeping = new ArrayList<>();

    StringBuilder data = new StringBuilder();

    String[] mScores, mFoodHygiene, mHealthierChoice, mSafety, mStaffHygiene, mHouseKeeping;

    float count = 0;

    public static TotalScoreStatsFragment getInstance() {
        return new TotalScoreStatsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_stats_totalscores, container, false);

        mChart = view.findViewById(R.id.chart);
        mExportButton = view.findViewById(R.id.exportscore_button);

        mExportButton.setOnClickListener(v -> new Thread(() -> {
            generateScores();
            exportData();
        }).start());

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        StatisticsFragment.registerTenantIdUpdateListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StatisticsFragment.unregisterTenantIdUpdateListener(this);
    }

    // api call to get the tenant's audit performance scores
    @Override
    public void onTenantIdUpdate(String tenantId, String token, DatabaseApiCaller apiCaller) {
        this.token = token;
        this.tenantId = tenantId;
        this.apiCaller = apiCaller;

        mExportButton.setEnabled(false);
        Call<List<Report>> getScores = this.apiCaller.getScoresById("Token " + token, Integer.parseInt(tenantId));

        getScores.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
                List<Report> responseBody = response.body();

                if (response.code() == 200) {
                    new Thread(() -> {
                        getUserType(responseBody);
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<List<Report>> call, Throwable t) {
                Toast.makeText(getActivity(), "" + t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void plotChart() {
//        getActivity().runOnUiThread(() -> {
        new Thread(()->{

            if (!scores.isEmpty()) {
                LineDataSet set, set1, set2, set3, set4, set5, set6;
                set1 = new LineDataSet(scores, "Total Score ");
                set1.setColor(Color.GREEN);
                set1.setCircleColor(Color.GREEN);

                set2 = new LineDataSet(houseKeeping, "Housekeeping & General Cleanliness ");
                set2.setColor(Color.RED);
                set2.setCircleColor(Color.RED);

                set3 = new LineDataSet(safety, "Workplace Safety & Health ");
                set3.setColor(Color.GRAY);
                set3.setCircleColor(Color.GRAY);

                set4 = new LineDataSet(staffHygiene, "Professionalism & Staff Hygiene ");
                set4.setColor(Color.YELLOW);
                set4.setCircleColor(Color.YELLOW);

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();

                dataSets.add(set1);
                dataSets.add(set2);
                dataSets.add(set3);
                dataSets.add(set4);

                if (userType.equals("F&B")) {
                    set5 = new LineDataSet(foodHygiene, "Food Hygiene ");
                    set5.setColor(Color.BLUE);
                    set5.setCircleColor(Color.BLUE);

                    set6 = new LineDataSet(healthierChoice, "Healthier Choice");
                    set6.setColor(Color.MAGENTA);
                    set6.setCircleColor(Color.MAGENTA);

                    dataSets.add(set5);
                    dataSets.add(set6);
                }

                LineData data = new LineData(dataSets);

                mChart.setExtraBottomOffset(20);
                mChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                mChart.getLegend().setTextSize(12);
                mChart.getDescription().setEnabled(false);
                mChart.getAxisLeft().setDrawGridLines(false);
                mChart.getXAxis().setGranularityEnabled(true);
                mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
                mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(reportIds));
                mChart.getXAxis().setDrawGridLines(false);
                mChart.setData(data);

                mChart.notifyDataSetChanged();
                mChart.invalidate();
                mExportButton.setEnabled(true);
            } else {
                Toast.makeText(getActivity(), "No relevant data found.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void clearArray() {
        reportIds.clear();
        scores.clear();
        foodHygiene.clear();
        healthierChoice.clear();
        houseKeeping.clear();
        staffHygiene.clear();
        safety.clear();
    }

    private void createArray(List<Report> responseBody) {
        mScores = new String[responseBody.size()];
        mFoodHygiene = new String[responseBody.size()];
        mSafety = new String[responseBody.size()];
        mHouseKeeping = new String[responseBody.size()];
        mHealthierChoice = new String[responseBody.size()];
        mStaffHygiene = new String[responseBody.size()];
        mSafety = new String[responseBody.size()];
    }

    private void loopData(List<Report> responseBody) {
        count = 0;
        for (Report r : responseBody) {
            aggregateData(r);
            count++;
        }
    }

    private void aggregateData(Report r) {
        reportIds.add("Report " + r.getId());

        if (userType.equals("F&B")) {
            foodHygiene.add(new Entry (count, r.getFoodhygiene_score()));
            healthierChoice.add(new Entry (count, r.getHealthierchoice_score()));
            mFoodHygiene[((int) count)] = String.valueOf(r.getFoodhygiene_score());
            mHealthierChoice[((int) count)] = String.valueOf(r.getHealthierchoice_score());
            scores.add(new Entry(count, (r.getFoodhygiene_score()+r.getHealthierchoice_score()+r.getHousekeeping_score()+r.getStaffhygiene_score()+r.getSafety_score())/5));
            mScores[((int) count)] = String.valueOf((r.getFoodhygiene_score()+r.getHealthierchoice_score()+r.getHousekeeping_score()+r.getStaffhygiene_score()+r.getSafety_score())/5);
        } else {
            scores.add(new Entry(count, (r.getHousekeeping_score()+r.getStaffhygiene_score()+r.getSafety_score())/3));
            mScores[((int) count)] = String.valueOf((r.getHousekeeping_score()+r.getStaffhygiene_score()+r.getSafety_score())/3);
        }

        houseKeeping.add(new Entry (count, r.getHousekeeping_score()));
        staffHygiene.add(new Entry (count, r.getStaffhygiene_score()));
        safety.add(new Entry (count, r.getSafety_score()));

        mHouseKeeping[((int) count)] = String.valueOf(r.getHousekeeping_score());
        mStaffHygiene[((int) count)] = String.valueOf(r.getStaffhygiene_score());
        mSafety[((int) count)] = String.valueOf(r.getSafety_score());

    }

    private void getUserType(List<Report> responseBody) {
        getUserCall = apiCaller.getSingleUserById("Token " + token, tenantId);
        getUserCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                System.out.println("Response code: " + response.code());
                userType = response.body().get(0).getType();

                clearArray();
                createArray(responseBody);
                loopData(responseBody);
                plotChart();
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                System.out.println("FAILEEEED");
            }
        });
    }

    private void exportData() {
        try {
            saveData();
            exportFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateScores() {
        if (userType.equals("F&B")) {
            data.append("Report,Total Scores,Food Hygiene,Healthier Choice,Housekeeping & General Cleanliness,Professionalism & Staff Hygiene,Workplace Safety & Health");
            for (int i = 0; i<mScores.length; i++){
                data.append("\n"+reportIds.get(i)+","+mScores[i]+","+mFoodHygiene[i]+","+mHealthierChoice[i]+","+mHouseKeeping[i]+","+mStaffHygiene[i]+","+mSafety[i]);
            }
        } else {
            data.append("Report,Total Scores,Housekeeping & General Cleanliness,Professionalism & Staff Hygiene,Workplace Safety & Health");
            for (int i = 0; i< mScores.length; i++){
                data.append("\n"+reportIds.get(i)+","+mScores[i]+","+mHouseKeeping[i]+","+mStaffHygiene[i]+","+mSafety[i]);
            }
        }
    }

    private void saveData() throws IOException {
        FileOutputStream out = getActivity().getApplicationContext().openFileOutput("datafile.csv", Context.MODE_PRIVATE);
        out.write((data.toString()).getBytes());
        out.close();
    }

    private void exportFile() {
        Context context = getActivity().getApplicationContext();
        File filelocation = new File(getActivity().getApplicationContext().getFilesDir(), "datafile.csv");
        Uri path = FileProvider.getUriForFile(context, "com.example.android.fileprovider", filelocation);
        Intent fileIntent = new Intent(Intent.ACTION_SEND);
        fileIntent.setType("text/csv");
        fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Tenant" + tenantId + "_AuditScores");
        fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        fileIntent.putExtra(Intent.EXTRA_STREAM, path);
        startActivity(Intent.createChooser(fileIntent, "Send mail"));
    }

}
