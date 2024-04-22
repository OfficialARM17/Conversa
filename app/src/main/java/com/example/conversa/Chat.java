package com.example.conversa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.conversa.history.UserChatHistory;
import com.example.conversa.database.ChatMessageSection;
import com.example.conversa.database.ChatroomSection;
import com.example.conversa.database.UserSection;
import com.example.conversa.manager.AndroidManager;
import com.example.conversa.manager.FirebaseManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class Chat extends AppCompatActivity {
    // Couple of Request Code variables for accessing different screens
    private static final int COMBINE_LETTERS_REQUEST_CODE = 100;
    private static final int SPEECH_TO_TEXT_REQUEST_CODE = 200;
    // Variables needed to get chatroom information for a specific user
    UserSection secondUser;
    String chatroomId;
    ChatroomSection chatroomSection;
    UserChatHistory adapter;
    // Defining UI elements
    EditText messageInput;
    ImageButton sendMessageBtn, menu_Btn;
    ImageButton backBtn, chat_history_Btn;
    TextToSpeech tts;
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        // Initializing the TTS system
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    // The TTS will speak out words with an English Accent
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });

        // Saving details from other classes
        secondUser = AndroidManager.getUserSectionDetails(getIntent());
        chatroomId = FirebaseManager.createChatroomId(FirebaseManager.getCurrentUserId(),secondUser.getUserId());
        // Linking UI elements together
        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        menu_Btn = findViewById(R.id.attach_button);
        backBtn = findViewById(R.id.back_btn);
        chat_history_Btn = findViewById(R.id.chat_history_button);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        imageView = findViewById(R.id.profile_pic_image_view);
        // Getting the profile picture of the user you are talking to and then
        // assigning it to the imageview at the top of the screen
        FirebaseManager.getOtherProfilePic(secondUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri  = t.getResult();
                        AndroidManager.setUserProfilePic(this,uri,imageView);
                    }
                });
        // If the user presses the back button, they can go back to the home screen
        backBtn.setOnClickListener((v)->{
            onBackPressed();
        });
        // Set the textview at the top to the person you are talking to
        otherUsername.setText(secondUser.getUsername());
        // if the user presses the chat icon on the top right, the last message
        // entered will be read out for someone to hear
        chat_history_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lastMessage = chatroomSection.getLastMessage();
                tts.speak(lastMessage,TextToSpeech.QUEUE_FLUSH,null);
            }
        });
        // Defining the menu button to pick between talking with speech-to-text
        // or using the sign language translation
        menu_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define the pop up menu that will appear with the two options
                PopupMenu popupMenu = new PopupMenu(Chat.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Check which menu item was clicked based on its ID
                        if (item.getItemId() == R.id.ASL_Translation_Button) {
                            // If the user selects sign language translation
                            Toast.makeText(Chat.this, "Sign Language Translation Selected", Toast.LENGTH_SHORT).show();
                            // Create intent with the package name of the second app
                            // Create an Intent to navigate to CombineLettersActivity
                            Intent intent = new Intent();
                            // Go to the sign language translation app and open the ASL Letter Combination screen
                            intent.setClassName("com.example.conversaimageprocessing", "com.example.conversaimageprocessing.ASL_Letter_Combination");
                            // If the app is installed, the user will go to Conversa Image Processing
                            if (intent != null) {
                                // Start Conversa Image Processing
                                startActivityForResult(intent, COMBINE_LETTERS_REQUEST_CODE);
                            } else {
                                // Output error message if the app is not installed
                                Toast.makeText(Chat.this, "App is not Installed", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        } else if (item.getItemId() == R.id.Speech_To_TextButton) {
                            // If the user selects Speech to Text
                            Toast.makeText(Chat.this, "Speech to Text Selected", Toast.LENGTH_SHORT).show();
                            // Handle Speech to Text button click
                            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
                            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to Text");
                            try {
                                startActivityForResult(intent, SPEECH_TO_TEXT_REQUEST_CODE);
                            } catch (Exception e) {
                                Toast.makeText(Chat.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });
        // If this button is pressed, the current value on the text box
        // as long as it is not empty will be sent
        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            // Go to the send message to user method
            sendMessageToUser(message);
        }));
        // Load the chatroom or create one if needed
        getOrCreateChatroomModel();
        // Load previous chat history if present
        loadChatHistory();
    }
    // Method to accommodate going to another application or using Speech to Text API
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COMBINE_LETTERS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Get the text sent back from CombineLettersActivity
            String receivedText = data.getStringExtra("testMessage");
            // Update the EditText (messageInput) with the received text
            messageInput.setText(receivedText);
        } else if(requestCode == SPEECH_TO_TEXT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            messageInput.setText(Objects.requireNonNull(result).get(0));
        }
    }
    // Load chat history method
    void loadChatHistory(){
        // Load the history of the chatroom and descend it by order like a chat application
        Query query = FirebaseManager.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);
        // Use FirestoreRecyclerOptions to gather specific data needed to get the chat message loading
        FirestoreRecyclerOptions<ChatMessageSection> options = new FirestoreRecyclerOptions.Builder<ChatMessageSection>()
                .setQuery(query, ChatMessageSection.class).build();
        // Create an instance of UserChatHistory with correct information
        adapter = new UserChatHistory(options,getApplicationContext());
        // Create a layout to showcase the messages
        LinearLayoutManager chathistory = new LinearLayoutManager(this);
        // Reverse the layout so that the latest message is at the bottom
        chathistory.setReverseLayout(true);
        // Apply this to the recycler view
        recyclerView.setLayoutManager(chathistory);
        recyclerView.setAdapter(adapter);
        // Begin checking for new changes such as a new message
        adapter.startListening();
        // Use an observer to monitor when new items are added
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }
    // Method to get/create a chatroom model inside of Firebase
    void getOrCreateChatroomModel(){
        // Get the reference of the specific chatroom you are looking for
        FirebaseManager.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            // If the chatroom is present
            if(task.isSuccessful()){
                // Convert the information retrieved into a ChatroomSection object
                chatroomSection = task.getResult().toObject(ChatroomSection.class);
                // Check if this value is null or not
                if(chatroomSection ==null){
                    // Create a new chatroom with a new id, both users' ids
                    // the current timestamp and an empty string as the last message
                    chatroomSection = new ChatroomSection(
                            chatroomId,
                            Arrays.asList(FirebaseManager.getCurrentUserId(),secondUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    // Save this to Firebase
                    FirebaseManager.getChatroomReference(chatroomId).set(chatroomSection);
                }
            }
        });
    }
    // Method to send a message to a user in a chatroom
    void sendMessageToUser(String message){
        // Update the last message metadata with new message info
        chatroomSection.setLastMessageTimestamp(Timestamp.now());
        chatroomSection.setLastMessageSenderId(FirebaseManager.getCurrentUserId());
        chatroomSection.setLastMessage(message);
        // Update Firebase with the new information
        FirebaseManager.getChatroomReference(chatroomId).set(chatroomSection);
        // Create a new chat message section with the message info that
        // is being sent
        ChatMessageSection chatMessageSection = new ChatMessageSection(message, FirebaseManager.getCurrentUserId(),Timestamp.now());
        // Add the new message to the messages collection in Firebase
        FirebaseManager.getChatroomMessageReference(chatroomId).add(chatMessageSection)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        // If the message has been successfully added to Firebase
                        if(task.isSuccessful()){
                            messageInput.setText("");
                        }
                    }
                });
    }

}