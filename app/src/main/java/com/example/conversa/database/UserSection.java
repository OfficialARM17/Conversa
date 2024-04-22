package com.example.conversa.database;

import com.google.firebase.Timestamp;

public class UserSection {
    // Defining user details
    // A User's phone number
    private String phone;
    // A user's username
    private String username;
    // Timestamp for when a user was created
    private Timestamp createdTimestamp;
    // A User's ID
    private String userId;

    public UserSection() {
    }
    // Constructor to initialize the user section with provided details
    public UserSection(String phone, String username, Timestamp createdTimestamp, String userId) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }
    // Getter methods for each attribute
    public String getPhone() {
        return phone;
    }
    public String getUsername() {
        return username;
    }
    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    // Setter methods for each attribute

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

}
