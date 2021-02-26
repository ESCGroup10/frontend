package com.example.singhealthapp.tenant;

import com.example.singhealthapp.User;

public class TenantObject extends User {

    private String company;
    private String location;
    private String institution;
    private String type;

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

    public TenantObject(String name, int id, String email, String password, String company, String location, String institution, String type) {
        super(name, id, email, password);
        this.company = company;
        this.location = location;
        this.institution = institution;
        this.type = type;
    }
}
