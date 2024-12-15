package com.example.hellorescue;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import org.checkerframework.checker.nullness.qual.NonNull;
import java.util.concurrent.TimeUnit;

public class MobileNumberInputActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String verificationId;
    private DatabaseReference usersReference;
    private String userIdToDelete;
    private EditText editTextPhone;

    private EditText otpET1, otpET2, otpET3, otpET4,otpET5,otpET6;
    private TextView resendBtn;
    private Button verifyBtn;
    private Button verifyOTPBtn;

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

        mAuth = FirebaseAuth.getInstance(); // Initialize Firebase Auth


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        otpET1 = findViewById(R.id.otpET1);
        otpET2 = findViewById(R.id.otpET2);
        otpET3 = findViewById(R.id.otpET3);
        otpET4 = findViewById(R.id.otpET4);
        otpET5 = findViewById(R.id.otpET5);
        otpET6 = findViewById(R.id.otpET6);

        resendBtn = findViewById(R.id.resendBtn);
        verifyBtn = findViewById(R.id.verify);
        verifyOTPBtn = findViewById(R.id.verifyOTP);
        final TextView mobileNumber = findViewById(R.id.mobileNumber);

        otpET1.addTextChangedListener(textWatcher);
        otpET2.addTextChangedListener(textWatcher);
        otpET3.addTextChangedListener(textWatcher);
        otpET4.addTextChangedListener(textWatcher);
        otpET5.addTextChangedListener(textWatcher);
        otpET6.addTextChangedListener(textWatcher);

        // Default open keyboard on first edit text
        showKeyBoard(otpET1);

        startCountDownTimer();

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resendEnabled) {
                    // Resend Code here...
                    startCountDownTimer();
                    // Set mobile number to TextView
                    //mobileNumber.setText(mobileNumberTxt);
                }
            }
        });

        // Initialize Firebase reference
        usersReference = FirebaseDatabase.getInstance().getReference("users");

        // Retrieve the user ID passed from ActivityIdScanner
        userIdToDelete = getIntent().getStringExtra("USER_ID");

        // Set up phone number input validation
        editTextPhone = findViewById(R.id.editTextPhone);
        setPhoneNumberInputFilter(editTextPhone);




        // Set up the click listener for the verify button
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = editTextPhone.getText().toString().trim();

                if (phoneNumber.isEmpty() || phoneNumber.length() <= 9) {
                    editTextPhone.setError("Invalid phone number.");
                    editTextPhone.requestFocus();
                } else {// if the number is valid

                    // Set the text of mobileNumber TextView to the inputted phone number
                    TextView mobileNumberTextView = findViewById(R.id.mobileNumber);
                    mobileNumberTextView.setText("+63" + phoneNumber);
                    sendVerificationCode(phoneNumber);

                    // Make the verify button and phone input invisible
                    verifyBtn.setVisibility(View.INVISIBLE);
                    editTextPhone.setVisibility(View.INVISIBLE);

                    // Make the CountryCodePicker invisible
                    CountryCodePicker ccp = findViewById(R.id.ccp);
                    ccp.setVisibility(View.INVISIBLE);

                    // Make the RelativeLayout visible
                    RelativeLayout otpModal = findViewById(R.id.otpModal);
                    otpModal.setVisibility(View.VISIBLE);

                    // Load fade-in animation
                    Animation fadeIn = AnimationUtils.loadAnimation(MobileNumberInputActivity.this, R.anim.fade_in);
                    otpModal.startAnimation(fadeIn);
                }
            }
        });
        // Set up the click listener for the verify OTP button
        verifyOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide the keyboard
                hideKeyboard(view);

                String code = otpET1.getText().toString().trim() +
                        otpET2.getText().toString().trim() +
                        otpET3.getText().toString().trim() +
                        otpET4.getText().toString().trim() +
                        otpET5.getText().toString().trim() +
                        otpET6.getText().toString().trim();

                if (code.isEmpty() || code.length() < 6) {
                    showToast("Please enter a valid OTP");
                } else {
                    verifyCode(code);
                }
            }
        });

    }
    private void sendVerificationCode(String phoneNumber) {
        // Ensure mAuth is initialized
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+63" + phoneNumber)  // Add country code if necessary
                        .setTimeout(60L, TimeUnit.SECONDS)    // Timeout and unit
                        .setActivity(this)                    // Activity (for callback binding)
                        .setCallbacks(mCallbacks)             // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    String code = credential.getSmsCode();
                    if (code != null) {
                        otpET1.setText(String.valueOf(code.charAt(0)));
                        otpET2.setText(String.valueOf(code.charAt(1)));
                        otpET3.setText(String.valueOf(code.charAt(2)));
                        otpET4.setText(String.valueOf(code.charAt(3)));
                        otpET5.setText(String.valueOf(code.charAt(4)));
                        otpET6.setText(String.valueOf(code.charAt(5)));
                        verifyCode(code); // Automatically verify the code if it's retrieved
                    }
                }


                @Override
                public void onVerificationFailed(FirebaseException e) {
                    showToast("Verification Failed. " + e.getMessage());
                }

                @Override
                public void onCodeSent(@NonNull String s, PhoneAuthProvider.@NonNull ForceResendingToken token) {
                    super.onCodeSent(s, token);
                    verificationId = s;
                    showToast("OTP Sent");
                }
            };

    private void verifyCode(String code) {
        if (verificationId != null) {
            // Create a credential using the OTP entered by the user and the verificationId
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithCredential(credential);
        } else {
            showToast("Verification ID is null. Please try again.");
        }
    }


    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Find the TableLayout
                TableLayout tableLayout = findViewById(R.id.mobile_number_dialog);

                // Set visibility to VISIBLE
                tableLayout.setVisibility(View.VISIBLE);

                // Load the fade-in animation and apply it
                Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                tableLayout.startAnimation(fadeInAnimation);

                // Use a Handler to introduce a 3-second delay before navigating to the login screen
                new Handler().postDelayed(() -> {
                    // Create an Intent to navigate to the login screen
                    Intent intent = new Intent(this, MainActivity.class);  // Change MainActivity to the activity that handles login

                    // Clear the activity stack up to TermsActivity
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    // Start the login activity
                    startActivity(intent);

                    // Optionally, finish the current activity so that the user cannot go back to it
                    finish();
                }, 3000);  // 3000 milliseconds = 3 seconds

                // Continue with the rest of your app's functionality
                storeMobileNumber();

            } else {
                showToast("Invalid OTP");
            }
        });
    }



    private void storeMobileNumber() {
        String mobileNumber = editTextPhone.getText().toString().trim();
        if (mobileNumber.length() == 10) {
            // Prepend "+63" to the mobile number
            String formattedMobileNumber = "+63" + mobileNumber;

            // Store the mobile number in the database
            usersReference.child(userIdToDelete).child("mobileNumber").setValue(formattedMobileNumber)
                    .addOnSuccessListener(aVoid -> {
//                        // Mobile number stored successfully
//                        storeDefaultAddress();
                        setUsernameAndPassword();
                    })
                    .addOnFailureListener(e -> {
                        // Handle the error
                    });
        } else {
            editTextPhone.setError("Please enter a valid mobile number.");
        }
    }

