package com.example.hellorescue.lgu;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    private static final String[] ROLES = {"POLICE", "FIRE", "MDRRMO", "BARANGAY"};

    private EditText usernameEditText, passwordEditText, reEnterPasswordEditText;
    private AutoCompleteTextView roleDropdown;
    private TextInputLayout roleInputLayout;
    private Button createAccountButton;
    private ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_responder_lgu);

        initializeViews();
        setupRoleDropdown();
        setupListeners();
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        reEnterPasswordEditText = findViewById(R.id.re_enter_password);
        roleDropdown = findViewById(R.id.auto_complete_txt_custom);
        roleInputLayout = findViewById(R.id.TextInputLayoutCustom_container);
        createAccountButton = findViewById(R.id.create_account_button);
        loadingProgressBar = findViewById(R.id.loading_progress_bar);

        findViewById(R.id.add_responder_lgu_back).setOnClickListener(v -> finish());
    }

    private void setupRoleDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, ROLES);
        roleDropdown.setAdapter(adapter);
    }

    private void setupListeners() {
        roleDropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                roleInputLayout.setHint(null);
            } else if (roleDropdown.getText().toString().isEmpty()) {
                roleInputLayout.setHint("Select Option");
            }
        });

        createAccountButton.setOnClickListener(v -> createAccount());
    }

    private void createAccount() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String reEnterPassword = reEnterPasswordEditText.getText().toString().trim();
        String role = roleDropdown.getText().toString().trim();

        if (validateInput(username, password, reEnterPassword, role)) {
            setLoadingState(true);
            String hashedPassword = hashPassword(password);

            if (hashedPassword != null) {
                checkUsernameAvailability(username, hashedPassword, role);
            } else {
                handlePasswordHashError();
            }
        }
    }

    private boolean validateInput(String username, String password, String reEnterPassword, String role) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(reEnterPassword) || TextUtils.isEmpty(role)) {
            showToast("All fields must be filled");
            return false;
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            showToast("Invalid username. Use 3-20 alphanumeric characters or underscores");
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            showToast("Password must be at least 8 characters with letters and numbers");
            return false;
        }

        if (!password.equals(reEnterPassword)) {
            showToast("Passwords do not match");
            return false;
        }

        return true;
    }

    private void checkUsernameAvailability(String username, String hashedPassword, String role) {
        DatabaseReference respondersRef = FirebaseDatabase.getInstance().getReference("Responders");

        respondersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot rootSnapshot) {
                boolean usernameExists = isUsernameTaken(rootSnapshot, username);

                if (usernameExists) {
                    handleExistingUsername();
                } else {
                    saveResponderToFirebase(username, hashedPassword, role);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                handleDatabaseError(error);
            }
        });
    }

    private boolean isUsernameTaken(DataSnapshot rootSnapshot, String username) {
        for (DataSnapshot roleSnapshot : rootSnapshot.getChildren()) {
            for (DataSnapshot userSnapshot : roleSnapshot.getChildren()) {
                String existingUsername = userSnapshot.child("username").getValue(String.class);
                if (username.equals(existingUsername)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void saveResponderToFirebase(String username, String hashedPassword, String role) {
        DatabaseReference respondersRef = FirebaseDatabase.getInstance().getReference("Responders");
        DatabaseReference newResponderRef = respondersRef.child(role).push();

        newResponderRef.child("username").setValue(username);
        newResponderRef.child("hashedPassword").setValue(hashedPassword);
        newResponderRef.child("role").setValue(role)
                .addOnSuccessListener(v -> handleSuccessfulAccountCreation())
                .addOnFailureListener(this::handleAccountCreationFailure);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void handlePasswordHashError() {
        showToast("Error processing password");
        setLoadingState(false);
    }

    private void handleExistingUsername() {
        showToast("Username already exists across roles. Please choose another.");
        setLoadingState(false);
    }

    private void handleSuccessfulAccountCreation() {
        showToast("Responder account created successfully");
        clearFields();
        setLoadingState(false);
    }

    private void handleAccountCreationFailure(Exception e) {
        showToast("Failed to create account: " + e.getMessage());
        setLoadingState(false);
    }

    private void handleDatabaseError(DatabaseError error) {
        showToast("Error checking username: " + error.getMessage());
        setLoadingState(false);
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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}