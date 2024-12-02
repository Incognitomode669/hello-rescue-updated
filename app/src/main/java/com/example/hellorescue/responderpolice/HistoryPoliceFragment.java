package com.example.hellorescue.responderpolice;

import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.hellorescue.R;
import com.example.hellorescue.ui.HistoryPolicePagerAdapter;

public class HistoryPoliceFragment extends AppCompatActivity {
    private static final int ANIMATION_DURATION_MS = 1000;
    private boolean isAnimationRunning = false; // prevent spam clicks
    private ViewPager2 viewPager2;
    private TextView typeOfAccidentTextView, dateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_police);

        // Set up the back button
        ImageView backButton = findViewById(R.id.history_police_back);
        backButton.setOnClickListener(v -> finish()); // Close the current Activity

        // Set up the filter button
        ImageButton filterButton = findViewById(R.id.police_history_filter);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P) // AnimatedImageDrawable requires API 28+
            @Override
            public void onClick(View v) {
                if (isAnimationRunning) {
                    return; // Prevent starting another animation if already running
                }

                Drawable drawable = filterButton.getDrawable();
                if (drawable instanceof AnimatedImageDrawable) {
                    AnimatedImageDrawable animatedGif = (AnimatedImageDrawable) drawable;
                    isAnimationRunning = true;
                    animatedGif.start();
                    new Handler().postDelayed(() -> {
                        animatedGif.stop();
                        isAnimationRunning = false; // Reset the flag
                    }, ANIMATION_DURATION_MS);
                }
            }
        });

        // Initialize the ViewPager2 and set the adapter
        viewPager2 = findViewById(R.id.viewPager);
        HistoryPolicePagerAdapter adapter = new HistoryPolicePagerAdapter(this);
        viewPager2.setAdapter(adapter);

        // Initialize TextViews
        typeOfAccidentTextView = findViewById(R.id.type_of_accident_police);
        dateTextView = findViewById(R.id.date_police);

        // Set the default color to black and highlight the default selected TextView
        typeOfAccidentTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        dateTextView.setTextColor(getResources().getColor(android.R.color.black));

        // Set the ViewPager to show the "Type of Accident" fragment (position 0) by default
        viewPager2.setCurrentItem(0);

        // Add listeners to navigate between fragments based on the TextView clicks
        typeOfAccidentTextView.setOnClickListener(v -> {
            // Set the ViewPager to the "Type of Accident" fragment (position 0)
            viewPager2.setCurrentItem(0);
            // Change text color to red for selected TextView
            typeOfAccidentTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            // Reset the other TextView color to black
            dateTextView.setTextColor(getResources().getColor(android.R.color.black));
        });

        dateTextView.setOnClickListener(v -> {
            // Set the ViewPager to the "Date" fragment (position 1)
            viewPager2.setCurrentItem(1);
            // Change text color to red for selected TextView
            dateTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            // Reset the other TextView color to black
            typeOfAccidentTextView.setTextColor(getResources().getColor(android.R.color.black));
        });

        // Add a listener to update the color when the user swipes between pages
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    // "Type of Accident" selected
                    typeOfAccidentTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    dateTextView.setTextColor(getResources().getColor(android.R.color.black));
                } else if (position == 1) {
                    // "Date" selected
                    dateTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    typeOfAccidentTextView.setTextColor(getResources().getColor(android.R.color.black));
                }
            }
        });
    }
}
