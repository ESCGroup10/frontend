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

public class ReportPreviewTenantAdapter extends RecyclerView.Adapter<ReportPreviewHolder>{
    List<ReportPreview> list;
    FragmentActivity parent;
    List<Report> reports;
    private String token;

    public ReportPreviewTenantAdapter(List<ReportPreview> list, List<Report> reports, FragmentActivity parent, String token) {
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
        Report report;
        report = reports.get(position);
        report.setTenant_display_id(position + 1);
        holder.reportName.setText("Report " + String.valueOf(position + 1));
        holder.reportDate.setText(list.get(position).getReportDate());
        holder.resolution.setText(list.get(position).getResolution_date());
        if (report.isStatus()) holder.id.setText("Completed");
        else holder.id.setText("Unresolved");
        holder.view.setOnClickListener(v -> parent.getSupportFragmentManager().beginTransaction()
                .replace(parent.getSupportFragmentManager().findFragmentByTag("getReport").getId()
                        , new AuditorReportFragment(report, token), "viewReport").commit());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
