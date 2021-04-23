package com.example.singhealthapp.Views.Common.Statistics;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class StatsViewPagerAdapter extends FragmentPagerAdapter {

    private Fragment performanceTab, reportTab, totalTab;

    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> stringList = new ArrayList<>();

    public StatsViewPagerAdapter(@NotNull FragmentManager fm) {
        super(fm);
        performanceTab = new PerformanceStatsFragment();
        reportTab = new ReportStatsFragment();
        totalTab = new TotalScoreStatsFragment();

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return stringList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        stringList.add(title);
    }
}