//    private void storeDefaultAddress() {
//        usersReference.child(userIdToDelete).child("address").setValue("Poblacion, Trinidad, Bohol, Philippines")
//                .addOnSuccessListener(aVoid -> {
//                    // Address stored successfully
//                })
//                .addOnFailureListener(e -> {
//                    // Handle the error
//                });
//    }

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



    private void showToast(String message) {
        Toast.makeText(MobileNumberInputActivity.this, message, Toast.LENGTH_SHORT).show();
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







    private void setPhoneNumberInputFilter(EditText editText) {
        InputFilter phoneFilter = (source, start, end, dest, dstart, dend) -> {
            String newNumber = dest.toString().substring(0, dstart) + source.toString() + dest.toString().substring(dend);

            if (newNumber.length() > 10) {
                return ""; // Reject if it exceeds 10 digits
            }

            if (newNumber.length() == 1 && !newNumber.equals("9")) {
                editText.setError("Phone numbers should start with 9");
                return ""; // Reject if it starts with any digit other than '9'
            } else {
                editText.setError(null); // Clear the error if input is valid
            }

            return null;
        };

        editText.setFilters(new InputFilter[]{phoneFilter});
    }



    private void startCountDownTimer() {

        resendEnabled = false;
        resendBtn.setTextColor(Color.parseColor("#99000000"));

        new CountDownTimer(resendTime * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                resendBtn.setText("Resend Code (" + (millisUntilFinished / 1000) + ")");
            }

            @Override
            public void onFinish() {
                resendEnabled = true;
                resendBtn.setText("Resend Code");
                resendBtn.setTextColor(ContextCompat.getColor(MobileNumberInputActivity.this, android.R.color.holo_blue_dark));
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
                if (selectedETPosition < 5) {
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
            case 4:
                otpET5.requestFocus();
                break;
            case 5:
                otpET6.requestFocus();
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
            case 3:
                otpET4.requestFocus();
                break;
            case 4:
                otpET5.requestFocus();
                break;

        }

    }

    private void showKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    // Method to hide the keyboard
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }



}
