package com.example.hellorescue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

public class ActivityIdScanner extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private DatabaseReference usersReference, eligibleReference, extractedNamesReference;
    private TextView extractedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_scanner);



        //show gif success
        ImageView gifImageView = findViewById(R.id.gif);
        Glide.with(this)
                .asGif()
                .load(R.drawable.check)
                .into(gifImageView);




        // Initialize Firebase Realtime Database references
        usersReference = FirebaseDatabase.getInstance().getReference("users");
        eligibleReference = FirebaseDatabase.getInstance().getReference("eligiblePersons");
        extractedNamesReference = FirebaseDatabase.getInstance().getReference("extractedNames");

        // Find the TextView for displaying extracted information
        extractedTextView = findViewById(R.id.extracted);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Assuming the scanned result is a JSON string
                        String scannedText = result.getText();
                        try {
                            JSONObject jsonObject = new JSONObject(scannedText);
                            JSONObject subject = jsonObject.getJSONObject("subject");

                            // Extract values as strings
                            String lName = subject.optString("lName", "N/A");
                            String fName = subject.optString("fName", "N/A");
                            String mName = subject.optString("mName", "N/A");
                            String philIDCardNumber = subject.optString("PCN", "N/A");

                            // Display the extracted information in the TextView
                            String extractedInfo = "First Name: " + fName + "\n" +
                                    "Last Name: " + lName + "\n" +
                                    "Middle Name: " + mName + "\n" +
                                    "PhilID Card Number: " + philIDCardNumber;
                            extractedTextView.setText(extractedInfo);

                            /**
                             //test if there is any extracted information during scanning
                            saveExtractedFullName(fName, lName, mName); **/

                            // Check if the scanned information is eligible and not already registered
                            checkEligibilityAndRegister(fName, lName, mName, philIDCardNumber);
                        } catch (JSONException e) {
                            Toast.makeText(ActivityIdScanner.this, "Failed to parse QR code", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();

            }
        });


        // Hide GIF when scanner view is clicked
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
                gifImageView.setVisibility(View.INVISIBLE);
            }
        });

        // Assuming you have a Button with id error_btn in your layout
        Button errorButton = findViewById(R.id.error_btn);
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Animate the tableLayout to fade out
                View tableLayout = findViewById(R.id.tableLayout);
                tableLayout.animate()
                        .alpha(0f) // Animate to full transparency
                        .setDuration(500) // Duration of the animation
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                tableLayout.setVisibility(View.INVISIBLE); // Set visibility to INVISIBLE after the fade-out
                            }
                        });
            }
        });



    }


    /**
     //test if there is any extracted information during scanning
    private void saveExtractedFullName(String fName, String lName, String mName) {
        // Combine first, last, and middle names into a single string
        String fullName = fName + " " + (mName.isEmpty() ? "" : mName + " ") + lName;

        String extractedId = extractedNamesReference.push().getKey(); // Generate a unique key
        if (extractedId != null) {
            extractedNamesReference.child(extractedId).setValue(fullName)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(ActivityIdScanner.this, "Success.", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(ActivityIdScanner.this, "Failed to save extracted name.", Toast.LENGTH_SHORT).show()
                    );
        }
    } **/



    private void checkEligibilityAndRegister(String fName, String lName, String mName, String philIDCardNumber) {
        eligibleReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isEligible = false;

                // Convert the scanned values to lowercase for case-insensitive comparison
                String scannedFNameLower = fName.toLowerCase();
                String scannedLNameLower = lName.toLowerCase();
                String scannedMNameLower = mName.toLowerCase();
                String scannedPhilIDCardNumberLower = philIDCardNumber.toLowerCase();

                // Check if the fName, lName, mName, and philIDCardNumber exist in the eligiblePersons list (case-insensitive)
                for (DataSnapshot data : snapshot.getChildren()) {
                    String eligibleFName = data.child("fName").getValue(String.class);
                    String eligibleLName = data.child("lName").getValue(String.class);
                    String eligibleMName = data.child("mName").getValue(String.class);
                    String eligiblePhilIDCardNumber = data.child("philIDCardNumber").getValue(String.class);

                    if (eligibleFName != null && eligibleLName != null &&
                            eligibleMName != null && eligiblePhilIDCardNumber != null) {
                        if (scannedFNameLower.equals(eligibleFName.toLowerCase()) &&
                                scannedLNameLower.equals(eligibleLName.toLowerCase()) &&
                                scannedMNameLower.equals(eligibleMName.toLowerCase()) &&
                                scannedPhilIDCardNumberLower.equals(eligiblePhilIDCardNumber.toLowerCase())) {
                            isEligible = true;
                            break;
                        }
                    }
                }

                if (isEligible) {
                    // Check if the person is already registered in the users table (case-insensitive)
                    usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            boolean alreadyRegistered = false;

                            // Check if the scanned information already exists in the users table
                            for (DataSnapshot data : userSnapshot.getChildren()) {
                                String registeredFName = data.child("firstName").getValue(String.class);
                                String registeredLName = data.child("lastName").getValue(String.class);
                                String registeredMName = data.child("middleName").getValue(String.class);
                                String registeredPhilIDCardNumber = data.child("philIDCardNumber").getValue(String.class);


                                if (registeredFName != null && registeredLName != null &&
                                        registeredMName != null && registeredPhilIDCardNumber != null) {
                                    if (scannedFNameLower.equals(registeredFName.toLowerCase()) &&
                                            scannedLNameLower.equals(registeredLName.toLowerCase()) &&
                                            scannedMNameLower.equals(registeredMName.toLowerCase()) &&
                                            scannedPhilIDCardNumberLower.equals(registeredPhilIDCardNumber.toLowerCase())) {
                                        alreadyRegistered = true;
                                        break;
                                    }
                                }
                            }

                            if (alreadyRegistered) {
                                //Toast.makeText(ActivityIdScanner.this, "Multiple registration not allowed.", Toast.LENGTH_SHORT).show();

                                // Show the table layout with a fade-in effect
                                View tableLayout = findViewById(R.id.tableLayout);
                                tableLayout.setVisibility(View.VISIBLE);
                                tableLayout.setAlpha(0f); // Set initial transparency to 0
                                tableLayout.animate()
                                        .alpha(1f) // Animate to full opacity
                                        .setDuration(500) // Duration of the animation
                                        .setListener(null); // No need for a listener

                            } else {
                                // Save data to Firebase since the person is eligible and not already registered
                                saveUserData(fName, lName, mName, philIDCardNumber);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //Toast.makeText(ActivityIdScanner.this, "Failed to check registration.", Toast.LENGTH_SHORT).show();
                        }
                    });


                } else {
                    Toast.makeText(ActivityIdScanner.this, "You are not eligible to register.", Toast.LENGTH_SHORT).show();

                    // Show the table layout with a fade-in effect
                    View tableLayout = findViewById(R.id.tableLayout);
                    tableLayout.setVisibility(View.VISIBLE);
                    tableLayout.setAlpha(0f); // Set initial transparency to 0
                    tableLayout.animate()
                            .alpha(1f) // Animate to full opacity
                            .setDuration(500) // Duration of the animation
                            .setListener(null); // No need for a listener
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ActivityIdScanner.this, "Failed to check eligibility.", Toast.LENGTH_SHORT).show();
            }
        });
    }






    private void saveUserData(String fName, String lName, String mName, String philIDCardNumber) {
        String userId = usersReference.push().getKey(); // Generate a unique key
        if (userId != null) {
            User user = new User(fName, lName, mName, "", philIDCardNumber);
            usersReference.child(userId).setValue(user)
                    .addOnSuccessListener(aVoid -> {
                        extractedTextView.setVisibility(View.INVISIBLE);
                        ImageView gifImageView = findViewById(R.id.gif);
                        gifImageView.setVisibility(View.VISIBLE);

                        try {
                            GifDrawable gifDrawable = new GifDrawable(getResources().openRawResource(R.raw.check));
                            gifDrawable.setLoopCount(1); // Play once
                            gifImageView.setImageDrawable(gifDrawable);
                            gifImageView.animate().alpha(1f).setDuration(500);

                            // After GIF ends, start MobileNumberInputActivity
                            gifImageView.postDelayed(() -> {
                                Intent intent = new Intent(ActivityIdScanner.this, MobileNumberInputActivity.class);
                                intent.putExtra("FIRST_NAME", fName); // Pass first name
                                intent.putExtra("PHIL_ID_CARD_NUMBER", philIDCardNumber); // Pass PhilIDCardNumber
                                intent.putExtra("USER_ID", userId); // Pass user ID
                                startActivity(intent);
                                finish(); // Close this activity
                            }, gifDrawable.getDuration()); // Use the duration of the GIF
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(ActivityIdScanner.this, "Failed to Register.", Toast.LENGTH_SHORT).show());
        }
    }





    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    // User data class for Firebase
    public static class User {
        public String firstName;
        public String lastName;
        public String middleName;
        public String suffix;
        public String philIDCardNumber;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String firstName, String lastName, String middleName, String suffix, String philIDCardNumber) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.middleName = middleName;
            this.suffix = suffix;
            this.philIDCardNumber = philIDCardNumber;
        }
    }
}