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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.singhealthapp.HelperClasses.CustomFragment;
import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Tenant;
import com.example.singhealthapp.R;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StatisticsFragment extends CustomFragment {

    private AutoCompleteTextView tenantIdEditText;
    private Button searchButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String tenantId, token, userType;
    private ArrayList<String> tenantList;
    private ArrayList<Integer> tenantIdList;

    private static List<TenantIdUpdateListener> listenerList = new ArrayList<>();

    private DatabaseApiCaller apiCaller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("View Statistics");
        View view = inflater.inflate(R.layout.f_stats, container, false);

        getTabs(view);
        loadData();

        // create an api caller to the webserver
        apiCaller = new Retrofit.Builder()
                .baseUrl("https://esc10-303807.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);

        tenantIdEditText = view.findViewById(R.id.tenantId_edittext);
        searchButton = view.findViewById(R.id.searchTenantId_button);

        if (!userType.equals("Auditor")) {
            setInvisibleView();
            getTenantData();
        } else {
            getTenants();
            setSearchButton();
        }
        EspressoCountingIdlingResource.decrement();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!userType.equals("Auditor")) {
            getTenantData();
        }
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

    private void loadData() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN_KEY", null);
        userType = sharedPreferences.getString("USER_TYPE_KEY", null);
        tenantId = String.valueOf(sharedPreferences.getInt("USER_ID_KEY", 0));
    }

    private void getTenantData() {
        new Thread(() -> {
            try {
                Thread.sleep(500);
                tenantIdUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void getTenants() {
        Call<List<Tenant>> call = apiCaller.getTenant("Token " + token);
            call.enqueue(new Callback<List<Tenant>>() {
                @Override
                public void onResponse(Call<List<Tenant>> call, Response<List<Tenant>> response) {
                    if (response.code() == 200) {
                        parseTenants(response);
                        setAdapter();
                    }
                    System.out.println("Response code is " + response.code());
                }
                @Override
                public void onFailure(Call<List<Tenant>> call, Throwable t) {
                    Toast.makeText(getActivity(), "Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

    }

    private void parseTenants(Response<List<Tenant>> response) {
        List<Tenant> responseBody = response.body();
        tenantIdList = new ArrayList<>();
        tenantList = new ArrayList<>();
        for (Tenant t : responseBody) {
            tenantList.add(t.getName() + " " + t.getId());
            tenantIdList.add(t.getId());
        }

        System.out.println("" + tenantList);
    }

    private void setAdapter() {
        System.out.println("Activity: " + getActivity());
        System.out.println("Activity: " + getContext());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, tenantList);
        tenantIdEditText.setAdapter(adapter);
    }

    private void setSearchButton() {
        searchButton.setOnClickListener(v -> {
            if (!(tenantIdEditText.length() == 0)) {
                searchQuery();
                try {
                    tenantIdUpdate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "Please fill in a tenant ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchQuery() {
        String tenantQuery = tenantIdEditText.getText().toString();
        if (tenantList.contains(tenantQuery)) {
            tenantId = String.valueOf(tenantIdList.get(tenantList.indexOf(tenantQuery)));
        }
    }

    private void getTabs(View view) {
        tabLayout = view.findViewById(R.id.stats_tabLayout);
        viewPager = view.findViewById(R.id.stats_viewPager);

        final StatsViewPagerAdapter statsViewPagerAdapter = new StatsViewPagerAdapter(getChildFragmentManager());

        statsViewPagerAdapter.addFragment(ReportStatsFragment.getInstance(), "Non-compliance Frequencies");
        statsViewPagerAdapter.addFragment(TotalScoreStatsFragment.getInstance(), "Audit Scores");

        viewPager.setAdapter(statsViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setInvisibleView() {
        tenantIdEditText.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);
    }
}