package com.example.singhealthapp.auditor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.R;

import java.util.Collections;
import java.util.List;

public class ReportPreviewAdapter extends RecyclerView.Adapter<ReportPreviewHolder>{
    List<ReportPreview> list;
    FragmentActivity parent;
    List<Report> reports;

    public ReportPreviewAdapter(List<ReportPreview> list, List<Report> reports, FragmentActivity parent) {
        this.list = list;
        this.reports = reports;
        this.parent = parent;
    }

    @NonNull
    @Override
    public ReportPreviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_preview_card, null);

        return new ReportPreviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportPreviewHolder holder, int position) {
        holder.reportName.setText(list.get(position).getReportName());
        holder.reportDate.setText(list.get(position).getReportDate());
        holder.resolution.setText(list.get(position).getResolution_date());
        holder.id.setText("TENANT ID: " + list.get(position).getTenant_id());
        holder.view.setOnClickListener(v -> parent.getSupportFragmentManager().beginTransaction()
                .replace(parent.getSupportFragmentManager().findFragmentByTag("getReport").getId()
                        , new AuditorReportFragment(reports.get(position)), "viewReport").commit());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
