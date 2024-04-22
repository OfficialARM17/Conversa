package com.example.conversa.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.conversa.R;
import com.hbb20.CountryCodePicker;

public class LoginFirstPage extends AppCompatActivity {
    // Declaring IDs for the UI elements
    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button sendOtpBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_first_page);
        // Defining UI elements with the use of findViewById
        countryCodePicker = findViewById(R.id.login_countrycode);
        phoneInput = findViewById(R.id.login_mobile_number);
        sendOtpBtn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        // Remove the progress loading when the system is done
        progressBar.setVisibility(View.GONE);
        //set the country code picker to work with the phone number edit text
        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sendOtpBtn.setOnClickListener((v)->{
            if(!countryCodePicker.isValidFullNumber()){
                // if the phone number isn't valid, output message
                phoneInput.setError("Phone Number entered is not valid");
                return;
            }
            // Go to the next step in the log in/sign up process
            Intent intent = new Intent(LoginFirstPage.this, LoginSecondPage.class);
            intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);
        });
    }


}