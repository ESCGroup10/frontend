package com.example.singhealthapp.Models;

public class Report {
    private int id, auditor_id, tenant_id;
    private Integer tenant_display_id = null;
    private float staffhygiene_score, housekeeping_score, safety_score, healthierchoice_score, foodhygiene_score;
    private String company, institution, location, outlet_type, report_notes, report_date, report_image, resolution_notes, resolution_date, resolution_image;
    private boolean status;

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Report{" +
                "auditor_id=" + auditor_id +
                ", tenant_id=" + tenant_id +
                ", tenant_display_id=" + tenant_display_id +
                ", staffhygiene_score=" + staffhygiene_score +
                ", housekeeping_score=" + housekeeping_score +
                ", safety_score=" + safety_score +
                ", healthierchoice_score=" + healthierchoice_score +
                ", foodhygiene_score=" + foodhygiene_score +
                ", company='" + company + '\'' +
                ", institution='" + institution + '\'' +
                ", location='" + location + '\'' +
                ", outlet_type='" + outlet_type + '\'' +
                ", report_notes='" + report_notes + '\'' +
                ", report_date='" + report_date + '\'' +
                ", report_image='" + report_image + '\'' +
                ", resolution_notes='" + resolution_notes + '\'' +
                ", resolution_date='" + resolution_date + '\'' +
                ", resolution_image='" + resolution_image + '\'' +
                ", status=" + status +
                '}';
    }

    public String getInstitution() {
        return institution;
    }

    public int getAuditor_id() {
        return auditor_id;
    }

    public String getCompany() {
        return company;
    }

    public int getTenant_id() {
        return tenant_id;
    }

    public float getStaffhygiene_score() {
        return staffhygiene_score;
    }

    public void setStaffhygiene_score(float staffhygiene_score) {
        this.staffhygiene_score = staffhygiene_score;
    }

    public float getHousekeeping_score() {
        return housekeeping_score;
    }

    public void setHousekeeping_score(float housekeeping_score) {
        this.housekeeping_score = housekeeping_score;
    }

    public float getSafety_score() {
        return safety_score;
    }

    public void setSafety_score(float safety_score) {
        this.safety_score = safety_score;
    }

    public float getHealthierchoice_score() {
        return healthierchoice_score;
    }

    public void setHealthierchoice_score(float healthierchoice_score) {
        this.healthierchoice_score = healthierchoice_score;
    }

    public float getFoodhygiene_score() {
        return foodhygiene_score;
    }

    public void setFoodhygiene_score(float foodhygiene_score) {
        this.foodhygiene_score = foodhygiene_score;
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

    public Integer getTenant_display_id() {
        return tenant_display_id;
    }

    public void setTenant_display_id(Integer tenant_display_id) {
        this.tenant_display_id = tenant_display_id;
    }
}
