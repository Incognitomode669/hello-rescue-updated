package com.example.hellorescue.barangay;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.hellorescue.R;
import com.google.android.material.textfield.TextInputLayout;

public class AddEligibleUserBarangayFragment extends AppCompatActivity {

    private AutoCompleteTextView roleDropdown;
    private TextInputLayout roleInputLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_eligible_user);


        // Initialize the back button
        ImageView backButton = findViewById(R.id.add_eligible_barangay_back);
        roleDropdown = findViewById(R.id.auto_complete_txt_custom);
        roleInputLayout = findViewById(R.id.TextInputLayoutCustom_container);


        String[] address = {"Poblacion, Trinidad, Bohol "};

        // Set up the dropdown adapter
        ArrayAdapter<String> adapterItemsAddResponder = new ArrayAdapter<>(this, R.layout.list_item, address);
        roleDropdown.setAdapter(adapterItemsAddResponder);

        // Manage hints for the dropdown
        roleDropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                roleInputLayout.setHint(null);
            } else if (roleDropdown.getText().toString().isEmpty()) {
                roleInputLayout.setHint("Select Option");
            }
        });










        // Set a click listener to finish the activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the current Activity and return to the previous one
                finish();
            }
        });
    }
}
