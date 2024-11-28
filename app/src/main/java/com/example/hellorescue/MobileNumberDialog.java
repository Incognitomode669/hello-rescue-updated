/*package com.example.hellorescue;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

public class MobileNumberInputActivity extends AppCompatActivity {
    private DatabaseReference usersReference;
    private String userIdToDelete;
    private EditText editTextPhone;

    private EditText otpET1, otpET2, otpET3, otpET4;
    private TextView resendBtn;
    private Button verifyBtn;

    // Resend OTP in seconds
    private int resendTime = 60;

    // Will be true after 60 seconds
    private boolean resendEnabled = false;

    private int selectedETPosition = 0;

    private String mobileNumberTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_number_input);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        otpET1 = findViewById(R.id.otpET1);
        otpET2 = findViewById(R.id.otpET2);
        otpET3 = findViewById(R.id.otpET3);
        otpET4 = findViewById(R.id.otpET4);
        resendBtn = findViewById(R.id.resendBtn);
        verifyBtn = findViewById(R.id.verifyBtn);
        final TextView mobileNumber = findViewById(R.id.mobileNumber);

        otpET1.addTextChangedListener(textWatcher);
        otpET2.addTextChangedListener(textWatcher);
        otpET3.addTextChangedListener(textWatcher);
        otpET4.addTextChangedListener(textWatcher);

        // Default open keyboard on first edit text
        showKeyBoard(otpET1);

        startCountDownTimer();

        // Initialize Firebase reference
        usersReference = FirebaseDatabase.getInstance().getReference("users");

        // Retrieve the user ID passed from ActivityIdScanner
        userIdToDelete = getIntent().getStringExtra("USER_ID");

        // Set up phone number input validation
        editTextPhone = findViewById(R.id.editTextPhone);
        setPhoneNumberInputFilter(editTextPhone);

        // Set up the continue button
        Button continueButton = findViewById(R.id.continue_button);
        continueButton.setOnClickListener(v -> storeMobileNumber());
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Registration")
                .setMessage("Do you really want to cancel your registration?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Delete the user's uploaded information
                    deleteUploadedData();
                    finish(); // Close the activity
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteUploadedData() {
        if (userIdToDelete != null) {
            usersReference.child(userIdToDelete).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        // Data deleted successfully
                    })
                    .addOnFailureListener(e -> {
                        // Handle the error
                    });
        }
    }

    private void storeMobileNumber() {
        String mobileNumber = editTextPhone.getText().toString().trim();
        if (mobileNumber.length() == 10) {
            // Prepend "+63" to the mobile number
            String formattedMobileNumber = "+63" + mobileNumber;

            // Store the mobile number in the database
            usersReference.child(userIdToDelete).child("mobileNumber").setValue(formattedMobileNumber)
                    .addOnSuccessListener(aVoid -> {
                        // Mobile number stored successfully
                        storeDefaultAddress();
                        setUsernameAndPassword();
                    })
                    .addOnFailureListener(e -> {
                        // Handle the error
                    });
        } else {
            editTextPhone.setError("Please enter a valid mobile number.");
        }
    }

    private void storeDefaultAddress() {
        usersReference.child(userIdToDelete).child("address").setValue("Poblacion, Trinidad")
                .addOnSuccessListener(aVoid -> {
                    // Address stored successfully
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }

    private void setUsernameAndPassword() {
        usersReference.child(userIdToDelete).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fName = dataSnapshot.child("firstName").getValue(String.class);
                    String lName = dataSnapshot.child("lastName").getValue(String.class);
                    String philIDCardNumber = dataSnapshot.child("philIDCardNumber").getValue(String.class);

                    if (fName != null && lName != null && philIDCardNumber != null && philIDCardNumber.length() >= 4) {
                        String username = fName;
                        String password = philIDCardNumber.substring(0, 4) + lName;

                        usersReference.child(userIdToDelete).child("username").setValue(username);
                        usersReference.child(userIdToDelete).child("password").setValue(password)
                                .addOnSuccessListener(aVoid -> showLocalNotification(username, password))
                                .addOnFailureListener(e -> {
                                    // Handle the error
                                });
                    } else {
                        // Handle missing or invalid data
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void setPhoneNumberInputFilter(EditText editText) {
        InputFilter phoneFilter = (source, start, end, dest, dstart, dend) -> {
            String newNumber = dest.toString().substring(0, dstart) + source.toString() + dest.toString().substring(dend);

            if (newNumber.length() > 10) {
                return ""; // Reject if it exceeds 10 digits
            }

            if (newNumber.length() == 1 && !newNumber.equals("9")) {
                editText.setError("In the Philippines, phone numbers should start with 9");
                return ""; // Reject if it starts with any digit other than '9'
            } else {
                editText.setError(null); // Clear the error if input is valid
            }

            return null;
        };

        editText.setFilters(new InputFilter[]{phoneFilter});
    }

    private void showLocalNotification(String username, String password) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("user_credentials_channel",
                    "User Credentials Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for user credentials");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "user_credentials_channel")
                .setSmallIcon(R.drawable.baseline_circle_notifications_24)
                .setContentTitle("New Message Received")
                .setContentText("You have received your login credentials.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Username: " + username + "\nPassword: " + password))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request notification permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }
        notificationManager.notify(0, builder.build());
    }

    private void startCountDownTimer() {
        new CountDownTimer(resendTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resendEnabled = false;
                resendBtn.setEnabled(false);
                resendBtn.setText("Resend OTP in " + millisUntilFinished / 1000 + " seconds");
            }

            @Override
            public void onFinish() {
                resendEnabled = true;
                resendBtn.setEnabled(true);
                resendBtn.setText("Resend OTP");
            }
        }.start();
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 1) {
                if (selectedETPosition < 3) {
                    selectedETPosition++;
                    focusOnNextOTPField();
                }
            } else if (s.length() == 0) {
                if (selectedETPosition > 0) {
                    selectedETPosition--;
                    focusOnPreviousOTPField();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void focusOnNextOTPField() {
        switch (selectedETPosition) {
            case 1:
                otpET2.requestFocus();
                break;
            case 2:
                otpET3.requestFocus();
                break;
            case 3:
                otpET4.requestFocus();
                break;
        }
    }

    private void focusOnPreviousOTPField() {
        switch (selectedETPosition) {
            case 0:
                otpET1.requestFocus();
                break;
            case 1:
                otpET2.requestFocus();
                break;
            case 2:
                otpET3.requestFocus();
                break;
        }
    }

    private void showKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}*/