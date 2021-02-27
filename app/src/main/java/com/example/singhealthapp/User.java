package com.example.singhealthapp;

public class User {
    private String name, email, company, location, institution, type;
    private int id;

    public User(String name, String company, String email, String location, String institution, String type) {
        //this.id = id;
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

    public String getEmail() {
        return email;
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
}
