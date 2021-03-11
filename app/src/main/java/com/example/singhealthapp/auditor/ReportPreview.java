package com.example.singhealthapp.auditor;

public class ReportPreview {
    private String resolution_date, report_date;
    private int id, tenant_id;
    private boolean status;

    public String getReportName() {
        return "Report " + String.valueOf(getId());
    }

    public String getReportDate() {
        return getReport_date().substring(0, 10) + " " + getReport_date().substring(11, 19);
    }

    public int getId() {
        return id;
    }

    public int getTenant_id() {
        return tenant_id;
    }

    public boolean isStatus() {
        return status;
    }

    public String getResolution_date() {
        if (isStatus()) {
            return "Resolution Date: " + resolution_date;
        }
        return "NOT RESOLVED";
    }

    public String getReport_date() {
        return report_date;
    }
}
