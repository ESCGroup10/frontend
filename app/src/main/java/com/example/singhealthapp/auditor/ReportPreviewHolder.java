package com.example.singhealthapp.auditor;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.singhealthapp.R;

public class ReportPreviewHolder extends RecyclerView.ViewHolder {
    TextView reportName, reportDate, resolution;
    View view;

    public ReportPreviewHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
        reportName = view.findViewById(R.id.reportPreviewName);
        reportDate = view.findViewById(R.id.reportPreviewDate);
        resolution = view.findViewById(R.id.resolutionPreview);
    }
}
