package com.example.singhealthapp.Views.Common.Statistics;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.singhealthapp.HelperClasses.CustomFragment;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.R;


public class PerformanceStatsFragment extends CustomFragment implements StatisticsFragment.TenantIdUpdateListener {

    public static PerformanceStatsFragment getInstance() {
        return new PerformanceStatsFragment();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        System.out.println("Context Performance attach");

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
        View view = inflater.inflate(R.layout.f_stats_performance, container, false);
        return view;
    }

    @Override
    public void onTenantIdUpdate(String tenantId, String token, DatabaseApiCaller apiCaller) {

        System.out.println("PerformanceStatsFragment UPDATED!" + tenantId);
    }
}
