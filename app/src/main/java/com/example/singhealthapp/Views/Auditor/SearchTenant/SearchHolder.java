package com.example.singhealthapp.Views.Auditor.SearchTenant;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.Models.Tenant;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Auditor.Tenants.TenantsFragment;

import java.util.List;

public class SearchHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "SearchHolder";
    TextView tenantType, tenantCompany, tenantInstitution;
    View view;
    CardView cardView;
    Button button;

    public SearchHolder(@NonNull View itemView, FragmentActivity parent, List<Tenant> tenants) {
        super(itemView);
        view = itemView;
        cardView = view.findViewById(R.id.cardView);
//        button = view.findViewById(R.id.button);
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "view onClick: called");
//                parent.getSupportFragmentManager().beginTransaction()
//                        .replace(parent.getSupportFragmentManager().findFragmentByTag("getTenant").getId()
//                                , new TenantsFragment(tenants.get(getAdapterPosition())), "viewTenant").commit();
//            }
//        });

        tenantType = view.findViewById(R.id.tenantCardType);
        tenantCompany = view.findViewById(R.id.tenantCardCompany);
        tenantInstitution = view.findViewById(R.id.tenantCardInstitution);
    }

}