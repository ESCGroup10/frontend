package com.example.singhealthapp.Views.Auditor.Reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.Models.Report;
import com.example.singhealthapp.Models.ReportPreview;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Auditor.AuditorReport.AuditorReportFragment;

import java.util.List;

public class ReportPreviewAdapter extends RecyclerView.Adapter<ReportPreviewHolder>{
    List<ReportPreview> list;
    FragmentActivity parent;
    List<Report> reports;
    private String token;

    public ReportPreviewAdapter(List<ReportPreview> list, List<Report> reports, FragmentActivity parent, String token) {
        this.list = list;
        this.reports = reports;
        this.parent = parent;
        this.token = token;
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
                        , new AuditorReportFragment(reports.get(position), token), "viewReport").commit());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}