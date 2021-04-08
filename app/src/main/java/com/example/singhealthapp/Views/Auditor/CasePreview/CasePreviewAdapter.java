package com.example.singhealthapp.Views.Auditor.CasePreview;

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
import com.example.singhealthapp.Views.Auditor.AuditorReport.AuditorReportFragment;
import com.example.singhealthapp.Views.Tenant.ExpandedCase;

import java.util.List;

public class CasePreviewAdapter extends RecyclerView.Adapter<CasePreviewHolder>{
    List<Case> cases;
    Report report;
    FragmentActivity parent;

    public CasePreviewAdapter(List<Case> cases, Report report, FragmentActivity parent) {
        this.cases = cases;
        this.report = report;
        this.parent = parent;
    }

    @NonNull
    @Override
    public CasePreviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.case_card_layout, null);
        return new CasePreviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CasePreviewHolder holder, int position) {
        if (cases.get(position).isIs_resolved()) {
            holder.is_resolved.setText("Resolved");
            holder.cardView.setCardBackgroundColor(Color.rgb(159, 221, 88));
        }
        else {
            holder.is_resolved.setText("Unresolved");
            holder.cardView.setCardBackgroundColor(Color.rgb(239, 117, 119));
        }
        holder.type.setText(cases.get(position).getNon_compliance_type());
        holder.id.setText("Case ID " + cases.get(position).getId());
        Case a = cases.get(position);
        holder.cardView.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("COMPANY_KEY", report.getCompany());
            args.putInt("REPORT_NUMBER_KEY", report.getTenant_display_id());
            args.putInt("CASE_NUMBER_KEY", a.getId());
            args.putBoolean("RESOLVED_STATUS_KEY", a.isIs_resolved());
            args.putInt("REPORT_ID_KEY", report.getId());
            args.putInt("CASE_ID_KEY", a.getId());
            ExpandedCase expandedCase = new ExpandedCase();
            expandedCase.setArguments(args);
            parent.getSupportFragmentManager().beginTransaction()
                    .replace(parent.getSupportFragmentManager().findFragmentById(R.id.auditor_fragment_container).getId()
                            , expandedCase).commit();
        });
    }

    @Override
    public int getItemCount() {
        return cases.size();
    }
}
