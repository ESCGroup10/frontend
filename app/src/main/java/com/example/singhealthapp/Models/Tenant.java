package com.example.singhealthapp.Models;

public class Tenant {
    private int id;
    private String company, location, institution, type, name;
    private boolean status;

    public int getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getCompany() {
        return company;
    }

    public String getInstitution() {
        return institution;
    }

    public String getType() {
        return type;
    }

    public String getName(){return name;}


    public boolean isStatus() {
        return status;
    }
}
