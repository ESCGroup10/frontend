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

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Report;
import com.example.singhealthapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TotalScoreStatsFragment extends Fragment implements StatisticsFragment.TenantIdUpdateListener {

    LineChart mChart;
    Button mExportButton;
    ArrayList<Entry> scores = new ArrayList<>();
    ArrayList<Entry> foodHygiene = new ArrayList<>();
    ArrayList<Entry> healthierChoice = new ArrayList<>();
    ArrayList<Entry> safety = new ArrayList<>();
    ArrayList<Entry> staffHygiene = new ArrayList<>();
    ArrayList<Entry> houseKeeping = new ArrayList<>();

    String[] mScores, mFoodHygiene, mHealthierChoice, mSafety, mStaffHygiene, mHouseKeeping;

    float count = 0;

    public static TotalScoreStatsFragment getInstance() {
        return new TotalScoreStatsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats_totalscores, container, false);

        mChart = view.findViewById(R.id.chart);
        mExportButton = view.findViewById(R.id.exportscore_button);

        mExportButton.setOnClickListener(v -> {
            try {
                exportData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        System.out.println("Context Score attach");
        StatisticsFragment.registerTenantIdUpdateListener(this);

        ActivityResultContract contract = new ActivityResultContract() {
            @NonNull
            @Override
            public Intent createIntent(@NonNull Context context, Object input) {
                return null;
            }

            @Override
            public Object parseResult(int resultCode, @Nullable Intent intent) {
                return null;
            }
        };

        ActivityResultCallback callback = new ActivityResultCallback() {
            @Override
            public void onActivityResult(Object result) {

            }
        };

        ActivityResultLauncher launcher = TotalScoreStatsFragment.this.registerForActivityResult(contract, callback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StatisticsFragment.unregisterTenantIdUpdateListener(this);
    }

    // api call to get the tenant's audit performance scores
    @Override
    public void onTenantIdUpdate(String tenantId, String token, DatabaseApiCaller apiCaller) {
        Call<List<Report>> getScores = apiCaller.getScoresById("Token " + token, Integer.parseInt(tenantId));

        getScores.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
                if (response.code() == 200) {
                    List<Report> responseBody = response.body();

                    resetArray(responseBody);

                    for (Report r : responseBody) {
                        aggregateData(r);
                        count++;
                    }
                    plotChart();

                }
            }

            @Override
            public void onFailure(Call<List<Report>> call, Throwable t) { }
        });
        System.out.println("TotalScoreStatsFragment UPDATED!" + tenantId);
    }

    private void plotChart() {
        LineDataSet set, set1, set2, set3, set4, set5, set6;
        set1 = new LineDataSet(scores, "Total");
        set1.setColor(Color.GREEN);
        set1.setCircleColor(Color.GREEN);

        set2 = new LineDataSet(houseKeeping, "Housekeeping & General Cleanliness");
        set2.setColor(Color.RED);
        set2.setCircleColor(Color.RED);

        set3 = new LineDataSet(safety, "Workplace Safety & Health");
        set3.setColor(Color.GRAY);
        set3.setCircleColor(Color.GRAY);


        set4 = new LineDataSet(staffHygiene, "Professionalism & Staff Hygiene");
        set4.setColor(Color.YELLOW);
        set4.setCircleColor(Color.YELLOW);


        set5 = new LineDataSet(foodHygiene, "Food Hygiene");
        set5.setColor(Color.BLUE);
        set5.setCircleColor(Color.BLUE);


        set6 = new LineDataSet(healthierChoice, "Healthier Choice");
        set6.setColor(Color.MAGENTA);
        set6.setCircleColor(Color.MAGENTA);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        dataSets.add(set1);
        dataSets.add(set2);
        dataSets.add(set3);
        dataSets.add(set4);
        dataSets.add(set5);
        dataSets.add(set6);

        LineData data = new LineData(dataSets);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getXAxis().setDrawLabels(false);
        mChart.setData(data);
        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }

    private void resetArray(List<Report> responseBody){

        count = 0;

        scores.clear();
        foodHygiene.clear();
        healthierChoice.clear();
        houseKeeping.clear();
        staffHygiene.clear();
        safety.clear();

        System.out.println("Response body size: " + responseBody.size() );

        mScores = new String[responseBody.size()];
        mFoodHygiene = new String[responseBody.size()];
        mSafety = new String[responseBody.size()];
        mHouseKeeping = new String[responseBody.size()];
        mHealthierChoice = new String[responseBody.size()];
        mStaffHygiene = new String[responseBody.size()];
        mSafety = new String[responseBody.size()];
    }

    private void aggregateData(Report r) {
        scores.add(new Entry(count, (r.getFoodhygiene_score()+r.getHealthierchoice_score()+r.getHousekeeping_score()+r.getStaffhygiene_score()+r.getSafety_score())/5));
        foodHygiene.add(new Entry (count, r.getFoodhygiene_score()));
        healthierChoice.add(new Entry (count, r.getHealthierchoice_score()));
        houseKeeping.add(new Entry (count, r.getHousekeeping_score()));
        staffHygiene.add(new Entry (count, r.getStaffhygiene_score()));
        safety.add(new Entry (count, r.getSafety_score()));

        mScores[((int) count)] = String.valueOf(r.getFoodhygiene_score()+r.getHealthierchoice_score()+r.getHousekeeping_score()+r.getStaffhygiene_score()+r.getSafety_score()/5);
        mFoodHygiene[((int) count)] = String.valueOf(r.getFoodhygiene_score());
        mHealthierChoice[((int) count)] = String.valueOf(r.getHealthierchoice_score());
        mHouseKeeping[((int) count)] = String.valueOf(r.getHousekeeping_score());
        mStaffHygiene[((int) count)] = String.valueOf(r.getStaffhygiene_score());
        mSafety[((int) count)] = String.valueOf(r.getSafety_score());
    }

    private void exportData() throws IOException {
        ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // Permission is granted. Continue the action or workflow in your
                        // app.

                        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                        String fileName = "scores_data.csv";
                        String filePath = baseDir + File.separator + fileName;
                        File f = new File(filePath);

                        System.out.println(filePath);

                        // create FileWriter object with file as parameter
                        FileWriter outputfile = null;
                        try {
                            outputfile = new FileWriter(f);
                            // create CSVWriter object filewriter object as parameter
                            CSVWriter writer = new CSVWriter(outputfile);

                            // adding header to csv
                            String[] header = { "Scores", "Food Hygiene", "Healthier Choice", "House Keeping", "Staff Hygiene", "Safety" };
                            writer.writeNext(header);

                            // add data to csv
                            writer.writeNext(mScores);
                            writer.writeNext(mFoodHygiene);
                            writer.writeNext(mHealthierChoice);
                            writer.writeNext(mHouseKeeping);
                            writer.writeNext(mStaffHygiene);
                            writer.writeNext(mSafety);

                            // closing writer connection
                            writer.close();

                            Toast.makeText(getContext(), "Successful export of data", Toast.LENGTH_SHORT).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        // Explain to the user that the feature is unavailable because the
                        // features requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.

                        Toast.makeText(getContext(), "Permission not granted to create csv file", Toast.LENGTH_SHORT).show();
                    }
                });


    }
}
