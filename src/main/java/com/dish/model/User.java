package com.dish.model;

public class User {
    private int id;
    private String username;
    private String password; // In a real app, this would be a hashed password, but since it's for university project, let's just keep it simple

    // Constructor for creating a NEW user before saving to DB (ID is not known yet)
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}