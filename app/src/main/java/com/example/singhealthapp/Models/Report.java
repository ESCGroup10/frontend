package com.example.singhealthapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Report implements Parcelable {
    private int id, auditor_id, tenant_id;
    private Integer tenant_display_id = null;
    private float staffhygiene_score, housekeeping_score, safety_score, healthierchoice_score, foodhygiene_score;
    private String company, institution, location, outlet_type, report_notes, report_date, report_image, resolution_date;
    private boolean status;

    public Report() {
    }

    public void setResolution_date(String resolution_date) {
        this.resolution_date = resolution_date;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    protected Report(Parcel in) {
        id = in.readInt();
        auditor_id = in.readInt();
        tenant_id = in.readInt();
        if (in.readByte() == 0) {
            tenant_display_id = null;
        } else {
            tenant_display_id = in.readInt();
        }
        staffhygiene_score = in.readFloat();
        housekeeping_score = in.readFloat();
        safety_score = in.readFloat();
        healthierchoice_score = in.readFloat();
        foodhygiene_score = in.readFloat();
        company = in.readString();
        institution = in.readString();
        location = in.readString();
        outlet_type = in.readString();
        report_notes = in.readString();
        report_date = in.readString();
        report_image = in.readString();
        resolution_date = in.readString();
        status = in.readByte() != 0;
    }

    public static final Creator<Report> CREATOR = new Creator<Report>() {
        @Override
        public Report createFromParcel(Parcel in) {
            return new Report(in);
        }

        @Override
        public Report[] newArray(int size) {
            return new Report[size];
        }
    };

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
                ", resolution_date='" + resolution_date + '\'' +
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

    public String getResolution_date() {
        return resolution_date;
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

    @Override
    public int describeContents() {
        return 0;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAuditor_id(int auditor_id) {
        this.auditor_id = auditor_id;
    }

    public void setTenant_id(int tenant_id) {
        this.tenant_id = tenant_id;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setOutlet_type(String outlet_type) {
        this.outlet_type = outlet_type;
    }

    public void setReport_notes(String report_notes) {
        this.report_notes = report_notes;
    }

    public void setReport_date(String report_date) {
        this.report_date = report_date;
    }

    public void setReport_image(String report_image) {
        this.report_image = report_image;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeInt(auditor_id);
        dest.writeInt(tenant_id);
        if (tenant_display_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(tenant_display_id);
        }
        dest.writeFloat(staffhygiene_score);
        dest.writeFloat(housekeeping_score);
        dest.writeFloat(safety_score);
        dest.writeFloat(healthierchoice_score);
        dest.writeFloat(foodhygiene_score);
        dest.writeString(company);
        dest.writeString(institution);
        dest.writeString(location);
        dest.writeString(outlet_type);
        dest.writeString(report_notes);
        dest.writeString(report_date);
        dest.writeString(report_image);
        dest.writeString(resolution_date);
        dest.writeByte((byte) (status ? 1 : 0));
    }
}
