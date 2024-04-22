package com.example.conversa.database;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatroomSection {
    // Defining chatroom details
    // Chatroom ID
    String chatroomId;
    // List of user Ids used in the chatroom
    List<String> userIds;
    // ID of the user who last sent a message
    String lastMessageSenderId;
    // String content of the last message
    String lastMessage;
    // Time the last message was sent
    Timestamp lastMessageTimestamp;

    public ChatroomSection() {
    }
    // Constructor to initialize the chatroom section with provided details
    public ChatroomSection(String chatroomId, List<String> userIds, Timestamp lastMessageTimestamp, String lastMessageSenderId) {
        // Setting the values
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
    }

    // Getter methods for each attribute

    public String getChatroomId() {
        return chatroomId;
    }
    public List<String> getUserIds() {
        return userIds;
    }
    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }
    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }
    public String getLastMessage() {
        return lastMessage;
    }

    // Setter methods for each attribute

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }
    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }
    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }



}
