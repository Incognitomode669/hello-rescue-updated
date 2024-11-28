/*package com.example.hellorescue;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class IdScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_scanner); // Ensure this matches your XML file name
    }
}
*/


/*package com.example.hellorescue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class activity_id_scanner extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private TextView extractedTextView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_scanner);

        // Initialize the TextView for displaying extracted data
        extractedTextView = findViewById(R.id.extracted);

        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

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
                            String pob = subject.optString("POB", "N/A");
                            String mName = subject.optString("mName", "N/A");
                            String suffix = subject.optString("Suffix", "N/A");
                            String pcn = subject.optString("PCN", "N/A");

                            // Display the extracted values
                            String extractedText = "lName: " + lName + "\n" +
                                    "fName: " + fName + "\n" +
                                    "Suffix: " + suffix + "\n" +
                                    "mName: " + mName + "\n" +
                                    "PCN: " + pcn;
                            extractedTextView.setText(extractedText);

                            // Save data to Firebase
                            String userId = databaseReference.push().getKey(); // Generate a unique key
                            if (userId != null) {
                                User user = new User(fName, lName, mName, suffix, pcn);
                                databaseReference.child(userId).setValue(user)
                                        .addOnSuccessListener(aVoid ->
                                                Toast.makeText(activity_id_scanner.this, "Data saved successfully", Toast.LENGTH_SHORT).show()
                                        )
                                        .addOnFailureListener(e ->
                                                Toast.makeText(activity_id_scanner.this, "Failed to save data", Toast.LENGTH_SHORT).show()
                                        );
                            }

                        } catch (JSONException e) {
                            Toast.makeText(activity_id_scanner.this, "Failed to parse QR code", Toast.LENGTH_SHORT).show();
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
*/
