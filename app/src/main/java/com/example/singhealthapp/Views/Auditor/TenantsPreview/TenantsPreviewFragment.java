package com.example.singhealthapp.Views.Auditor.TenantsPreview;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.HelperClasses.CustomFragment;
import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.HelperClasses.Ping;
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

public class TenantsPreviewFragment extends CustomFragment implements TenantsPreviewAdapter.NavFromTenantSelection {

    TenantsPreviewAdapter adapter;
    private ArrayList<Tenant> tenantPreviews, getTenantPreviews;
    private List<Tenant> tenants, displayTenants;
    private String token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loadToken();
        getActivity().setTitle("Search Tenant");
        View view = inflater.inflate(R.layout.f_tenants_all, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        EspressoCountingIdlingResource.decrement();
        queueList(token);
    }

    private void queueList(String token){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://esc10-303807.et.r.appspot.com/").addConverterFactory(GsonConverterFactory.create()).build();
        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);
        Call<List<Tenant>> call = apiCaller.getTenant("Token " + token);
        call.enqueue(new Callback<List<Tenant>>() {
            @Override
            public void onResponse(Call<List<Tenant>> call, Response<List<Tenant>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Unsuccessful: response code " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                System.out.println("response " + response.code());
                EspressoCountingIdlingResource.increment();
                queueReport(token, response.body());
            }
            @Override
            public void onFailure(Call<List<Tenant>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private synchronized void queueReport(String token, List<Tenant> tenantSearch){
        while (token == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://esc10-303807.et.r.appspot.com").addConverterFactory(GsonConverterFactory.create()).build();
        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);
        Call<List<Tenant>> call = apiCaller.getTenant("Token " + token);
        call.enqueue(new Callback<List<Tenant>>() {
            @Override
            public void onResponse(Call<List<Tenant>> call, Response<List<Tenant>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Unsuccessful: response code " + response.code(), Toast.LENGTH_LONG).show();
                    return ;
                }
                System.out.println(response.body().get(0).getId());
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
                EspressoCountingIdlingResource.decrement();
            }
            @Override
            public void onFailure(Call<List<Tenant>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadToken() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN_KEY", null);
        int userId = sharedPreferences.getInt("USER_ID_KEY", 0);
        System.out.println("User ID " + userId);
    }

    @Override
    public void navigate(int position) {
        EspressoCountingIdlingResource.increment();
        TenantExpandedFragment tenantExpandedFragment = new TenantExpandedFragment(tenants.get(position));
        TenantsPreviewFragment.this.getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.auditor_fragment_container, tenantExpandedFragment, tenantExpandedFragment.getClass().getName())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onBackPressed() {
        if (getParentFragmentManager().getBackStackEntryCount() == 0) {
            return false;
        } else {
            return super.onBackPressed();
        }
    }
}
