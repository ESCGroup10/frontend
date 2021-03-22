package com.example.singhealthapp;

import com.google.gson.annotations.SerializedName;

public class Tenant {
    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("company")
    private String company;

    @SerializedName("location")
    private String location;

    @SerializedName("institution")
    private String institution;

    @SerializedName("type")
    private String type;

    private int id;

    public Tenant(String name, String company, String email, String location, String institution, String type) {
        this.name = name;
        this.company = company;
        this.email = email;
        this.location = location;
        this.institution = institution;
        this.type = type;
    }

    public String getName() {
        return name;
    }
    public void setName(String name){this.name=name;}

    public String getEmail() {
        return email;
    }
    public void setEmail(String email){this.email=email;}


    public String getCompany() {
        return company;
    }
    public void setCompany(String company){this.company=company;}


    public String getLocation() {
        return location;
    }
    public void setLocation(String location){this.location=location;}


    public String getInstitution() {
        return institution;
    }
    public void setInstitution(String institution){this.institution=institution;}


    public String getType() {
        return type;
    }
    public void setType(String type){this.type=type;}


    public int getId() {
        return id;
    }
}