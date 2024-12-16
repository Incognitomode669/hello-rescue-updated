package com.example.hellorescue.responderpolice;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellorescue.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpdateHotlinePoliceFragment extends AppCompatActivity {
    private final String role = "POLICE";
    private EditText enterNumberEditText;
    private TextView errorHintText;
    private RecyclerView responderRecyclerView;
    private HotlineAdapter hotlineAdapter;
    private DatabaseReference hotlineRef;
    private List<Hotline> hotlineList;
    private View addNewHotlineNumberBody;
    private View addNewHotlineNumberContainer;
    private ImageView addHotlinePolice;
    private Button addHotlineButton;
    private Animation shakeAnimation;
    private TextView counterTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_hotline_police);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        loadHotlines();
    }

    private void initializeViews() {
        enterNumberEditText = findViewById(R.id.enter_the_number);
        errorHintText = findViewById(R.id.error_hint_text_view);
        responderRecyclerView = findViewById(R.id.responders_recycler_view);
        addNewHotlineNumberBody = findViewById(R.id.add_new_hotline_number_body);
        addNewHotlineNumberContainer = findViewById(R.id.add_new_hotline_number_container);
        addHotlinePolice = findViewById(R.id.add_hotline_police);
        addHotlineButton = findViewById(R.id.add_hotline_button);

        errorHintText.setVisibility(View.INVISIBLE);

        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        counterTextView = findViewById(R.id.counter);





        hotlineList = new ArrayList<>();
        hotlineRef = FirebaseDatabase.getInstance().getReference("hotlines").child(role);

        addNewHotlineNumberBody.setVisibility(View.GONE);


        enterNumberEditText.addTextChangedListener(new TextWatcher() {
            private boolean isChanging = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isChanging) return;

                String text = s.toString();
                isChanging = true;


                // Update the counter
                int length = text.length();
                String counterText = length + "/11";
                counterTextView.setText(counterText);

                // Change the color of the counter based on the input length
                if (length == 11) {
                    counterTextView.setTextColor(getResources().getColor(R.color.green));
                } else {
                    counterTextView.setTextColor(getResources().getColor(R.color.black));
                }

                // Reset error state by default
                errorHintText.setVisibility(View.INVISIBLE);
                addHotlineButton.setEnabled(true);
                addHotlineButton.animate()
                        .alpha(1.0f)
                        .setDuration(200)
                        .start();

                if (text.length() >= 2) {
                    if (!text.substring(0, 2).equals("09")) {
                        enterNumberEditText.setText("");
                        showError("Number must start with 09");
                        counterTextView.setText("0/11");
                    } else if (text.length() < 11) {
                        errorHintText.setVisibility(View.VISIBLE);
                        errorHintText.setText("Number must be 11 digits");
                        addHotlineButton.setEnabled(false);
                        addHotlineButton.animate()
                                .alpha(0.5f)
                                .setDuration(200)
                                .start();
                    }  else if (text.length() == 11) {
                        checkHotlineExists(text);
                    }
                } else if (text.length() == 1 && !text.equals("0")) {
                    enterNumberEditText.setText("");
                    showError("Number must start with 09");
                    counterTextView.setText("0/11");
                }

                isChanging = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });



    }

    private void showError(String errorMessage) {
        errorHintText.setText(errorMessage);
        errorHintText.setVisibility(View.VISIBLE);
        errorHintText.startAnimation(shakeAnimation);
        addHotlineButton.setEnabled(false);
        addHotlineButton.animate()
                .alpha(0.5f)
                .setDuration(200)
                .start();
    }





    private void checkHotlineExists(String number) {
        hotlineRef.orderByChild("number").equalTo(number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    errorHintText.setVisibility(View.VISIBLE);
                    errorHintText.setText("This number already exists");
                    addHotlineButton.setEnabled(false); // Disable the button

                    addHotlineButton.animate()
                            .alpha(0.5f)
                            .setDuration(200)
                            .start();
                } else {
                    errorHintText.setVisibility(View.INVISIBLE);
                    addHotlineButton.setEnabled(true); // Enable the button

                    addHotlineButton.animate()
                            .alpha(1.0f)
                            .setDuration(200)
                            .start();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorHintText.setText("Database error occurred");
                addHotlineButton.setEnabled(true); // Enable the button in case of an error

                addHotlineButton.animate()
                        .alpha(1.0f)
                        .setDuration(200)
                        .start();
            }
        });
    }


    private void setupRecyclerView() {
        hotlineAdapter = new HotlineAdapter(hotlineList, hotlineRef);
        responderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        responderRecyclerView.setAdapter(hotlineAdapter);
    }

    private void setupClickListeners() {
        ImageView backButton = findViewById(R.id.update_hotline_police_back);
        backButton.setOnClickListener(v -> finish());

        Button addHotlineButton = findViewById(R.id.add_hotline_button);
        addHotlineButton.setOnClickListener(v -> validateAndAddHotline());

        addNewHotlineNumberBody.setOnClickListener(v -> hideAddHotlineNumberSection());
        addNewHotlineNumberContainer.setOnClickListener(v -> {/* don't hide if container is clicked */});
        addHotlinePolice.setOnClickListener(v -> showAddHotlineSection());
    }

    private void validateAndAddHotline() {
        String number = enterNumberEditText.getText().toString().trim();

        if (number.isEmpty()) {
            errorHintText.setVisibility(View.VISIBLE);
            errorHintText.setText("Please enter a number");
            return;
        }

        if (errorHintText.getVisibility() == View.VISIBLE) {
            return;
        }

        Hotline newHotline = new Hotline(number, role);
        String key = hotlineRef.push().getKey();
        if (key != null) {
            newHotline.setKey(key);
            hotlineRef.child(key).setValue(newHotline)
                    .addOnSuccessListener(aVoid -> {
                        enterNumberEditText.setText("");
                        hideAddHotlineNumberSection();
                        loadHotlines();

                        // Show toast message when hotline is added successfully
                        Toast.makeText(UpdateHotlinePoliceFragment.this,
                                "Hotline added successfully", Toast.LENGTH_SHORT).show();

                        // Close the keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(enterNumberEditText.getWindowToken(), 0);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(UpdateHotlinePoliceFragment.this,
                                    "Failed to add number", Toast.LENGTH_SHORT).show());
        }
    }


    private void showAddHotlineSection() {
        addNewHotlineNumberBody.setVisibility(View.VISIBLE);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        addNewHotlineNumberBody.startAnimation(fadeIn);

        addHotlinePolice.setEnabled(false);

        ObjectAnimator fadeOutAnimator = ObjectAnimator.ofFloat(addHotlinePolice, "alpha", 1.0f, 0.8f);
        fadeOutAnimator.setDuration(200);
        fadeOutAnimator.start();
    }

    private void hideAddHotlineNumberSection() {
        Animation fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                addNewHotlineNumberBody.setVisibility(View.GONE);
                addHotlinePolice.setEnabled(true);

                ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(addHotlinePolice, "alpha", 0.8f, 1.0f);
                fadeInAnimator.setDuration(50);
                fadeInAnimator.start();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(enterNumberEditText.getWindowToken(), 0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        addNewHotlineNumberBody.startAnimation(fadeOut);
    }

    private void loadHotlines() {
        hotlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hotlineList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Hotline hotline = dataSnapshot.getValue(Hotline.class);
                    if (hotline != null) {
                        hotline.setKey(dataSnapshot.getKey());
                        hotlineList.add(hotline);
                    }
                }
                hotlineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateHotlinePoliceFragment.this,
                        "Failed to load hotlines", Toast.LENGTH_SHORT).show();
            }

        });
    }
}