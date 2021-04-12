package com.example.singhealthapp.Models;

public class Case {
    private int id, report_id, tenant_id;
    private String question;
    private String unresolved_photo;
    private String unresolved_comments;
    private String resolved_photo;
    private String resolved_comments;
    private String non_compliance_type;
    private String unresolved_date;
    private String resolved_date;

    public void setRejected_comments(String rejected_comments) {
        this.rejected_comments = rejected_comments;
    }

    public String getRejected_comments() {
        return rejected_comments;
    }

    private String rejected_comments;
    private boolean is_resolved;

    public int getTenant_id() {
        return tenant_id;
    }

    public int getId() {
        return id;
    }

    public int getReport_id() {
        return report_id;
    }

    public String getQuestion() {
        return question;
    }

    public String getUnresolved_photo() {
        return unresolved_photo;
    }

    public String getUnresolved_comments() {
        return unresolved_comments;
    }

    public String getResolved_photo() {
        return resolved_photo;
    }

    public String getResolved_comments() {
        return resolved_comments;
    }

    public boolean isIs_resolved() {
        return is_resolved;
    }

    public String getNon_compliance_type() {
        return non_compliance_type;
    }

    public String getUnresolved_date() {
        return unresolved_date;
    }

    public String getResolved_date() {
        return resolved_date;
    }

    public void setResolved_photo(String resolved_photo) {
        this.resolved_photo = resolved_photo;
    }

    public void setResolved_comments(String resolved_comments) {
        this.resolved_comments = resolved_comments;
    }

    public void setResolved_date(String resolved_date) {
        this.resolved_date = resolved_date;
    }

    public void setIs_resolved(boolean is_resolved) {
        this.is_resolved = is_resolved;
    }
}
