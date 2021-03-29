package com.example.singhealthapp.Views.Auditor.Tenants;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Tenant;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Auditor.Checklists.SafetyChecklistFragment;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TenantsFragment extends Fragment {
    private static final String TAG = "TenantsFragment";
    Tenant tenant;
    View view;
    TextView company, institution, type, location, name;
    Button button, deleteButton;

    private static final String TENANT_TYPE_KEY = "tenant_type_key";

    public TenantsFragment(Tenant tenant) {
        this.tenant = tenant;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(tenant.getCompany());

        view = inflater.inflate(R.layout.fragment_tenant_expanded, container, false);

        location = view.findViewById(R.id.tenantLocation);
        location.setText("LOCATION: " + tenant.getLocation());

        institution = view.findViewById(R.id.tenantInstitution);
        institution.setText("INSTITUTION: " + tenant.getInstitution());

        type = view.findViewById(R.id.tenantType);
        type.setText("TYPE: " + tenant.getType());

        name = view.findViewById(R.id.tenantName);
        name.setText("OWNER NAME: " + tenant.getName());

        button = view.findViewById(R.id.startSafetyChecklistButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: called");
                Bundle bundle = new Bundle();
                bundle.putString(TENANT_TYPE_KEY, tenant.getType());
                SafetyChecklistFragment safetyChecklistFragment = new SafetyChecklistFragment();
                safetyChecklistFragment.setArguments(bundle);
                TenantsFragment.this.getParentFragmentManager().beginTransaction().replace(R.id.auditor_fragment_container, safetyChecklistFragment, "safetyFragment").commit();
            }
        });

        deleteButton = view.findViewById(R.id.deleteTenantButton);
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete this tenant?")
                    .setMessage("You can't undo this operation.")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteTenant(tenant.getId()))
                    .create().show();
        });

        return view;
    }

    private void deleteTenant(int id){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://esc10-303807.et.r.appspot.com/").addConverterFactory(GsonConverterFactory.create()).build();
        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);
        Call<Void> call = apiCaller.deleteUser("Token " + sharedPreferences.getString("TOKEN_KEY", null), id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 204) {
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

}