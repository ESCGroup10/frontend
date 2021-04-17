package com.example.singhealthapp.Views.Statistics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.singhealthapp.HelperClasses.CustomFragment;
import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.HelperClasses.Ping;
import com.example.singhealthapp.R;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StatisticsFragment extends CustomFragment {

    private EditText tenantIdEditText;
    private Button searchButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String tenantId, token;

    private static List<TenantIdUpdateListener> listenerList = new ArrayList<>();

    private final String TOKEN_KEY = "TOKEN_KEY";

    private DatabaseApiCaller apiCaller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        EspressoCountingIdlingResource.increment();
        getActivity().setTitle("View Statistics");
        View view = inflater.inflate(R.layout.f_stats, container, false);

        tabLayout = view.findViewById(R.id.stats_tabLayout);
        viewPager = view.findViewById(R.id.stats_viewPager);

        getTabs();
        loadToken();

        // create an api caller to the webserver
        apiCaller = new Retrofit.Builder()
                .baseUrl("https://esc10-303807.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);

        tenantIdEditText = view.findViewById(R.id.tenantId_edittext);
        searchButton = view.findViewById(R.id.searchTenantId_button);

        searchButton.setOnClickListener(v -> {

            if (!(tenantIdEditText.length() == 0)) {
                tenantId = tenantIdEditText.getText().toString();
                try {
                    tenantIdUpdate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "Please fill in a tenant ID", Toast.LENGTH_SHORT).show();
            }
            System.out.println("Activity context: " + getContext());

        });
        EspressoCountingIdlingResource.decrement();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void getTabs() {
        final StatsViewPagerAdapter statsViewPagerAdapter = new StatsViewPagerAdapter(getParentFragmentManager());

        statsViewPagerAdapter.addFragment(ReportStatsFragment.getInstance(), "REPORT");
        statsViewPagerAdapter.addFragment(TotalScoreStatsFragment.getInstance(), "TOTAL");

        viewPager.setAdapter(statsViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public interface TenantIdUpdateListener {
        void onTenantIdUpdate(String tenantId, String token, DatabaseApiCaller apiCaller) throws IOException;
    }

    public static synchronized void registerTenantIdUpdateListener(TenantIdUpdateListener listener) {
        listenerList.add(listener);
    }

    public static synchronized void unregisterTenantIdUpdateListener (TenantIdUpdateListener listener) {
        listenerList.remove(listener);
    }

    public synchronized void tenantIdUpdate() throws IOException {
        System.out.println("ListenerList: " + listenerList);
        for (TenantIdUpdateListener l : listenerList) {
            l.onTenantIdUpdate(tenantId, token, apiCaller);
        }
    }

    private void loadToken() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN_KEY", null);
    }

}