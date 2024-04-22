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
import com.example.conversa.manager.AndroidManager;
import com.example.conversa.manager.FirebaseManager;
import com.example.conversa.database.UserSection;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserHistory extends FirestoreRecyclerAdapter<UserSection, SearchUserHistory.searchUserDetailsViewHolder> {

    Context context;

    public SearchUserHistory(@NonNull FirestoreRecyclerOptions<UserSection> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull searchUserDetailsViewHolder holder, int position, @NonNull UserSection model) {
        // Setting the username and phone number
        holder.username.setText(model.getUsername());
        holder.phone.setText(model.getPhone());
        // Checking if the user is the current user
        if(model.getUserId().equals(FirebaseManager.getCurrentUserId())){
            holder.username.setText(model.getUsername()+" <-- (Myself)");
        }
        // Accessing the profile pic and adding it for the user
        FirebaseManager.getOtherProfilePic(model.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri  = t.getResult();
                        // Using AndroidManager to set the profile pic
                        AndroidManager.setUserProfilePic(context,uri,holder.profilePic);
                    }
                });
        // Handling the item click for when the user presses the item
        holder.itemView.setOnClickListener(v -> {
            // Go to the Chat Screen
            Intent intent = new Intent(context, Chat.class);
            // Send the appropriate user's details to the Chat Screen
            // so that the info for the corresponding user is present
            AndroidManager.sendUserSectionDetails(intent,model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public searchUserDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the layout for every row of the Recycler View
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_history_content,parent,false);
        return new searchUserDetailsViewHolder(view);
    }
    // Class to hold the views
    class searchUserDetailsViewHolder extends RecyclerView.ViewHolder{
        // Details that will display during the user search
        // The user's name
        TextView username;
        // The user's phone number
        TextView phone;
        // The user's profile picture
        ImageView profilePic;

        public searchUserDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            // Setting those UI elements with findViewById
            username = itemView.findViewById(R.id.user_name_text);
            phone = itemView.findViewById(R.id.phone_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
