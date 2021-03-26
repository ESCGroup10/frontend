package com.example.singhealthapp.Views.Auditor.Tenants;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.singhealthapp.Models.Tenant;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Auditor.Checklists.SafetyChecklistFragment;

public class TenantsFragment extends Fragment {
    private static final String TAG = "TenantsFragment";
    Tenant tenant;
    View view;
    TextView company, institution, type, location, name;
    Button button;

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

        return view;
    }


}