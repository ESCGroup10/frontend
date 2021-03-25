package com.example.singhealthapp.Views.Auditor.Tenants;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.singhealthapp.Models.Tenant;
import com.example.singhealthapp.R;

public class TenantsFragment extends Fragment {
    Tenant tenant;
    View view;
    TextView company, institution, type, location, name;

    public TenantsFragment(Tenant tenant) {
        this.tenant = tenant;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Report " + tenant.getId());
        view = inflater.inflate(R.layout.fragment_tenant_expanded, container, false);

        company = view.findViewById(R.id.tenantCompany);
        company.setText("COMPANY: " + tenant.getCompany());

        location = view.findViewById(R.id.tenantLocation);
        location.setText("LOCATION: " + tenant.getLocation());

        institution = view.findViewById(R.id.tenantInstitution);
        institution.setText("INSTITUTION: " + tenant.getInstitution());

        type = view.findViewById(R.id.tenantType);
        type.setText("TYPE: " + tenant.getType());

        name = view.findViewById(R.id.tenantName);
        name.setText("OWNER NAME: " + tenant.getName());

        return view;
    }
}