package com.example.hellorescue.barangay;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.hellorescue.R;

public class UpdateHotlineBarangayFragment extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_hotline_barangay);

        // Initialize the back button
        ImageView backButton = findViewById(R.id.update_hotline_barangay_back);
        ImageButton addHotlineButton = findViewById(R.id.add_hotline_barangay);

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
