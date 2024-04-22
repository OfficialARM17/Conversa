package com.example.conversa;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.conversa.history.RecentChatHistory;
import com.example.conversa.database.ChatroomSection;
import com.example.conversa.manager.FirebaseManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ChatFragment extends Fragment {
    // Defining UI elements
    // RecyclerView which contains the list of recent chats
    RecyclerView recyclerView;
    // Access to the recent chat history
    RecentChatHistory recentChatHistory;


    public ChatFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Linking UI elements
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        setupRecyclerView();

        return view;
    }
    // Method to load up the recent chats onto the screen
    void setupRecyclerView(){
        // Making a query to load all chatrooms which match the user
        Query query = FirebaseManager.allChatroomCollectionReference()
                .whereArrayContains("userIds", FirebaseManager.getCurrentUserId())
                .orderBy("lastMessageTimestamp",Query.Direction.DESCENDING);
        // Creating
        FirestoreRecyclerOptions<ChatroomSection> options = new FirestoreRecyclerOptions.Builder<ChatroomSection>()
                .setQuery(query, ChatroomSection.class).build();
        // Creating an instance of RecentChatHistory to gather the appropriate information
        recentChatHistory = new RecentChatHistory(options,getContext());
        // Setting up the recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recentChatHistory);
        // Check for changes in the Adapter
        recentChatHistory.startListening();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Start listening for changes once the fragment has opened
        if(recentChatHistory !=null)
            recentChatHistory.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Stop listening for changes when the fragment has closed
        if(recentChatHistory !=null)
            recentChatHistory.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tell the adapter when the fragment is resumed to update the information
        // if needed
        if(recentChatHistory !=null)
            recentChatHistory.notifyDataSetChanged();
    }
}