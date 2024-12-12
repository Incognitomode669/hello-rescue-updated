package com.example.hellorescue.client;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView; // Assuming your back button is an ImageView or similar.

import androidx.appcompat.app.AppCompatActivity;

import com.example.hellorescue.R;

public class PersonalInformationFragment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_information);

        // Find the back button (update the ID based on your XML layout)
        ImageView backButton = findViewById(R.id.personal_information_back);

        // Set click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the current Activity and return to the previous one
                finish();
            }
        });
    }
}
