package com.example.conversa;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.conversa.database.UserSection;
import com.example.conversa.manager.AndroidManager;
import com.example.conversa.manager.FirebaseManager;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Settings extends AppCompatActivity {
    // Defining UI elements
    // ImageView to load the profile picture
    ImageView profilePic;
    // Text Boxes for the Username and Phone
    EditText usernameInput;
    EditText phoneInput;
    // Button to register updating the profile
    Button updateProfileBtn;
    // Progress Bar for loading
    ProgressBar progressBar;
    // Logout Button as text
    TextView logoutBtn;
    // Used to gather current user's data
    UserSection currentUserSection;
    // Launcher to change the profile picture
    ActivityResultLauncher<Intent> imagePickLauncher;
    // Selected image URI
    Uri selectedImageUri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Initializing UI elements
        profilePic = findViewById(R.id.profile_image_view);
        usernameInput = findViewById(R.id.profile_username);
        phoneInput = findViewById(R.id.profile_phone);
        updateProfileBtn = findViewById(R.id.profle_update_btn);
        progressBar = findViewById(R.id.profile_progress_bar);
        logoutBtn = findViewById(R.id.logout_btn);
        // Use of method to get the user's data
        getUserData();
        // Update the profile button
        updateProfileBtn.setOnClickListener((v -> {
            // Method that will do this
            updateProfile();
        }));
        // Logout Button Func.
        logoutBtn.setOnClickListener((v)->{
            // Delete the Firebase Cloud Messaging and log out
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        // Log the user out
                        FirebaseManager.logout();
                        // If successful, go to the splash screen
                        Intent intent = new Intent(Settings.this,SplashScreen.class);
                        // Prevent the user from going back for security
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            });
        });
        // Profile Picture Selection Func.
        profilePic.setOnClickListener((v)->{
            // Launch Image picker
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            // Go to the new screen to select a new profile pic
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });
        // Image Selection launcher Functionality
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            // If the user has selected an image to change
                            // change their profile picture
                            selectedImageUri = data.getData();
                            AndroidManager.setUserProfilePic(Settings.this,selectedImageUri,profilePic);
                        }
                    }
                }
        );
    }
    // Method to update the profile on the button press
    void updateProfile(){
        // Get the new username from the text box
        String newUsername = usernameInput.getText().toString();
        if(newUsername.isEmpty() || newUsername.length()<3){
            // Make sure the new username is valid and not empty
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        // Update the username in the Firebase
        currentUserSection.setUsername(newUsername);
        // Start the loading bar
        progressBarChecker(true);
        // Upload the new profile picture if it is new as well
        if(selectedImageUri!=null){
            // Update the profile picture and everything else
            FirebaseManager.getCurrentProfilePic().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        updateFirebaseInfo();
                    });
        }else{
            updateFirebaseInfo();
        }

    }
    // Method to update user information to Firebase
    void updateFirebaseInfo(){
        FirebaseManager.currentUserDetails().set(currentUserSection)
                .addOnCompleteListener(task -> {
                    // Remove the loading bar
                    progressBarChecker(false);
                    // If updating the user information is successful
                    if(task.isSuccessful()){
                        // Add a Toast Message saying it was successful
                        AndroidManager.showMessage(Settings.this,"Updated successfully");
                    }else{
                        AndroidManager.showMessage(Settings.this,"Updated failed");
                    }
                });
    }
    //Method to get the user data from Firebase
    void getUserData(){
        // Begin the loading bar
        progressBarChecker(true);
        // Get the profile picture URI
        FirebaseManager.getCurrentProfilePic().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri  = task.getResult();
                        AndroidManager.setUserProfilePic(Settings.this,uri,profilePic);
                    }
                });
        // Get the current user details
        FirebaseManager.currentUserDetails().get().addOnCompleteListener(task -> {
            // Remove the loading bar
            progressBarChecker(false);
            currentUserSection = task.getResult().toObject(UserSection.class);
            usernameInput.setText(currentUserSection.getUsername());
            phoneInput.setText(currentUserSection.getPhone());
        });
    }
    // Method for progress bar visibility
    void progressBarChecker(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }
}