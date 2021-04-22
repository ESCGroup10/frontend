package com.example.singhealthapp.Views.Auditor.CasesPreview;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.HelperClasses.CasePreviewNavigateListener;
import com.example.singhealthapp.Models.Report;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Models.Case;
import com.example.singhealthapp.Views.Tenant.CaseExpanded;

import java.util.List;

public class CasePreviewAdapter extends RecyclerView.Adapter<CasePreviewHolder>{
    private static final String TAG = "CasePreviewAdapter";
    List<Case> cases;
    Report report;
    CasePreviewNavigateListener parent;

    public CasePreviewAdapter(List<Case> cases, Report report, CasePreviewNavigateListener parent) {
        this.cases = cases;
        this.report = report;
        this.parent = parent;
    }

    @NonNull
    @Override
    public CasePreviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_case, null);
        return new CasePreviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CasePreviewHolder holder, int position) {
        Case thisCase = cases.get(position);
        if (thisCase.isIs_resolved()) {
            holder.is_resolved.setText("Resolved");
            holder.is_resolved.setTextColor(Color.parseColor("#62bd69"));
        }
        else {
            if (thisCase.getRejected_comments().isEmpty()) {
                if (!thisCase.getResolved_photo().isEmpty()) {
                    holder.is_resolved.setText("Pending Resolution");
                    holder.is_resolved.setTextColor(Color.parseColor("#ff6961"));
                } else {
                    holder.is_resolved.setText("Unresolved");
                    holder.is_resolved.setTextColor(Color.parseColor("#ff6961"));
                }
            }
            else {
                holder.is_resolved.setText("Rejected");
                holder.is_resolved.setTextColor(Color.BLACK);
            }
        }
        holder.type.setText(cases.get(position).getNon_compliance_type());
        holder.id.setText(("Case " + cases.get(position).getId()));
        holder.cardView.setOnClickListener(v -> {
            parent.navigateFromRecyclerView(report, thisCase);
        });
    }

    @Override
    public int getItemCount() {
        return cases.size();
    }

}
