package com.example.conversa.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.conversa.HomeScreen;
import com.example.conversa.R;
import com.example.conversa.database.UserSection;
import com.example.conversa.manager.FirebaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginThirdPage extends AppCompatActivity {
    // Declaring UI elements
    EditText usernameInput;
    Button enterConversaBtn;
    ProgressBar progressBar;
    String phoneNumber;
    UserSection userSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_third_page);
        // Linking UI elements to corresponding elements on the XML file
        usernameInput = findViewById(R.id.login_username);
        enterConversaBtn = findViewById(R.id.enter_conversa);
        progressBar =findViewById(R.id.login_progress_bar);
        // Use Intent Extras to get the phone number value
        phoneNumber = getIntent().getExtras().getString("phone");
        getUsername();
        enterConversaBtn.setOnClickListener((v -> {
            setUsername();
        }));


    }
    // Setting a user's name for the application
    void setUsername(){
        // Get the value from the edit text to assign as the username
        String username = usernameInput.getText().toString();
        // Checking if the username meets the criteria
        if(username.isEmpty() || username.length()<3){
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        progressBarChecker(true);
        // Check if the user is a new user or a current user
        if(userSection !=null){
            // If current user
            userSection.setUsername(username);
        }else{
            // Makes a new instance of UserSection to make their account
            userSection = new UserSection(phoneNumber,username, Timestamp.now(), FirebaseManager.getCurrentUserId());
        }
        // If this completes fine, go to the home screen of Conversa
        FirebaseManager.currentUserDetails().set(userSection).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBarChecker(false);
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginThirdPage.this, HomeScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity(intent);
                }
            }
        });
    }
    // If the user has already signed up, their name will appear in the text box
    void getUsername(){
        progressBarChecker(true);
        // Check Firebase to get the user's details
        FirebaseManager.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressBarChecker(false);
                if(task.isSuccessful()){
                    userSection = task.getResult().toObject(UserSection.class);
                    if(userSection !=null){
                        usernameInput.setText(userSection.getUsername());
                    }
                }
            }
        });
    }
    // Method to see when the progress loading circle should be on or off
    void progressBarChecker(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            enterConversaBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            enterConversaBtn.setVisibility(View.VISIBLE);
        }
    }
}