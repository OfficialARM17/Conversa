package com.example.conversa.manager;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseManager {
    // Method to get the current user's ID
    public static String getCurrentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }
    // Method to check if a user is logged in
    public static boolean isTheUserLoggedIn(){
        if(getCurrentUserId()!=null){
            return true;
        }
        return false;
    }
    // A method which uses DocumentReference to get the specific details for one user
    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(getCurrentUserId());
    }
    // A method which uses CollectionReference to get a list of all users in the application
    public static CollectionReference returnAllUsers(){
        return FirebaseFirestore.getInstance().collection("users");
    }
    // Method to get the specific details for a chatroom a user is looking for
    // The use of ID as a unique identifier is essential to achieve this
    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }
    // Method to return a list of all messages inside of a chatroom
    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }
    // Method to create a chatroom ID from two users and their IDs
    public static String createChatroomId(String firstUserID, String secondUserID){
        if(firstUserID.hashCode()<secondUserID.hashCode()){
            return firstUserID+"_"+secondUserID;
        }else{
            return secondUserID+"_"+firstUserID;
        }
    }
    // Method to return the collection reference of every chatroom
    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }
    // Method to get the DocumentReference for another user inside of a chatroom
    public static DocumentReference getOtherUserDetailsFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseManager.getCurrentUserId())){
            return returnAllUsers().document(userIds.get(1));
        }else{
            return returnAllUsers().document(userIds.get(0));
        }
    }
    // Method to convert a Timestamp into a String
    public static String convertTimestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }
    // A method to log a user out using FirebaseAuth
    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }
    // Method to get the current profile picture of a user
    public static StorageReference getCurrentProfilePic(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseManager.getCurrentUserId());
    }
    // Method to get the profile picture of another user in the system
    public static StorageReference getOtherProfilePic(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }
}