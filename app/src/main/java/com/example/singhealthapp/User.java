package com.example.singhealthapp;

public class User {
    private String name;
    private int id;
    private String email;
    private String password;

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public User(String name, int id, String email, String password) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.password = password;
    }
}
