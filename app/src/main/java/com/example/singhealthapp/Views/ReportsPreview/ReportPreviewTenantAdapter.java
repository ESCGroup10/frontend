package com.example.singhealthapp.Views.ReportsPreview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.HelperClasses.TenantReportPreviewNavigateListener;
import com.example.singhealthapp.Models.Report;
import com.example.singhealthapp.Models.ReportPreview;
import com.example.singhealthapp.R;

import java.util.List;

import static com.example.singhealthapp.HelperClasses.TextAestheticsAndParsing.setHalfBoldTextViews;

public class ReportPreviewTenantAdapter extends RecyclerView.Adapter<ReportPreviewHolder> {
    List<ReportPreview> list;
    TenantReportPreviewNavigateListener parent;
    List<Report> reports;
    private String token;

    public ReportPreviewTenantAdapter(List<ReportPreview> list, List<Report> reports, TenantReportPreviewNavigateListener parent, String token) {
        this.list = list;
        this.reports = reports;
        this.parent = parent;
        this.token = token;
    }

    @NonNull
    @Override
    public ReportPreviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_report, null);

        return new ReportPreviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportPreviewHolder holder, int position) {
        Report report;
        report = reports.get(position);
        report.setTenant_display_id(position + 1);
        holder.reportName.setText(("Report " + String.valueOf(position + 1)));
        holder.reportDate.setText(("Created at: "));
        setHalfBoldTextViews(holder.reportDate, list.get(position).getReportDate());
        holder.resolution.setText("");
        holder.id.setText("");
        holder.view.setOnClickListener(v -> parent.navigateFromRecyclerView(report, token));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
