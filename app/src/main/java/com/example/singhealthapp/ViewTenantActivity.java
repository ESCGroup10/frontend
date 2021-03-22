package com.example.singhealthapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.auditor.Adapters.RecyclerViewTenantAdapter;
import com.example.singhealthapp.auditor.ApiInterfaceTenant;
import com.example.singhealthapp.auditor.ApiTenant;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewTenantActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private RecyclerViewTenantAdapter tenantAdapter;
    private List<Tenant> tenantInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewtenant);
        tenantInfoList = new ArrayList<>();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        tenantAdapter = new RecyclerViewTenantAdapter(getApplicationContext(),tenantInfoList);
        recyclerView.setAdapter(tenantAdapter);

        ApiInterfaceTenant apiService = ApiTenant.getTenant().create(ApiInterfaceTenant.class);
        Call<List<Tenant>> call = apiService.getTenants();

        call.enqueue(new Callback<List<Tenant>>() {
            @Override
            public void onResponse(Call<List<Tenant>> call, Response<List<Tenant>> response) {
                tenantInfoList = response.body();
                Log.d("TAG","Response = "+tenantInfoList);
                tenantAdapter.setTenantInfoList(tenantInfoList);
            }

            @Override
            public void onFailure(Call<List<Tenant>> call, Throwable t) {
                Log.d("TAG","Response = "+t.toString());
            }
        });
    }
}
