package com.example.conversa.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conversa.R;
import com.example.conversa.database.ChatMessageSection;
import com.example.conversa.manager.FirebaseManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class UserChatHistory extends FirestoreRecyclerAdapter<ChatMessageSection, UserChatHistory.userChatHistoryContentViewHolder> {

    Context context;
    // Constructor
    public UserChatHistory(@NonNull FirestoreRecyclerOptions<ChatMessageSection> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull userChatHistoryContentViewHolder holder, int position, @NonNull ChatMessageSection model) {
       // Checking if the sender ID is the same as the current user's ID
        if(model.getSenderId().equals(FirebaseManager.getCurrentUserId())){
            // If the current user sent a message, hide the chat on the left hand side
            holder.userSentChatLayout.setVisibility(View.GONE);
            holder.otherUserrightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextview.setText(model.getMessage());
       }else{
            //If another user sent the message, hide the chat on the right hand side
            holder.otherUserrightChatLayout.setVisibility(View.GONE);
            holder.userSentChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextview.setText(model.getMessage());
       }
    }

    @NonNull
    @Override
    public userChatHistoryContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the layout
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_history_content,parent,false);
        return new userChatHistoryContentViewHolder(view);
    }
    // Class to hold all of the content inside of the Chat Screen
    class userChatHistoryContentViewHolder extends RecyclerView.ViewHolder{
        // Layouts for each side of the user chat
        LinearLayout userSentChatLayout,otherUserrightChatLayout;
        // Text that will appear on each side
        TextView leftChatTextview,rightChatTextview;

        public userChatHistoryContentViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initializing the views
            userSentChatLayout = itemView.findViewById(R.id.left_chat_layout);
            otherUserrightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
        }
    }
}
