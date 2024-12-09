package com.example.hellorescue.responderpolice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.hellorescue.R;

public class HistoryPoliceFragment extends AppCompatActivity {
    private static final int ANIMATION_DURATION_MS = 1000;
    private boolean isAnimationRunning = false; // prevent spam clicks
    private ViewPager2 viewPager2;
    private TextView typeOfAccidentTextView, dateTextView;
    private boolean isFilterVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_police);

        // Set up the back button
        ImageView backButton = findViewById(R.id.history_police_back);
        backButton.setOnClickListener(v -> finish()); // Close the current Activity

        // Set up the filter button
        ImageButton filterButton = findViewById(R.id.police_history_filter);



        filterButton.setOnClickListener(v -> {
            // Check if already running
            if (isAnimationRunning) {
                return; // Prevent multiple animations at the same time
            }

            // Disable the button immediately
            filterButton.setEnabled(false);
            backButton.setEnabled(false);

            // Play the GIF animation
            Drawable drawable = filterButton.getDrawable();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (drawable instanceof AnimatedImageDrawable) {
                    AnimatedImageDrawable animatedDrawable = (AnimatedImageDrawable) drawable;
                    animatedDrawable.start(); // Start the GIF animation

                    // Stop the animation and re-enable button after 1 second
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (animatedDrawable.isRunning()) {
                            animatedDrawable.stop(); // Stop the animation
                        }

                        // Re-enable the button on the main thread
                        runOnUiThread(() -> {
                            filterButton.setEnabled(true);
                            isAnimationRunning = false;
                        });
                    }, 1000);
                }
            }

            isAnimationRunning = true;

            // Get the main container and filter container views
            View mainContainer = findViewById(R.id.main_container);
            View filterContainer = findViewById(R.id.filter_container);

            if (!isFilterVisible) {
                // Show filter (Slide Up and Fade In)
                mainContainer.setTranslationY(2000f);
                mainContainer.setVisibility(View.VISIBLE);

                ObjectAnimator slideUp = ObjectAnimator.ofFloat(mainContainer, "translationY", 0f);
                slideUp.setDuration(300);

                filterContainer.setAlpha(0f);
                filterContainer.setVisibility(View.VISIBLE);
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(filterContainer, "alpha", 0f, 1f);
                fadeIn.setDuration(300);

                AnimatorSet showAnimatorSet = new AnimatorSet();
                showAnimatorSet.playTogether(slideUp, fadeIn);
                showAnimatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isFilterVisible = true;
                    }
                });
                showAnimatorSet.start();
            } else {
                // Hide filter (Slide Down and Fade Out)
                ObjectAnimator slideDown = ObjectAnimator.ofFloat(mainContainer, "translationY", 2000f);
                slideDown.setDuration(300);

                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(filterContainer, "alpha", 1f, 0f);
                fadeOut.setDuration(300);

                AnimatorSet hideAnimatorSet = new AnimatorSet();
                hideAnimatorSet.playTogether(slideDown, fadeOut);
                hideAnimatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mainContainer.setVisibility(View.GONE);
                        filterContainer.setVisibility(View.GONE);
                        isFilterVisible = false;
                    }
                });
                hideAnimatorSet.start();
            }
        });



        View mainParent = findViewById(R.id.main_parent);
        mainParent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Check if the touch is outside the main_container
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    View mainContainer = findViewById(R.id.main_container);
                    if (!isPointInsideView(mainContainer, event.getRawX(), event.getRawY())) {
                        // Trigger slide down animation
                        hideFilter(); // Call the method to hide the filter (slide down and fade out)
                        return true;
                    }
                }
                return false;
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

    // Helper method to check if the touch point is inside a view
    private boolean isPointInsideView(View view, float x, float y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();

        return x >= left && x <= right && y >= top && y <= bottom;
    }

    // Method to hide the filter (slide down and fade out)
    private void hideFilter() {
        // Check if the filter is visible, if so, slide it down and fade it out
        View mainContainer = findViewById(R.id.main_container);
        View filterContainer = findViewById(R.id.filter_container);

        ObjectAnimator slideDown = ObjectAnimator.ofFloat(mainContainer, "translationY", 2000f);
        slideDown.setDuration(300);

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(filterContainer, "alpha", 1f, 0f);
        fadeOut.setDuration(300);

        AnimatorSet hideAnimatorSet = new AnimatorSet();
        hideAnimatorSet.playTogether(slideDown, fadeOut);
        hideAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainContainer.setVisibility(View.GONE);
                filterContainer.setVisibility(View.GONE);
                isFilterVisible = false;
            }
        });
        hideAnimatorSet.start();
    }
}
