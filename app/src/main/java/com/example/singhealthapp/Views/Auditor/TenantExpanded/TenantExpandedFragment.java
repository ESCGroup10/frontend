package com.example.singhealthapp.Views.Auditor.TenantExpanded;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.singhealthapp.HelperClasses.CustomFragment;
import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.HelperClasses.Ping;
import com.example.singhealthapp.Models.Case;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Report;
import com.example.singhealthapp.Models.Tenant;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Auditor.Checklists.SafetyChecklistFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TenantExpandedFragment extends CustomFragment {
    private static final String TAG = "TenantExpandedFragment";
    Tenant tenant;
    View view;
    TextView company, institution, type, location, name;
    Button button, deleteButton;
    int deleteStatus = 0;
    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://esc10-303807.et.r.appspot.com/").addConverterFactory(GsonConverterFactory.create()).build();
    DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);

    public TenantExpandedFragment(Tenant tenant) {
        this.tenant = tenant;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            EspressoCountingIdlingResource.decrement();
        }
        getActivity().setTitle("Tenant " + tenant.getId());

        view = inflater.inflate(R.layout.f_tenant_expanded, container, false);

        location = view.findViewById(R.id.tenantLocation);
        location.setText(tenant.getLocation());

        institution = view.findViewById(R.id.tenantInstitution);
        institution.setText(tenant.getInstitution());

        type = view.findViewById(R.id.tenantType);
        type.setText(tenant.getType());

        name = view.findViewById(R.id.tenantName);
        name.setText(tenant.getName());

        company = view.findViewById(R.id.tenantCompany);
        company.setText(tenant.getCompany());

        button = view.findViewById(R.id.startSafetyChecklistButton);
        button.setOnClickListener(v -> {
            Log.d(TAG, "onClick: called");
            Bundle bundle = new Bundle();
            bundle.putString("TENANT_TYPE_KEY", tenant.getType());
            bundle.putInt("ID_KEY", tenant.getId());
            bundle.putString("COMPANY_KEY", tenant.getCompany());
            bundle.putString("LOCATION_KEY", tenant.getLocation());
            bundle.putString("INSTITUTION_KEY", tenant.getInstitution());
            SafetyChecklistFragment safetyChecklistFragment = new SafetyChecklistFragment();
            safetyChecklistFragment.setArguments(bundle);

            EspressoCountingIdlingResource.increment();
            TenantExpandedFragment.this.getParentFragmentManager().beginTransaction()
                    .replace(R.id.auditor_fragment_container, safetyChecklistFragment, safetyChecklistFragment.getClass().getName())
                    .addToBackStack(null)
                    .commit();
        });

        deleteButton = view.findViewById(R.id.deleteTenantButton);
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete this tenant?")
                    .setMessage("All of the related reports and cases will be deleted.\nYou can't undo this operation.")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteTenant(tenant.getId()))
                    .create().show();
        });

        return view;
    }

    private void deleteTenant(int id){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN_KEY", null);
        Object lock = new Object();
        deleteStatus = 0;
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://esc10-303807.et.r.appspot.com/").addConverterFactory(GsonConverterFactory.create()).build();
        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);
        Call<Void> call = apiCaller.deleteUser("Token " + token, id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 204) {
                    Call<List<Report>> reportCall = apiCaller.getScoresById("Token " + token, id);
                    reportCall.enqueue(new Callback<List<Report>>(){
                        @Override
                        public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(getContext(), "Unsuccessful: response code " + response.code(), Toast.LENGTH_LONG).show();
                                requireActivity().onBackPressed();
                                return ;
                            }
                            if (response.body() == null || response.body().isEmpty()) return;
                            for (Report r: response.body()) {
                                deleteReport(r.getId(), token);
                            }
                        }
                        @Override
                        public void onFailure(Call<List<Report>> call, Throwable t) {
                        }
                    });
                    Toast.makeText(getActivity(), "Tenant \"" + tenant.getCompany() + "\" deleted", Toast.LENGTH_LONG).show();
                    requireActivity().onBackPressed();
                }
                else Toast.makeText(getActivity(), "Deletion failed.\nPlease try again.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getActivity(), "Deletion failed.\n" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteReport(int id, String token){
        Call<Void> call = apiCaller.deleteReport("Token " + token, id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 204) {
                    Call<List<Case>> caseCall = apiCaller.getCasesById("Token " + token, id, null);
                    caseCall.enqueue(new Callback<List<Case>>(){
                        @Override
                        public void onResponse(Call<List<Case>> call, Response<List<Case>> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(getContext(), "Unsuccessful: response code " + response.code(), Toast.LENGTH_LONG).show();
                                requireActivity().onBackPressed();
                                return ;
                            }
                            if (response.body() == null || response.body().isEmpty()) return;
                            for (Case c: response.body()) {
                                deleteCase(c.getId(), token);
                            }
                        }
                        @Override
                        public void onFailure(Call<List<Case>> call, Throwable t) {
                        }
                    });
                    for (Integer i : new int[]{0,1}){
                        caseCall = apiCaller.getCasesById("Token " + token, id, i);
                        caseCall.enqueue(new Callback<List<Case>>(){
                            @Override
                            public void onResponse(Call<List<Case>> call, Response<List<Case>> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Unsuccessful: response code " + response.code(), Toast.LENGTH_LONG).show();
                                    requireActivity().onBackPressed();
                                    return ;
                                }
                                if (response.body() == null || response.body().isEmpty()) return;
                                for (Case c: response.body()) {
                                    deleteCase(c.getId(), token);
                                }
                            }
                            @Override
                            public void onFailure(Call<List<Case>> call, Throwable t) {
                            }
                        });
                    }
                }
                else Toast.makeText(getActivity(), "Deletion failed.\nPlease try again.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getActivity(), "Deletion failed.\n" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteCase(int id, String token){
        Call<Void> call = apiCaller.deleteCase("Token " + token, id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 204) {
                    //Toast.makeText(getActivity(), "Tenant \"" + tenant.getCompany() + "\" deleted", Toast.LENGTH_LONG).show();
                }
                else Toast.makeText(getActivity(), "Deletion failed.\nPlease try again.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getActivity(), "Deletion failed.\n" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}