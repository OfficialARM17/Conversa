package com.example.conversa.database;

import com.google.firebase.Timestamp;

public class ChatMessageSection {
    // Defining a chat message's information
    // String which represents the content of the message
    private String message;
    // String for who sent the message
    private String senderId;
    // Time of when the message is sent
    private Timestamp timestamp;
    public ChatMessageSection() {
    }
    // Constructor to initialize the chat message section with provided details
    public ChatMessageSection(String message, String senderId, Timestamp timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }
    // Getter methods for each attribute
    public String getMessage() {
        return message;
    }
    public String getSenderId() {
        return senderId;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
    // Setter methods for each attribute
    public void setMessage(String message) {
        this.message = message;
    }
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
