package com.example.singhealthapp.Views.Auditor.SearchTenant;

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

import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Tenant;
import com.example.singhealthapp.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchTenantFragment extends Fragment {
    SearchAdapter adapter;
    private ArrayList<SearchMain> tenantPreviews, getTenantPreviews;
    private ArrayList<Tenant> tenants, displayTenants;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Search Tenant");
        View view = inflater.inflate(R.layout.fragment_tenants, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        queueList(loadToken());
    }
    private void queueList(String token){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://esc10-303807.et.r.appspot.com/").addConverterFactory(GsonConverterFactory.create()).build();
        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);
        Call<List<SearchMain>> call = apiCaller.getSearchMain("Token " + token);
        call.enqueue(new Callback<List<SearchMain>>() {
            @Override
            public void onResponse(Call<List<SearchMain>> call, Response<List<SearchMain>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Unsuccessful: response code " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                System.out.println("response " + response.code());
                queueReport(token, response.body());
            }
            @Override
            public void onFailure(Call<List<SearchMain>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void queueReport(String token, List<SearchMain> tenantSearch){
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
                adapter = new SearchAdapter(tenantSearch, response.body(), getActivity(), loadToken());
                try {
                    RecyclerView view = (RecyclerView) getView().findViewById(R.id.tenantRecycler);
                    view.setLayoutManager(new LinearLayoutManager(getActivity()));
                    view.setItemAnimator(new DefaultItemAnimator());
                    view.setAdapter(adapter);
                }
                catch (Exception e) {
                    System.out.println("recycleView not set");
                }
            }
            @Override
            public void onFailure(Call<List<Tenant>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private String loadToken() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN_KEY", null);
        int userId = sharedPreferences.getInt("USER_ID_KEY", 0);
        System.out.println("User ID " + userId);
        return token;
    }
}
