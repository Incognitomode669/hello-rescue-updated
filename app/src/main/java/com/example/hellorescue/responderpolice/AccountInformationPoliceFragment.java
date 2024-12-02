package com.example.hellorescue.responderpolice;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.hellorescue.R;

public class AccountInformationPoliceFragment extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_information_police);

        // Initialize the back button
        ImageView backButton = findViewById(R.id.account_information_police_back);


        // Set a click listener to finish the activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the current Activity and return to the previous one
                finish();
            }
        });
    }
}
