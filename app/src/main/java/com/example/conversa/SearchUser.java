package com.example.conversa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.conversa.history.SearchUserHistory;
import com.example.conversa.database.UserSection;
import com.example.conversa.manager.FirebaseManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchUser extends AppCompatActivity {
    // Defining UI elements
    // Text box to search for a user
    EditText searchInput;
    // Button to register the search
    ImageButton searchButton;
    // Back Button to go to the Home Screen
    ImageButton backButton;
    // RecyclerView to list all possible users
    RecyclerView recyclerView;
    // Adapter to get the information
    SearchUserHistory adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        // Initializing UI elements
        searchInput = findViewById(R.id.seach_username_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);
        // Setting focus on the field
        searchInput.requestFocus();
        // Back Button Functionality
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });
        // Search Button Functionality
        searchButton.setOnClickListener(v -> {
            // Getting the user's input
            String searchTerm = searchInput.getText().toString();
            // Check to make sure the user's input is not empty
            if(searchTerm.isEmpty() || searchTerm.length()<2){
                searchInput.setError("Invalid Username");
                return;
            }
            // Return the method to gather the results
            returnSearchResults(searchTerm);
        });
    }
    // Method to return the search results
    void returnSearchResults(String searchTerm){
        // Use of Firebase Query to search for users
        Query query = FirebaseManager.returnAllUsers()
                .whereGreaterThanOrEqualTo("username",searchTerm)
                .whereLessThanOrEqualTo("username",searchTerm+'\uf8ff');
        // Creatng the options to gather information
        FirestoreRecyclerOptions<UserSection> options = new FirestoreRecyclerOptions.Builder<UserSection>()
                .setQuery(query, UserSection.class).build();
        // Creating an instance of SearchUserHistory
        adapter = new SearchUserHistory(options,getApplicationContext());
        // Updating the RecyclerView with the correct, up-to-date information
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        // Begin listening
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start listening for changes once the fragment has opened
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop listening for changes when the fragment has closed
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume listening for changes when the activity has resumed
        if(adapter!=null)
            adapter.startListening();
    }
}