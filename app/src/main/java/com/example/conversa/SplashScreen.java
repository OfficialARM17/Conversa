package com.example.conversa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.conversa.login.LoginFirstPage;
import com.example.conversa.manager.FirebaseManager;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        // Delay to navigation to check if the user is logged in or not
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check whether the user is logged in or not using the Firebase
                // method
                if(FirebaseManager.isTheUserLoggedIn()){
                    // If so, go to the Home Screen and they are signed in
                    startActivity(new Intent(SplashScreen.this, HomeScreen.class));
                }else{
                    // If not, they need to log in or register by going to the first log in screen
                    startActivity(new Intent(SplashScreen.this, LoginFirstPage.class));
                }
                // Finish
                finish();
                }
                },
                1000); // Splash Screen for 1 second
    }
}
