package com.example.singhealthapp.Views.Auditor.Reports;

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
        holder.reportName.setText(list.get(position).getReportName());
//        holder.reportDate.setText(list.get(position).getReportDate());
        holder.reportDate.setText("Created at: ");
        setHalfBoldTextViews(holder.reportDate, list.get(position).getReportDate());
        if (!list.get(position).getResolution_date().equals("NOT RESOLVED")) {
            holder.resolution.setText("Resolved at: "+list.get(position).getResolution_date());
        }
//        holder.resolution.setText(list.get(position).getResolution_date());
//        holder.id.setText("TENANT ID: " + list.get(position).getTenant_id());
        holder.resolution.setText("");
        holder.id.setText("");
        holder.view.setOnClickListener(v -> parent.getSupportFragmentManager().beginTransaction()
                .replace(parent.getSupportFragmentManager().findFragmentByTag("getReport").getId()
                        , new AuditorReportFragment(reports.get(position), token), "viewReport").commit());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
