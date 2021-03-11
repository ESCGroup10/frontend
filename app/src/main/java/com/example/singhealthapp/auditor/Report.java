package com.example.singhealthapp.auditor;

public class Report {
    private int id, auditor_id, tenant_id, staffhygiene_score, housekeeping_score, safety_score, healthierchoice_score, foodhygiene_score;
    private String location, outlet_type, report_notes, report_date, report_image, resolution_notes, resolution_date, resolution_image;
    private boolean status;

    public int getId() {
        return id;
    }

    public int getAuditor_id() {
        return auditor_id;
    }

    public int getTenant_id() {
        return tenant_id;
    }

    public int getStaffhygiene_score() {
        return staffhygiene_score;
    }

    public int getHousekeeping_score() {
        return housekeeping_score;
    }

    public int getSafety_score() {
        return safety_score;
    }

    public int getHealthierchoice_score() {
        return healthierchoice_score;
    }

    public int getFoodhygiene_score() {
        return foodhygiene_score;
    }

    public String getLocation() {
        return location;
    }

    public String getOutlet_type() {
        return outlet_type;
    }

    public String getReport_notes() {
        return report_notes;
    }

    public String getReport_date() {
        return report_date;
    }

    public String getReport_image() {
        return report_image;
    }

    public String getResolution_notes() {
        return resolution_notes;
    }

    public String getResolution_date() {
        return resolution_date;
    }

    public String getResolution_image() {
        return resolution_image;
    }

    public boolean isStatus() {
        return status;
    }
}
