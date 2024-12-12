package com.example.hellorescue.lgu;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hellorescue.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class AddResponderLguFragment extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, reEnterPasswordEditText;
    private AutoCompleteTextView roleDropdown;
    private TextInputLayout roleInputLayout;
    private Button createAccountButton;
    private ProgressBar loadingProgressBar;

    // Regex for username and password validation
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_responder_lgu);

        // Initialize views
        ImageView backButton = findViewById(R.id.add_responder_lgu_back);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        reEnterPasswordEditText = findViewById(R.id.re_enter_password);
        roleDropdown = findViewById(R.id.auto_complete_txt_custom);
        roleInputLayout = findViewById(R.id.TextInputLayoutCustom_container);
        createAccountButton = findViewById(R.id.create_account_button);
        loadingProgressBar = findViewById(R.id.loading_progress_bar);

        // Back button
        backButton.setOnClickListener(v -> finish());

        // Sample items for dropdown
        String[] itemsCustom = {"POLICE", "BFP", "MDRRMO", "Barangay"};

        // Set up the dropdown adapter
        ArrayAdapter<String> adapterItemsAddResponder = new ArrayAdapter<>(this, R.layout.list_item, itemsCustom);
        roleDropdown.setAdapter(adapterItemsAddResponder);

        // Manage hints for the dropdown
        roleDropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                roleInputLayout.setHint(null);
            } else if (roleDropdown.getText().toString().isEmpty()) {
                roleInputLayout.setHint("Select Option");
            }
        });

        // Set click listener for the create account button
        createAccountButton.setOnClickListener(v -> createAccount());
    }

    private void createAccount() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String reEnterPassword = reEnterPasswordEditText.getText().toString().trim();
        String role = roleDropdown.getText().toString().trim();

        // Comprehensive input validation
        if (!validateInput(username, password, reEnterPassword, role)) {
            return;
        }

        // Disable button and show loading
        setLoadingState(true);

        // Hash password using Base64-encoded SHA-256
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            Toast.makeText(this, "Error processing password", Toast.LENGTH_SHORT).show();
            setLoadingState(false);
            return;
        }

        // Check for existing username across ALL roles
        checkUsernameAvailabilityAcrossRoles(username, hashedPassword, role);
    }

    private boolean validateInput(String username, String password, String reEnterPassword, String role) {
        // Check for empty fields
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(reEnterPassword) || TextUtils.isEmpty(role)) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Username validation
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            Toast.makeText(this, "Invalid username. Use 3-20 alphanumeric characters or underscores", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Password validation
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            Toast.makeText(this, "Password must be at least 8 characters with letters and numbers", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Password matching
        if (!password.equals(reEnterPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void checkUsernameAvailabilityAcrossRoles(String username, String hashedPassword, String role) {
        DatabaseReference respondersRef = FirebaseDatabase.getInstance().getReference("Responders");

        respondersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot rootSnapshot) {
                boolean usernameExists = false;

                // Check username across all roles
                for (DataSnapshot roleSnapshot : rootSnapshot.getChildren()) {
                    for (DataSnapshot userSnapshot : roleSnapshot.getChildren()) {
                        String existingUsername = userSnapshot.child("username").getValue(String.class);
                        if (username.equals(existingUsername)) {
                            usernameExists = true;
                            break;
                        }
                    }
                    if (usernameExists) break;
                }

                if (usernameExists) {
                    Toast.makeText(AddResponderLguFragment.this,
                            "Username already exists across roles. Please choose another.",
                            Toast.LENGTH_SHORT).show();
                    setLoadingState(false);
                } else {
                    saveResponderToFirebase(username, hashedPassword, role);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddResponderLguFragment.this,
                        "Error checking username: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                setLoadingState(false);
            }
        });
    }

    private void saveResponderToFirebase(String username, String hashedPassword, String role) {
        DatabaseReference respondersRef = FirebaseDatabase.getInstance().getReference("Responders");
        DatabaseReference newResponderRef = respondersRef.child(role).push();

        newResponderRef.child("username").setValue(username);
        newResponderRef.child("hashedPassword").setValue(hashedPassword);
        newResponderRef.child("role").setValue(role)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Responder account created successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                    setLoadingState(false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to create account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    setLoadingState(false);
                });
    }

    private void clearFields() {
        usernameEditText.setText("");
        passwordEditText.setText("");
        reEnterPasswordEditText.setText("");
        roleDropdown.setText("");
    }

    private void setLoadingState(boolean isLoading) {
        createAccountButton.setEnabled(!isLoading);
        loadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    // Secure password hashing method matching MainActivity's implementation
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            return Base64.encodeToString(digest, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}