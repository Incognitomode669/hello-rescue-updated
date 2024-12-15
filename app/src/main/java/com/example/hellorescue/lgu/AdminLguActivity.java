package com.example.hellorescue.lgu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hellorescue.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminLguActivity extends AppCompatActivity implements ResponderAdapter.OnResponderClickListener, ResponderTypeFilterLguFragment.FilterChangeListener   {

    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    private RecyclerView recyclerView;
    private ResponderAdapter adapter;
    private DatabaseReference respondersRef;

    private ImageButton filterButton;
    private boolean isAnimationRunning = false;
    private boolean isFilterVisible = false;



    private ViewPager2 viewPager2;
    private TextView typeOfResponderTextView;

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
        setContentView(R.layout.lgu_admin);

        initializeViews();
        setupToolbarAndDrawer();  // Changed from setupAndDrawer()
        setupNavigationClickListeners();
        setupRespondersRecyclerView();
        loadResponders();
        setupHeaderText();
        setupFilterButton();
        setupViewPager();
    }

    private void setupViewPager() {
        ResponderLguFilterPagerAdapter pagerAdapter = new ResponderLguFilterPagerAdapter(this);
        viewPager2.setAdapter(pagerAdapter);  // Use pagerAdapter, not adapter

        ImageButton respondersLine = findViewById(R.id.responders_line);

        typeOfResponderTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        respondersLine.setVisibility(View.VISIBLE);

        viewPager2.setCurrentItem(0);

        typeOfResponderTextView.setOnClickListener(v -> {
            respondersLine.setVisibility(View.VISIBLE);

            respondersLine.setScaleX(0f);

            respondersLine.animate()
                    .scaleX(1f)
                    .setDuration(300)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();

            viewPager2.setCurrentItem(0);
            typeOfResponderTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        });





        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                typeOfResponderTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light));

                respondersLine.setVisibility(View.VISIBLE);
                respondersLine.setScaleX(0f);
                respondersLine.animate()
                        .scaleX(1f)
                        .setDuration(300)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();
            }
        });
    }



    // Filter communication interface implementation from ResponderTypeFilterLguFragment
    @Override
    public void onFilterChanged(String role, boolean isChecked) {
        switch (role) {
            case "POLICE":
                adapter.setPoliceFilter(isChecked);
                break;
            case "FIRE":
                adapter.setFireFilter(isChecked);
                break;
            case "MDRRMO":
                adapter.setMDRRMOFilter(isChecked);
                break;
            case "BARANGAY":
                adapter.setBarangayFilter(isChecked);
                break;
        }
    }




    private void initializeViews() {
        navigationView = findViewById(R.id.navview);
        drawerLayout = findViewById(R.id.lgu_drawer);
        toolbar = findViewById(R.id.toolbar_lgu);
        recyclerView = findViewById(R.id.responders_recycler_view);
        filterButton = findViewById(R.id.lgu_filter);
        viewPager2 = findViewById(R.id.viewPager);
        typeOfResponderTextView = findViewById(R.id.type_of_responder);

        // Initialize drag slide elements
        dragSlide = findViewById(R.id.drag_slide);
        mainContainer = findViewById(R.id.main_container_type_of_responder);
        filterContainer = findViewById(R.id.filter_container_type_of_responder);

        // Calculate drag threshold (10% of screen height)
        dragThreshold = getResources().getDisplayMetrics().heightPixels * 0.1f;

        // Initialize Firebase reference
        respondersRef = FirebaseDatabase.getInstance().getReference("Responders");
    }


    private void setupToolbarAndDrawer() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void setupNavigationClickListeners() {
        LinearLayout AccountInformation = findViewById(R.id.change_password_username_lgu);
        LinearLayout AddResponder = findViewById(R.id.add_responder);

        AccountInformation.setOnClickListener(v -> {
            Intent intent = new Intent(this, AccountInformationLguFragment.class);
            startActivity(intent);
        });

        AddResponder.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddResponderLguFragment.class);
            startActivity(intent);
        });
    }

    private void setupRespondersRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResponderAdapter(this, this);
        recyclerView.setAdapter(adapter);
    }

    private void loadResponders() {
        respondersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Responder> respondersList = new ArrayList<>();
                for (DataSnapshot roleSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot userSnapshot : roleSnapshot.getChildren()) {
                        Responder responder = new Responder(
                                userSnapshot.getKey(),
                                userSnapshot.child("username").getValue(String.class),
                                userSnapshot.child("hashedPassword").getValue(String.class),
                                userSnapshot.child("role").getValue(String.class)
                        );
                        respondersList.add(responder);
                    }
                }
                adapter.setResponders(respondersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminLguActivity.this, "Error loading responders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupHeaderText() {
        TextView rescueTextView = findViewById(R.id.rescue);
        String helloText = "Hello ";
        String rescueText = "Rescue";
        SpannableString spannableString = new SpannableString(helloText + rescueText);

        spannableString.setSpan(
                new ForegroundColorSpan(Color.BLACK),
                0,
                helloText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        spannableString.setSpan(
                new ForegroundColorSpan(Color.RED),
                helloText.length(),
                spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        rescueTextView.setText(spannableString);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupFilterButton() {
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
                        if (Math.abs(deltaY) > ViewConfiguration.get(AdminLguActivity.this).getScaledTouchSlop()) {
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

            // Play the GIF animation
            Drawable drawable = filterButton.getDrawable();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && drawable instanceof AnimatedImageDrawable) {
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

            isAnimationRunning = true;

            if (!isFilterVisible) {
                // Show filter (Slide Up and Fade In)
                mainContainer.setTranslationY(2000f);
                mainContainer.setVisibility(View.VISIBLE);

                ObjectAnimator slideUp = ObjectAnimator.ofFloat(mainContainer, "translationY",0f);
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
                closeFilter();
            }
        });

        View mainParent = findViewById(R.id.filter_container_type_of_responder);
        mainParent.setOnTouchListener((v, event) -> {
            // Check if the touch is outside the main_container
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View mainContainer = findViewById(R.id.main_container_type_of_responder);
                if (!isPointInsideView(mainContainer, event.getRawX(), event.getRawY())) {
                    // Trigger slide down animation
                    closeFilter();
                    return true;
                }
            }
            return false;
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

    @Override
    public void onEditClick(Responder responder) {
        showEditDialog(responder);
    }

    @Override
    public void onDeleteClick(Responder responder) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Responder")
                .setMessage("Are you sure you wantonder?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    respondersRef.child(responder.getRole()).child(responder.getId()).removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Responder deleted", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete responder", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showEditDialog(Responder responder) {
        // TODO: Implement the edit dialog
        // You can create a custom dialog or start a new activity for editing
        Toast.makeText(this, "Edit functionality to be implemented", Toast.LENGTH_SHORT).show();
    }
}
