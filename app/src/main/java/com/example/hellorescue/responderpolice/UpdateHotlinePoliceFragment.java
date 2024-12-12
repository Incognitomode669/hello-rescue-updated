package com.example.hellorescue.responderpolice;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.hellorescue.R;

public class UpdateHotlinePoliceFragment extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_hotline_police);

        // Initialize the back button
        ImageView backButton = findViewById(R.id.update_hotline_police_back);
        ImageButton addHotlineButton = findViewById(R.id.add_hotline);

        // Set a click listener to finish the activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the current Activity and return to the previous one
                finish();
            }
        });

        // Add click listener for the add hotline button
        addHotlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement your add hotline logic here
                // For example, open a dialog or navigate to an add hotline screen
                // You might want to use an Intent to start a new Activity
                // startActivity(new Intent(this, AddHotlineActivity.class));
            }
        });
    }
}
