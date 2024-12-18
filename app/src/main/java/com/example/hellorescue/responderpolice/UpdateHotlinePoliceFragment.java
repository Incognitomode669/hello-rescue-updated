package com.example.hellorescue.responderpolice;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    private View updateHotlineNumberBody;
    private View updateHotlineNumberContainer;
    private EditText enterNumberUpdateEditText;
    private TextView errorHintTextUpdateView;
    private TextView counterUpdateTextView;
    private Button updateHotlineButton;
    private Hotline selectedHotline;

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
        updateHotlineNumberBody = findViewById(R.id.update_hotline_number_body);
        updateHotlineNumberContainer = findViewById(R.id.update_hotline_number_container);
        enterNumberUpdateEditText = findViewById(R.id.enter_the_number_update);
        errorHintTextUpdateView = findViewById(R.id.error_hint_text_update_view);
        counterUpdateTextView = findViewById(R.id.counter_update);
        updateHotlineButton = findViewById(R.id.update_hotline_button);

        updateHotlineNumberBody.setVisibility(View.GONE);
        errorHintText.setVisibility(View.INVISIBLE);
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        counterTextView = findViewById(R.id.counter);

        hotlineList = new ArrayList<>();
        hotlineRef = FirebaseDatabase.getInstance().getReference("hotlines").child(role);

        addNewHotlineNumberBody.setVisibility(View.GONE);

        enterNumberEditText.addTextChangedListener(new TextWatcher() {
            private boolean isChanging = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isChanging) return;

                String text = s.toString();
                isChanging = true;

                int length = text.length();
                String counterText = length + "/11";
                counterTextView.setText(counterText);

                if (length == 11) {
                    counterTextView.setTextColor(getResources().getColor(R.color.green));
                } else {
                    counterTextView.setTextColor(getResources().getColor(R.color.black));
                }

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
                    } else if (text.length() == 11) {
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
            public void afterTextChanged(Editable s) {}
        });

        enterNumberUpdateEditText.addTextChangedListener(new TextWatcher() {
            private boolean isChanging = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isChanging) return;

                String text = s.toString();
                isChanging = true;

                int length = text.length();
                String counterText = length + "/11";
                counterUpdateTextView.setText(counterText);

                if (length == 11) {
                    counterUpdateTextView.setTextColor(getResources().getColor(R.color.green));
                } else {
                    counterUpdateTextView.setTextColor(getResources().getColor(R.color.black));
                }

                errorHintTextUpdateView.setVisibility(View.INVISIBLE);
                updateHotlineButton.setEnabled(true);
                updateHotlineButton.animate()
                        .alpha(1.0f)
                        .setDuration(200)
                        .start();

                if (text.length() >= 2) {
                    if (!text.substring(0, 2).equals("09")) {
                        enterNumberUpdateEditText.setText("");
                        showErrorUpdate("Number must start with 09");
                        counterUpdateTextView.setText("0/11");
                    } else if (text.length() < 11) {
                        errorHintTextUpdateView.setVisibility(View.VISIBLE);
                        errorHintTextUpdateView.setText("Number must be 11 digits");
                        updateHotlineButton.setEnabled(false);
                        updateHotlineButton.animate()
                                .alpha(0.5f)
                                .setDuration(200)
                                .start();
                    } else if (text.length() == 11) {
                        checkHotlineExistsForUpdate(text);
                    }
                } else if (text.length() == 1 && !text.equals("0")) {
                    enterNumberUpdateEditText.setText("");
                    showErrorUpdate("Number must start with 09");
                    counterUpdateTextView.setText("0/11");
                }

                isChanging = false;
            }

            @Override
            public void afterTextChanged(Editable s) {}
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

    private void showErrorUpdate(String errorMessage) {
        errorHintTextUpdateView.setText(errorMessage);
        errorHintTextUpdateView.setVisibility(View.VISIBLE);
        errorHintTextUpdateView.startAnimation(shakeAnimation);
        updateHotlineButton.setEnabled(false);
        updateHotlineButton.animate()
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
                    addHotlineButton.setEnabled(false);

                    addHotlineButton.animate()
                            .alpha(0.5f)
                            .setDuration(200)
                            .start();
                } else {
                    errorHintText.setVisibility(View.INVISIBLE);
                    addHotlineButton.setEnabled(true);

                    addHotlineButton.animate()
                            .alpha(1.0f)
                            .setDuration(200)
                            .start();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorHintText.setText("Database error occurred");
                addHotlineButton.setEnabled(true);

                addHotlineButton.animate()
                        .alpha(1.0f)
                        .setDuration(200)
                        .start();
            }
        });
    }

    private void checkHotlineExistsForUpdate(String number) {
        hotlineRef.orderByChild("number").equalTo(number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    errorHintTextUpdateView.setVisibility(View.VISIBLE);
                    errorHintTextUpdateView.setText("This number already exists");
                    updateHotlineButton.setEnabled(false);

                    updateHotlineButton.animate()
                            .alpha(0.5f)
                            .setDuration(200)
                            .start();
                } else {
                    errorHintTextUpdateView.setVisibility(View.INVISIBLE);
                    updateHotlineButton.setEnabled(true);

                    updateHotlineButton.animate()
                            .alpha(1.0f)
                            .setDuration(200)
                            .start();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorHintTextUpdateView.setText("Database error occurred");
                updateHotlineButton.setEnabled(true);

                updateHotlineButton.animate()
                        .alpha(1.0f)
                        .setDuration(200)
                        .start();
            }
        });
    }

    private void setupRecyclerView() {
        hotlineAdapter = new HotlineAdapter(hotlineList, hotlineRef, updateHotlineNumberBody, addHotlinePolice);
        responderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        responderRecyclerView.setAdapter(hotlineAdapter);

        hotlineAdapter.setOnHotlineSelectedListener(hotline -> {
            selectedHotline = hotline;
            enterNumberUpdateEditText.setText(hotline.getNumber());
        });
    }

    private void setupClickListeners() {
        ImageView backButton = findViewById(R.id.update_hotline_police_back);
        backButton.setOnClickListener(v -> finish());

        addHotlineButton.setOnClickListener(v -> validateAndAddHotline());

        updateHotlineButton.setOnClickListener(v -> updateHotline());

        addNewHotlineNumberBody.setOnClickListener(v -> hideAddHotlineNumberSection());
        addNewHotlineNumberContainer.setOnClickListener(v -> {/* don't hide if container is clicked */});
        addHotlinePolice.setOnClickListener(v -> showAddHotlineSection());

        updateHotlineNumberBody.setOnClickListener(v -> hideUpdateHotlineNumberSection());
        updateHotlineNumberContainer.setOnClickListener(v -> {/* don't hide if container is clicked */});
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

                        Toast.makeText(UpdateHotlinePoliceFragment.this,
                                "Hotline added successfully", Toast.LENGTH_SHORT).show();

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(enterNumberEditText.getWindowToken(), 0);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(UpdateHotlinePoliceFragment.this,
                                    "Failed to add number", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateHotline() {
        String number = enterNumberUpdateEditText.getText().toString().trim();

        if (number.isEmpty()) {
            errorHintTextUpdateView.setVisibility(View.VISIBLE);
            errorHintTextUpdateView.setText("Please enter a number");
            return;
        }

        if (errorHintTextUpdateView.getVisibility() == View.VISIBLE) {
            return;
        }

        if (selectedHotline != null) {
            selectedHotline.setNumber(number);
            hotlineRef.child(selectedHotline.getKey()).setValue(selectedHotline)
                    .addOnSuccessListener(aVoid -> {
                        int index = hotlineList.indexOf(selectedHotline);
                        if (index != -1) {
                            hotlineList.set(index, selectedHotline);
                            hotlineAdapter.notifyItemChanged(index);
                        }

                        enterNumberUpdateEditText.setText("");
                        hideUpdateHotlineNumberSection();

                        Toast.makeText(UpdateHotlinePoliceFragment.this,
                                "Hotline updated successfully", Toast.LENGTH_SHORT).show();

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(enterNumberUpdateEditText.getWindowToken(), 0);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(UpdateHotlinePoliceFragment.this,
                                    "Failed to update number", Toast.LENGTH_SHORT).show());
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

    private void hideUpdateHotlineNumberSection() {
        Log.d("UpdateHotline", "hideUpdateHotlineNumberSection called");
        Animation fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d("UpdateHotline", "Animation started");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("UpdateHotline", "Animation ended");
                updateHotlineNumberBody.setVisibility(View.GONE);
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
        updateHotlineNumberBody.startAnimation(fadeOut);
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
