package com.example.conversa.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.conversa.database.UserSection;

public class AndroidManager {
    // Method to display a toast message
   public static void showMessage(Context context, String message){
       Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
    // Method to send a user's details as intent extras to another activity
    public static void sendUserSectionDetails(Intent intent, UserSection model){
       intent.putExtra("username",model.getUsername());
       intent.putExtra("phone",model.getPhone());
       intent.putExtra("userId",model.getUserId());
    }
    // Method to get a user's details using intent extras
    public static UserSection getUserSectionDetails(Intent intent){
        UserSection userSection = new UserSection();
        userSection.setUsername(intent.getStringExtra("username"));
        userSection.setPhone(intent.getStringExtra("phone"));
        userSection.setUserId(intent.getStringExtra("userId"));
        return userSection;
    }
    // Method to set a user's profile picture with the use of the Glide library
    public static void setUserProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
