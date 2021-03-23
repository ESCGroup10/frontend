package com.example.singhealthapp.Views.Auditor.SearchTenant;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.singhealthapp.R;

public class SearchHolder extends RecyclerView.ViewHolder {
    TextView tenantType, tenantCompany, tenantInstitution, id;
    View view;

    public SearchHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
        tenantCompany = view.findViewById(R.id.tenantCardCompany);
        tenantInstitution = view.findViewById(R.id.tenantCardInstitution);
        tenantType = view.findViewById(R.id.tenantCardType);
    }
}
