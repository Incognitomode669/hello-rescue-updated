package com.example.hellorescue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager; // Import for hiding the keyboard
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hellorescue.ui.AdminPoliceActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 3;

    private int denialCount = 0;
    private EditText usernameEditText, passwordEditText;
    private Button loginButton, createAccountButton;
    private DatabaseReference usersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Set full-screen mode
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // Initialize Firebase database reference
        usersDatabase = FirebaseDatabase.getInstance().getReference("users");

        usernameEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_pass);
        loginButton = findViewById(R.id.login_btn);
        createAccountButton = findViewById(R.id.create_account_button);

        createAccountButton.setOnClickListener(v -> {
            requestCameraPermission();
        });

        loginButton.setOnClickListener(v -> {
            hideKeyboard(); // Hide the keyboard before processing login
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!username.isEmpty() && !password.isEmpty()) {
                checkLoginCredentials(username, password);
            } else {
                Toast.makeText(MainActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void checkLoginCredentials(String username, String password) {
        // Check for police credentials first
        if (username.equals("police") && password.equals("police")) {
            // Successful police login, redirect to AdminPoliceActivity
            navigateToAdminPolice();
        } else {
            // Check Firebase for other user credentials
            usersDatabase.orderByChild("username").equalTo(username)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    String storedPassword = userSnapshot.child("password").getValue(String.class);
                                    if (storedPassword != null && storedPassword.equals(password)) {
                                        // Successful login for other users
                                        navigateToHome();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(MainActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void navigateToAdminPolice() {
        // After successful police login, navigate to AdminPoliceActivity
        Intent intent = new Intent(MainActivity.this, AdminPoliceActivity.class);
        startActivity(intent);
        finish(); // Close the login activity
    }


    private void navigateToHome() {
        // After successful login, navigate to Main activity
        Intent intent = new Intent(MainActivity.this, Main_Navigation.class);
        startActivity(intent);
        finish(); // Optionally close the login activity
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            requestNotificationPermission();
        }
    }

    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
        } else {
            openTermsActivity();
        }
    }

    private void openTermsActivity() {
        Intent intent = new Intent(MainActivity.this, TermsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
                denialCount++;
                handlePermissionDenied();
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission();
            } else {
                denialCount++;
                handlePermissionDenied();
            }
        } else if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openTermsActivity();
            } else {
                denialCount++;
                handlePermissionDenied();
            }
        }
    }

    private void handlePermissionDenied() {
        if (denialCount >= 3) {
            showPermissionDeniedDialog();
        } else {
            Toast.makeText(this, "Permission denied. Please allow access to continue.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("To continue using the app, please grant access to the Camera, Location, and Notifications. This is essential for the features you want to use. You can enable these permissions in the app settings.")
                .setCancelable(false)
                .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(android.net.Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_permission_warning)
                .show();
    }
}
