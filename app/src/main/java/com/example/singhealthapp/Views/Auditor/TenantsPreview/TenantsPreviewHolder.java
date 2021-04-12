package com.example.singhealthapp.Views.Auditor.TenantsPreview;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.Models.Tenant;
import com.example.singhealthapp.R;

import java.util.List;

public class TenantsPreviewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "TenantsPreviewHolder";
    TextView tenantType, tenantCompany, tenantInstitution;
    View view;
    CardView cardView;
    Button button;

    public TenantsPreviewHolder(@NonNull View itemView, FragmentActivity parent, List<Tenant> tenants) {
        super(itemView);
        view = itemView;
        cardView = view.findViewById(R.id.cardView);
//        cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "view onClick: called");
//                parent.getSupportFragmentManager().beginTransaction()
//                        .replace(parent.getSupportFragmentManager().findFragmentByTag("getTenant").getId()
//                                , new TenantExpandedFragment(tenants.get(getAdapterPosition())), "viewTenant").commit();
//            }
//        });


        tenantType = view.findViewById(R.id.tenantCardType);
        tenantCompany = view.findViewById(R.id.tenantCardCompany);
        tenantInstitution = view.findViewById(R.id.tenantCardInstitution);
    }

}