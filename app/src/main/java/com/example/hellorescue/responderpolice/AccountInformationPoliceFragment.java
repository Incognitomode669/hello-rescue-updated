package com.example.hellorescue.responderpolice;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.hellorescue.R;

public class AccountInformationPoliceFragment extends AppCompatActivity {
    private View changePasswordBody;
    private View changePasswordContainer;
    private ImageView editPasswordButton;

    private View changeUsernameBody;
    private View changeUsernameContainer;
    private ImageView editUsernameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_information_police);

        // Initialize the back button
        ImageView backButton = findViewById(R.id.account_information_police_back);

        // Initialize change password elements
        changePasswordBody = findViewById(R.id.change_password_body);
        changePasswordContainer = findViewById(R.id.change_password_container);
        editPasswordButton = findViewById(R.id.edit_password);

        // Initialize change username elements
        changeUsernameBody = findViewById(R.id.change_username_body);
        changeUsernameContainer = findViewById(R.id.change_username_container);
        editUsernameButton = findViewById(R.id.edit_username);

        // Set a click listener to finish the activity
        backButton.setOnClickListener(v -> finish());

        // Set click listener for edit password with animation
        setupEditButtonAnimation(editPasswordButton, this::showChangePasswordSection);

        // Set click listener for edit username with animation
        setupEditButtonAnimation(editUsernameButton, this::showChangeUsernameSection);

        // Set click listeners for closing sections when clicked outside
        changePasswordBody.setOnClickListener(v -> hideChangePasswordSection());
        changeUsernameBody.setOnClickListener(v -> hideChangeUsernameSection());

        // Initially hide the change sections
        changePasswordBody.setVisibility(View.GONE);
        changeUsernameBody.setVisibility(View.GONE);

        // Prevent clicks on the containers from closing the sections
        changePasswordContainer.setOnClickListener(v -> {});
        changeUsernameContainer.setOnClickListener(v -> {});
    }

    // Generic method to set up edit button animation
    private void setupEditButtonAnimation(ImageView button, Runnable showSection) {
        button.setOnClickListener(v -> {
            // Apply scale animation
            Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.button_scale);
            v.startAnimation(scaleAnimation);

            // After animation, show section
            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    showSection.run();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });
    }

    private void showChangePasswordSection() {
        changePasswordBody.setVisibility(View.VISIBLE);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        changePasswordBody.startAnimation(fadeIn);
    }

    private void showChangeUsernameSection() {
        changeUsernameBody.setVisibility(View.VISIBLE);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        changeUsernameBody.startAnimation(fadeIn);
    }

    private void hideChangePasswordSection() {
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                changePasswordBody.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        changePasswordBody.startAnimation(fadeOut);
    }

    private void hideChangeUsernameSection() {
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                changeUsernameBody.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        changeUsernameBody.startAnimation(fadeOut);
    }
}