package com.example.singhealthapp.Views.Auditor.TenantsPreview;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.HelperClasses.CentralisedToast;
import com.example.singhealthapp.HelperClasses.CustomFragment;
import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Tenant;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Auditor.TenantExpanded.TenantExpandedFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class TenantsPreviewFragment extends CustomFragment implements TenantsPreviewAdapter.NavFromTenantSelection {

    private static final String TAG = "TenantsPreviewFragment";

    TenantsPreviewAdapter adapter;
    private ArrayList<Tenant> tenantPreviews, getTenantPreviews;
    private List<Tenant> tenants, displayTenants;
    private String token;
    private static DatabaseApiCaller apiCaller;
    private Button reloadButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        while (EspressoCountingIdlingResource.getCount() > 0) {
            EspressoCountingIdlingResource.decrement();
        }
        loadToken();
        initApiCaller();
        getActivity().setTitle("Search Tenant");
        View view = inflater.inflate(R.layout.f_tenants_all, container, false);
        reloadButton = view.findViewById(R.id.reloadButton);
        reloadButton.setOnClickListener(v -> {
            TenantsPreviewFragment tenantsPreviewFragment = new TenantsPreviewFragment();
            String tag = tenantsPreviewFragment.getClass().getName();
            getParentFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, tenantsPreviewFragment, tag)
                    .addToBackStack(tag)
                    .commit();
        });
        reloadButton.setVisibility(GONE);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        queueList(token);
    }

    @Override
    public void onStart() {
//        EspressoCountingIdlingResource.decrement();
        super.onStart();
    }

    private synchronized void initApiCaller() {
        apiCaller = new Retrofit.Builder()
                .baseUrl("https://esc10-303807.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);
        notifyAll();
    }

    private synchronized void queueList(String token){
        while (token == null || apiCaller == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "queueList: getting tenant with token: "+token);
        Call<List<Tenant>> call = apiCaller.getTenant("Token " + token);
        call.enqueue(new Callback<List<Tenant>>() {
            @Override
            public void onResponse(Call<List<Tenant>> call, Response<List<Tenant>> response) {
//                EspressoCountingIdlingResource.increment();
                if (!response.isSuccessful()) {
//                    EspressoCountingIdlingResource.decrement();
                    Toast.makeText(getContext(), "Unsuccessful: response code " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                System.out.println("response " + response.code());
                queueReport(token, response.body());
            }
            @Override
            public void onFailure(Call<List<Tenant>> call, Throwable t) {
//                EspressoCountingIdlingResource.decrement();
                Log.d(TAG, "onFailure: queueList");
                t.printStackTrace();
                CentralisedToast.makeText(getActivity(), "Unable to make request to server, please load the page again!", CentralisedToast.LENGTH_LONG);
                getView().findViewById(R.id.reloadButton).setVisibility(VISIBLE);
            }
        });
    }

    private synchronized void queueReport(String token, List<Tenant> tenantSearch){
        while (token == null || apiCaller == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "queueList: getting tenant with token: "+token);
        Call<List<Tenant>> call = apiCaller.getTenant("Token " + token);
        call.enqueue(new Callback<List<Tenant>>() {
            @Override
            public void onResponse(Call<List<Tenant>> call, Response<List<Tenant>> response) {
                if (!response.isSuccessful()) {
//                    EspressoCountingIdlingResource.decrement();
                    Toast.makeText(getContext(), "Unsuccessful: response code " + response.code(), Toast.LENGTH_LONG).show();
                    return ;
                }
                if (response.body().isEmpty() || response.body() == null) {
//                    EspressoCountingIdlingResource.decrement();
                    return;
                }
                adapter = new TenantsPreviewAdapter(tenantSearch, response.body(), getActivity(), token, TenantsPreviewFragment.this);
                tenants = response.body();
                try {
                    RecyclerView view = (RecyclerView) getView().findViewById(R.id.tenantRecycler);
                    view.setLayoutManager(new LinearLayoutManager(getActivity()));
                    view.setItemAnimator(new DefaultItemAnimator());
                    view.setAdapter(adapter);
                }
                catch (Exception e) {
                    System.out.println("recycleView not set");
                }
//                EspressoCountingIdlingResource.decrement();
            }
            @Override
            public void onFailure(Call<List<Tenant>> call, Throwable t) {
//                EspressoCountingIdlingResource.decrement();
                Log.d(TAG, "onFailure: queueReport");
                t.printStackTrace();
                CentralisedToast.makeText(getActivity(), "Unable to make request to server, please load the page again!", CentralisedToast.LENGTH_LONG);
                getView().findViewById(R.id.reloadButton).setVisibility(VISIBLE);
            }
        });
    }

    private void loadToken() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN_KEY", null);
        int userId = sharedPreferences.getInt("USER_ID_KEY", 0);
        Log.d(TAG, "loadToken: userId: "+userId);
    }

    @Override
    public void navigate(int position) {
        EspressoCountingIdlingResource.increment();
        TenantExpandedFragment tenantExpandedFragment = new TenantExpandedFragment(tenants.get(position));
        String tag = tenantExpandedFragment.getClass().getName();
        TenantsPreviewFragment.this.getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.auditor_fragment_container, tenantExpandedFragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
