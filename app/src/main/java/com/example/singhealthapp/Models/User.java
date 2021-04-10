package com.example.singhealthapp.Models;

public class User {
    private String email, password, name, company, location, institution, type;
    private int id;

    public User(String email, String password, String name, String company, String location, String institution, String type) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.company = company;
        this.location = location;
        this.institution = institution;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() { return password; }

    public String getName() {
        return name;
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
}
