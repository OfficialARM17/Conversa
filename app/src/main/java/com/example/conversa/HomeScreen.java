package com.example.conversa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeScreen extends AppCompatActivity {
    // Defining UI elements
    // Bottom Nav View which I used for design
    BottomNavigationView bottomNavigationView;
    // Buttons to select settings and search
    // on the top of the screen
    ImageButton searchBtn, settingsBtn;
    // Loading the chatFragment to use its content
    ChatFragment chatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        // Load the chatFragment
        chatFragment = new ChatFragment();
        // Assign the UI elements their IDs for manipulation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchBtn = findViewById(R.id.main_search_btn);
        settingsBtn = findViewById(R.id.settings_button);
        // Button Click to go to the Settings Screen
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen.this, Settings.class));
            }
        });
        // Button Click to go to the Search Menu
        searchBtn.setOnClickListener((v) -> {
            startActivity(new Intent(HomeScreen.this, SearchUser.class));
        });
        // bottomNav Menu but there is only one option
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_chat) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, chatFragment).commit();
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);

    }

}
