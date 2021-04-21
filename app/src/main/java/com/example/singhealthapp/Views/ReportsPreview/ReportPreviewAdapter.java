package com.example.singhealthapp.Views.ReportsPreview;

import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.HelperClasses.AuditorReportPreviewNavigateListener;
import com.example.singhealthapp.Models.Report;
import com.example.singhealthapp.Models.ReportPreview;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Auditor.ReportSummary.ReportSummaryFragment;

import java.util.List;

public class ReportPreviewAdapter extends RecyclerView.Adapter<ReportPreviewHolder>{
    List<ReportPreview> list;
    AuditorReportPreviewNavigateListener parent;
    List<Report> reports;
    private String token;

    public ReportPreviewAdapter(List<ReportPreview> list, List<Report> reports, AuditorReportPreviewNavigateListener parent, String token) {
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

    private void setHalfBoldTextViews(TextView mytextview, String textToAdd) {
        String originalText = mytextview.getText().toString();
        if(Build.VERSION.SDK_INT < 24) {
            String sourceString = "<b>" + originalText + "</b> " + textToAdd;
            mytextview.setText(Html.fromHtml(sourceString));
        } else {
            int INT_END = originalText.length();
            SpannableStringBuilder str = new SpannableStringBuilder(originalText + textToAdd);
            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, INT_END, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mytextview.setText(str);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ReportPreviewHolder holder, int position) {
        Report report = reports.get(position);
        report.setTenant_display_id(position + 1);
        holder.reportName.setText(list.get(position).getReportName());
        holder.reportDate.setText(("Created on: "));
        setHalfBoldTextViews(holder.reportDate, list.get(position).getReportDate());
        if (!list.get(position).getResolution_date().equals("NOT RESOLVED")) {
            holder.resolution.setText(("Resolved on: "+list.get(position).getResolution_date()));
        }
        holder.resolution.setText(("No resolution record"));
        holder.id.setText("");
        System.out.println("tenant display id: "+report.getTenant_display_id());
        holder.view.setOnClickListener(v -> parent.navigateFromRecyclerView(report, token));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
