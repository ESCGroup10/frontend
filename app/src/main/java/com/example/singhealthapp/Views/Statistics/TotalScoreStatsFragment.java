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
import com.example.singhealthapp.Models.Report;
import com.example.singhealthapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TotalScoreStatsFragment extends Fragment implements StatisticsFragment.TenantIdUpdateListener {

    LineChart mChart;
    ArrayList<Entry> scores = new ArrayList<>();
    ArrayList<Entry> foodHygiene = new ArrayList<>();
    ArrayList<Entry> healthierChoice = new ArrayList<>();
    ArrayList<Entry> safety = new ArrayList<>();
    ArrayList<Entry> staffHygiene = new ArrayList<>();
    ArrayList<Entry> houseKeeping = new ArrayList<>();
    float count = 0;

    public static TotalScoreStatsFragment getInstance() {
        return new TotalScoreStatsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats_totalscores, container, false);

        mChart = view.findViewById(R.id.chart);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        System.out.println("Context Score attach");
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
        Call<List<Report>> getScores = apiCaller.getScoresById("Token " + token, Integer.parseInt(tenantId));

        getScores.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
                if (response.code() == 200) {
                    List<Report> responseBody = response.body();

                    scores.clear();
                    foodHygiene.clear();
                    healthierChoice.clear();
                    houseKeeping.clear();
                    staffHygiene.clear();
                    safety.clear();

                    for (Report r : responseBody) {
                        scores.add(new Entry(count, (r.getFoodhygiene_score()+r.getHealthierchoice_score()+r.getHousekeeping_score()+r.getStaffhygiene_score()+r.getSafety_score())/5*100));
                        foodHygiene.add(new Entry (count, r.getFoodhygiene_score()*100));
                        healthierChoice.add(new Entry (count, r.getHealthierchoice_score()*100));
                        houseKeeping.add(new Entry (count, r.getHousekeeping_score()*100));
                        staffHygiene.add(new Entry (count, r.getStaffhygiene_score()*100));
                        safety.add(new Entry (count, r.getSafety_score()*100));
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
}
