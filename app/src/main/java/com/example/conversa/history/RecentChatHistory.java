package com.example.conversa.history;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conversa.Chat;
import com.example.conversa.R;
import com.example.conversa.database.ChatroomSection;
import com.example.conversa.database.UserSection;
import com.example.conversa.manager.AndroidManager;
import com.example.conversa.manager.FirebaseManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecentChatHistory extends FirestoreRecyclerAdapter<ChatroomSection, RecentChatHistory.lastChatDetailsViewHolder> {

    Context context;

    public RecentChatHistory(@NonNull FirestoreRecyclerOptions<ChatroomSection> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull lastChatDetailsViewHolder holder, int position, @NonNull ChatroomSection model) {
        // Getting the other user's details from the chatroom
        FirebaseManager.getOtherUserDetailsFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            // Was the last message sent by the current user or the other user?
                            boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseManager.getCurrentUserId());
                            // Retrieving the other user's details
                            UserSection otherUserSection = task.getResult().toObject(UserSection.class);
                            // Adding the other user's profile picture
                            FirebaseManager.getOtherProfilePic(otherUserSection.getUserId()).getDownloadUrl()
                                    .addOnCompleteListener(updateProfilePicture -> {
                                        if(updateProfilePicture.isSuccessful()){
                                            Uri uri  = updateProfilePicture.getResult();
                                            // Using AndroidManager's set profile pic method to change
                                            // the profile pic
                                            AndroidManager.setUserProfilePic(context,uri,holder.profilePic);
                                        }
                                    });
                            // Setting the username
                            holder.username.setText(otherUserSection.getUsername());
                            // Setting the last message text that appears on the home screen
                            if(lastMessageSentByMe)
                                holder.lastMessage.setText("You : "+model.getLastMessage());
                            else
                                holder.lastMessage.setText(model.getLastMessage());
                            // Setting the time that will appear as well
                            holder.lastMessageTime.setText(FirebaseManager.convertTimestampToString(model.getLastMessageTimestamp()));
                            // Handling the click on the item to go to the Chat Screen
                            holder.itemView.setOnClickListener(v -> {
                                // Go to Chat Screen
                                Intent intent = new Intent(context, Chat.class);
                                AndroidManager.sendUserSectionDetails(intent, otherUserSection);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            });

                        }
                });
    }

    @NonNull
    @Override
    public lastChatDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the flayout for every row
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_history_content,parent,false);
        return new lastChatDetailsViewHolder(view);
    }
    // ViewHolder class to hold the info
    class lastChatDetailsViewHolder extends RecyclerView.ViewHolder{
        // Details for the box that will appear in the home screen
        // Text for the user
        TextView username;
        // Text of the last message
        TextView lastMessage;
        // TIme the last message was sent
        TextView lastMessageTime;
        // Profile Pic of the user
        ImageView profilePic;

        public lastChatDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            // Setting those UI elements with findViewById
            username = itemView.findViewById(R.id.user_name_text);
            lastMessage = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
