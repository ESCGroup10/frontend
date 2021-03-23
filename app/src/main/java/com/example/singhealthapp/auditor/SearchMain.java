package com.example.singhealthapp.auditor;

public class SearchMain {
    private String company, location, institution, type;
    private int id;
    private boolean status;

    public String getTenantName() {
        return "Tenant: " + getId();
    }

    public String getCompany() {
        return company;
    }

    public String getLocation() {
        return location;
    }

    public String getInstitution() {
        return institution;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public boolean isStatus() {
        return status;
    }

}

