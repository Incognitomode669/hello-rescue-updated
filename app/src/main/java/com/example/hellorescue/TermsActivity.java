package com.example.hellorescue;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class TermsActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private AppCompatButton acceptButton;
    private TextView tvTermsConditions;
    private ImageButton termsBackButton; // Declare the back button

    private static final String TAG = "TermsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms);

        scrollView = findViewById(R.id.scrollView);
        acceptButton = findViewById(R.id.accept_button);
        tvTermsConditions = findViewById(R.id.tv_terms_conditions);
        termsBackButton = findViewById(R.id.terms_back); // Initialize the back button

        // Set text view to be scrollable
        tvTermsConditions.setMovementMethod(new ScrollingMovementMethod());

        // Initially set the button to be semi-transparent and unclickable
        acceptButton.setAlpha(0.6f); // Set initial opacity to 60%
        acceptButton.setClickable(false); // Make button unclickable
        acceptButton.setEnabled(false); // Ensure button is disabled

        // Scroll listener to check when scrolled to bottom
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (scrollView.getChildAt(0) != null) {
                int scrollY = scrollView.getScrollY();
                int scrollViewHeight = scrollView.getHeight();
                int scrollContentHeight = scrollView.getChildAt(0).getHeight();

                // Debug log for scroll position
                Log.d(TAG, "Scroll Y: " + scrollY + ", ScrollView Height: " + scrollViewHeight + ", Content Height: " + scrollContentHeight);

                // Check if we are at the bottom
                if (scrollY + scrollViewHeight >= scrollContentHeight) {
                    // Set button to fully opaque, clickable, and enabled
                    acceptButton.setAlpha(1.0f); // Set opacity to 100%
                    acceptButton.setClickable(true); // Make button clickable
                    acceptButton.setEnabled(true); // Enable button
                    Log.d(TAG, "Reached bottom: Button is now clickable and fully opaque.");
                } else {
                    // Set button to semi-transparent, unclickable, and disabled
                    acceptButton.setAlpha(0.6f); // Set opacity to 60%
                    acceptButton.setClickable(false); // Make button unclickable
                    acceptButton.setEnabled(false); // Disable button
                    Log.d(TAG, "Not at bottom: Button is now unclickable and semi-transparent.");
                }
            }
        });

        // Set OnClickListener for the accept button
        acceptButton.setOnClickListener(v -> {
            // Navigate only if the button is enabled and clickable
            if (acceptButton.isEnabled() && acceptButton.isClickable()) {
                openIdScannerActivity();
            }
        });

        // Set OnClickListener for the back button
        termsBackButton.setOnClickListener(v -> {
            openMainActivity(); // Change to open MainActivity
        });
    }

    // Method to open the ID scanner activity
    private void openIdScannerActivity() {
        Intent intent = new Intent(TermsActivity.this, ActivityIdScanner.class);
        startActivity(intent);
    }

    // Method to open the MainActivity
    private void openMainActivity() {
        Intent intent = new Intent(TermsActivity.this, MainActivity.class); // Navigate to MainActivity
        startActivity(intent);
        finish(); // Optional: Call finish() to remove the TermsActivity from the back stack
    }
}
