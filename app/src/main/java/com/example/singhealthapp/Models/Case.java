package com.example.singhealthapp.Models;

public class Case {
    private int id, report_id, tenant_id;
    private String question, unresolved_photo, unresolved_comments, resolved_photo, resolved_comments, non_compliance_type,
            unresolved_date, resolved_date, rejected_comments;
    private boolean is_resolved;

    public void setRejected_comments(String rejected_comments) {
        this.rejected_comments = rejected_comments;
    }

    public void setReport_id(int report_id) {
        this.report_id = report_id;
    }

    public void setTenant_id(int tenant_id) {
        this.tenant_id = tenant_id;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setUnresolved_photo(String unresolved_photo) {
        this.unresolved_photo = unresolved_photo;
    }

    public void setUnresolved_comments(String unresolved_comments) {
        this.unresolved_comments = unresolved_comments;
    }

    public void setNon_compliance_type(String non_compliance_type) {
        this.non_compliance_type = non_compliance_type;
    }

    public void setUnresolved_date(String unresolved_date) {
        this.unresolved_date = unresolved_date;
    }

    public String getRejected_comments() {
        return rejected_comments;
    }

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
