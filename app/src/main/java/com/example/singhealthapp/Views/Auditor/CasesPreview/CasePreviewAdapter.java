package com.example.singhealthapp.Views.Auditor.CasesPreview;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.Models.Report;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Models.Case;
import com.example.singhealthapp.Views.Tenant.CaseExpanded;

import java.util.List;

public class CasePreviewAdapter extends RecyclerView.Adapter<CasePreviewHolder>{
    List<Case> cases;
    Report report;
    FragmentActivity parent;
    String userType;

    public CasePreviewAdapter(List<Case> cases, Report report, FragmentActivity parent) {
        this.cases = cases;
        this.report = report;
        this.parent = parent;
    }

    @NonNull
    @Override
    public CasePreviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        loadUserType();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_case, null);
        return new CasePreviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CasePreviewHolder holder, int position) {
        if (cases.get(position).isIs_resolved()) {
            holder.is_resolved.setText("Resolved");
            holder.is_resolved.setTextColor(Color.parseColor("#62bd69"));
        }
        else {
            holder.is_resolved.setText("Unresolved");
            holder.is_resolved.setTextColor(Color.parseColor("#ff6961"));
        }
        holder.type.setText(cases.get(position).getNon_compliance_type());
        holder.id.setText("Case " + cases.get(position).getId());
        Case a = cases.get(position);
        holder.cardView.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("COMPANY_KEY", report.getCompany());
            args.putInt("REPORT_NUMBER_KEY", report.getTenant_display_id());
            args.putInt("CASE_NUMBER_KEY", a.getId());
            args.putBoolean("RESOLVED_STATUS_KEY", a.isIs_resolved());
            args.putInt("REPORT_ID_KEY", report.getId());
            args.putInt("CASE_ID_KEY", a.getId());
            CaseExpanded caseExpanded = new CaseExpanded();
            caseExpanded.setArguments(args);
            int fragment_id;
            if (userType.equals("Auditor")) {
                fragment_id = R.id.auditor_fragment_container;
            } else {
                fragment_id = R.id.fragment_container;
            }
            parent.getSupportFragmentManager().beginTransaction()
                    .replace(parent.getSupportFragmentManager().findFragmentById(fragment_id).getId()
                            , caseExpanded).commit();
        });
    }

    @Override
    public int getItemCount() {
        return cases.size();
    }

    private void loadUserType() {
        SharedPreferences sharedPreferences = parent.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        userType = sharedPreferences.getString("USER_TYPE_KEY", null);
    }
}
