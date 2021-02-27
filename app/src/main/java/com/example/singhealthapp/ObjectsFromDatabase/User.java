package com.example.singhealthapp.ObjectsFromDatabase;

import com.google.gson.annotations.SerializedName;

public class User{

    @SerializedName("id")
    private int user_id;

    private String name;

    @SerializedName("company")
    private String company_name;

    private String email;

    private String location;

    private String institution;

    private String type;

    public int getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public String getCompany_name() {
        return company_name;
    }

    public String getEmail() {
        return email;
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
