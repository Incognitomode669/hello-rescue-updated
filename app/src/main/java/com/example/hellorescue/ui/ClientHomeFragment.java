package com.example.hellorescue.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import android.widget.FrameLayout;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.widget.ImageView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.hellorescue.R;
import androidx.fragment.app.Fragment;

public class ClientHomeFragment extends Fragment {

    // Dropdown items for fire types
    String[] items_mdr = {"Mdr","Industrial Fires", "Residential Fires", "Forest and Grassland Fires", "Vehicle Fires", "Fireworks-Related Fires"};
    // Dropdown items for fire types
    String[] items_police = {"Vehicular Accident", "Domestic Violence ", "Trouble Alarm", "Robbery Alarm", "Shooting"};
    // Dropdown items for fire types
    String[] items_fire = {"Fire","Industrial Fires", "Residential Fires", "Forest and Grassland Fires", "Vehicle Fires", "Fireworks-Related Fires"};
    // Dropdown items for sex
    String[] sexOptions = {"M", "F"};
    private static final String TAG = "HomeFragment"; //  TAG for logging

    public ClientHomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        // Reference to the modal layout
        ConstraintLayout modalLayoutFire = view.findViewById(R.id.modal_layout_fire);




        // Dropdown setup for fire types
        AutoCompleteTextView autoCompleteTxt_fire = view.findViewById(R.id.auto_complete_txt_fire);
        ArrayAdapter<String> adapterItemsFire = new ArrayAdapter<>(getContext(), R.layout.list_item, items_fire);
        autoCompleteTxt_fire.setAdapter(adapterItemsFire);

        autoCompleteTxt_fire.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }
        });

        // Dropdown setup for police types
        AutoCompleteTextView autoCompleteTxt_police = view.findViewById(R.id.auto_complete_txt_police);
        ArrayAdapter<String> adapterItemsPolice = new ArrayAdapter<>(getContext(), R.layout.list_item, items_police);
        autoCompleteTxt_police.setAdapter(adapterItemsPolice);

        autoCompleteTxt_police.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }
        });

        // Dropdown setup for police types
        AutoCompleteTextView autoCompleteTxt_mdr = view.findViewById(R.id.auto_complete_txt_mdr);
        ArrayAdapter<String> adapterItemsMdr = new ArrayAdapter<>(getContext(), R.layout.list_item, items_mdr);
        autoCompleteTxt_mdr.setAdapter(adapterItemsMdr);

        autoCompleteTxt_mdr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }
        });



        // Dropdown setup for sex selection
        Spinner sexSpinner = view.findViewById(R.id.sex_spinner);
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sexOptions);
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexSpinner.setAdapter(sexAdapter);

        // TextView color setup for "Hello Rescue"
        TextView helloHomeTextView = view.findViewById(R.id.HLO_home);
        String text = "Hello Rescue";
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF5048")), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 6, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        helloHomeTextView.setText(spannableString);
        helloHomeTextView.setShadowLayer(6, 0, 2, Color.BLACK);

        // Modal Fire setup
        FrameLayout fireContainer = view.findViewById(R.id.fire_container);
        ConstraintLayout modalFire = view.findViewById(R.id.modal_fire);
        modalFire.setVisibility(View.GONE);

        // Modal Police setup
        FrameLayout policeContainer = view.findViewById(R.id.police_container);
        ConstraintLayout modalPolice = view.findViewById(R.id.modal_police);
        modalPolice.setVisibility(View.GONE);

        // Modal Mdr setup
        FrameLayout mdrContainer = view.findViewById(R.id.mdr_container);
        ConstraintLayout modalMdr = view.findViewById(R.id.modal_mdr);
        modalMdr.setVisibility(View.GONE);

        // Don't show modal when icon is clicked
        ImageView fireCornerIcon = view.findViewById(R.id.fire_corner_icon);
        fireCornerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do nothing when fireCornerIcon is clicked
            }
        });

        // Show modalFire with fade-in animation on fireContainer click, excluding fireCornerIcon
        fireContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modalFire.getVisibility() != View.VISIBLE) {
                    Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    modalFire.startAnimation(fadeIn);
                    modalFire.setVisibility(View.VISIBLE);
                }
            }
        });

        // Don't show modal when icon is clicked
        ImageView policeCornerIcon = view.findViewById(R.id.police_corner_icon);
        policeCornerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do nothing when policeCornerIcon is clicked
            }
        });

        // Show modalPolice with fade-in animation on policeContainer click, excluding policeCornerIcon
        policeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modalPolice.getVisibility() != View.VISIBLE) {
                    Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    modalPolice.startAnimation(fadeIn);
                    modalPolice.setVisibility(View.VISIBLE);
                }
            }
        });

        // Don't show modal when icon is clicked
        ImageView mdrCornerIcon = view.findViewById(R.id.mdr_corner_icon);
        mdrCornerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do nothing when mdrCornerIcon is clicked
            }
        });

        // Show modalMdr with fade-in animation on mdrContainer click, excluding mdrCornerIcon
        mdrContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modalMdr.getVisibility() != View.VISIBLE) {
                    Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    modalMdr.startAnimation(fadeIn);
                    modalMdr.setVisibility(View.VISIBLE);
                }
            }
        });

        // Hide modal fire with fade-out animation when clicking outside modalLayoutFire
        modalFire.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Rect rect = new Rect();
                    modalLayoutFire.getGlobalVisibleRect(rect);

                    if (!rect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
                        modalFire.startAnimation(fadeOut);
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {}

                            @SuppressLint("ClickableViewAccessibility")
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                modalFire.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });
                        return true;
                    }
                }
                return false;
            }
        });

        // Hide modal police with fade-out animation when clicking outside modalLayoutPolice
        modalPolice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Rect rect = new Rect();
                    modalLayoutFire.getGlobalVisibleRect(rect);

                    if (!rect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
                        modalPolice.startAnimation(fadeOut);
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {}

                            @SuppressLint("ClickableViewAccessibility")
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                modalPolice.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });
                        return true;
                    }
                }
                return false;
            }
        });

        // Hide modal mdr with fade-out animation when clicking outside modalLayoutMdr
        modalMdr.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Rect rect = new Rect();
                    modalLayoutFire.getGlobalVisibleRect(rect);

                    if (!rect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
                        modalMdr.startAnimation(fadeOut);
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {}

                            @SuppressLint("ClickableViewAccessibility")
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                modalMdr.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });
                        return true;
                    }
                }
                return false;
            }
        });

        return view;
    }
}
