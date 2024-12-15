package com.example.hellorescue.barangay;

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
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.hellorescue.R;

public class HistoryBarangayFragment extends AppCompatActivity {
    private static final int ANIMATION_DURATION_MS = 1000;
    private boolean isAnimationRunning = false; // prevent spam clicks
    private ViewPager2 viewPager2;
    private TextView typeOfAccidentTextView, dateTextView;
    private boolean isFilterVisible = false;

    // Drag slide variables
    private View dragSlide;
    private View mainContainer;
    private View filterContainer;
    private float initialTouchY;
    private float dragThreshold;
    private boolean isDragging = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_barangay);

        // Set up the back button
        ImageView backButton = findViewById(R.id.history_barangay_back);
        backButton.setOnClickListener(v -> finish()); // Close the current Activity

        // Set up the filter button
        ImageButton filterButton = findViewById(R.id.barangay_history_filter);

        // Initialize drag slide elements
        dragSlide = findViewById(R.id.drag_slide);
        mainContainer = findViewById(R.id.main_container);
        filterContainer = findViewById(R.id.filter_container);

        // Calculate drag threshold (10% of screen height)
        dragThreshold = getResources().getDisplayMetrics().heightPixels * 0.1f;

        // Set up drag slide touch listener
        dragSlide.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Store initial touch position
                        initialTouchY = event.getRawY();
                        isDragging = false;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        // Calculate the distance moved
                        float currentY = event.getRawY();
                        float deltaY = currentY - initialTouchY;

                        // Only start dragging if moved beyond touch slop
                        if (Math.abs(deltaY) > ViewConfiguration.get(HistoryBarangayFragment.this).getScaledTouchSlop()) {
                            isDragging = true;
                        }

                        // If dragging, move the container
                        if (isDragging && isFilterVisible) {
                            // Ensure we're only moving downwards
                            if (deltaY > 0) {
                                mainContainer.setTranslationY(deltaY);
                                filterContainer.setAlpha(1 - (deltaY / mainContainer.getHeight()));
                            }
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (isDragging && isFilterVisible) {
                            float finalY = event.getRawY();
                            float totalDragDistance = finalY - initialTouchY;

                            // Determine if we should close the filter based on drag distance
                            if (totalDragDistance > dragThreshold) {
                                // Close the filter
                                closeFilter();
                            } else {
                                // Snap back to original position
                                resetFilterPosition();
                            }
                            return true;
                        }
                        return false;
                }
                return false;
            }
        });

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
                        backButton.setEnabled(false);
                    }
                });
                showAnimatorSet.start();
            } else {
                closeFilter();
                backButton.setEnabled(true);
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
                        closeFilter();
                        backButton.setEnabled(true);
                        return true;
                    }
                }
                return false;
            }
        });

        // Initialize the ViewPager2 and set the adapter
        viewPager2 = findViewById(R.id.viewPager);
        HistoryBarangayPagerAdapter adapter = new HistoryBarangayPagerAdapter(this);
        viewPager2.setAdapter(adapter);

        // Initialize TextViews
        typeOfAccidentTextView = findViewById(R.id.type_of_accident_barangay);
        dateTextView = findViewById(R.id.date_barangay);
        ImageButton accidentLine = findViewById(R.id.accident_line);
        ImageButton dateLine = findViewById(R.id.date_line);

        // Set the default color to black and highlight the default selected TextView
        typeOfAccidentTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        dateTextView.setTextColor(getResources().getColor(android.R.color.black));
        accidentLine.setVisibility(View.VISIBLE);

        // Set the ViewPager to show the "Type of Accident" fragment (position 0) by default
        viewPager2.setCurrentItem(0);

        // Add listeners to navigate between fragments based on the TextView clicks
        typeOfAccidentTextView.setOnClickListener(v -> {
            accidentLine.setVisibility(View.VISIBLE);

            // Reset scale to initial state
            accidentLine.setScaleX(0f);

            // Animate the line to full width
            accidentLine.animate()
                    .scaleX(1f)
                    .setDuration(300)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();

            // Change text color and ViewPager
            viewPager2.setCurrentItem(0);
            typeOfAccidentTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            dateTextView.setTextColor(getResources().getColor(android.R.color.black));
        });

        dateTextView.setOnClickListener(v -> {
            dateLine.setVisibility(View.VISIBLE);

            // Reset scale to initial state
            dateLine.setScaleX(0f);

            // Animate the line to full width
            dateLine.animate()
                    .scaleX(1f)
                    .setDuration(300)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();

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

                boolean isFirstPage = (position == 0);
                View activeLineView = isFirstPage ? accidentLine : dateLine;
                View inactiveLineView = isFirstPage ? dateLine : accidentLine;

                TextView activeTextView = isFirstPage ? typeOfAccidentTextView : dateTextView;
                TextView inactiveTextView = isFirstPage ? dateTextView : typeOfAccidentTextView;

                // Animate active line in
                activeLineView.setVisibility(View.VISIBLE);
                activeLineView.setScaleX(0f);
                activeLineView.animate()
                        .scaleX(1f)
                        .setDuration(300)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();

                // Animate inactive line out
                inactiveLineView.animate()
                        .scaleX(0f)
                        .setDuration(300)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .withEndAction(() -> inactiveLineView.setVisibility(View.INVISIBLE))
                        .start();

                // Update text colors
                activeTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                inactiveTextView.setTextColor(getResources().getColor(android.R.color.black));
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

    // Method to close the filter with animation
    private void closeFilter() {
        if (!isFilterVisible) return;

        ObjectAnimator slideDown = ObjectAnimator.ofFloat(mainContainer, "translationY", mainContainer.getHeight());
        slideDown.setDuration(300);
        slideDown.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(filterContainer, "alpha", 0f);
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

    // Method to reset filter position if drag is not sufficient
    private void resetFilterPosition() {
        ObjectAnimator resetTranslation = ObjectAnimator.ofFloat(mainContainer, "translationY", 0f);
        resetTranslation.setDuration(200);
        resetTranslation.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator resetAlpha = ObjectAnimator.ofFloat(filterContainer, "alpha", 1f);
        resetAlpha.setDuration(200);

        AnimatorSet resetAnimatorSet = new AnimatorSet();
        resetAnimatorSet.playTogether(resetTranslation, resetAlpha);
        resetAnimatorSet.start();
    }
}