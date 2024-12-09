package com.example.hellorescue.responderpolice;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hellorescue.R;

public class TypeofIncidentFilterFragment extends Fragment {

    private static final String TAG = "TypeofIncidentFilter";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.type_of_incident_filter_police, container, false);

        View VehicularAccidentContainer = view.findViewById(R.id.VehicularAccidentContainer);
        CheckBox VehicularAccidentCheckbox = view.findViewById(R.id.VehicularAccidentCheckbox);
        ImageButton VehicularAccidentIcon = view.findViewById(R.id.vehicularAccidentIcon);

        View DomesticViolenceContainer = view.findViewById(R.id.DomesticViolenceContainer);
        CheckBox DomesticViolenceCheckbox = view.findViewById(R.id.DomesticViolenceCheckbox);
        ImageButton DomesticViolenceIcon = view.findViewById(R.id.DomesticViolenceIcon);

        View RobberyAlarmContainer = view.findViewById(R.id.RobberyAlarmContainer);
        CheckBox RobberyAlarmCheckbox = view.findViewById(R.id.RobberyAlarmCheckbox);
        ImageButton RobberyAlarmIcon = view.findViewById(R.id.RobberyAlarmIcon);

        View TroubleAlarmContainer = view.findViewById(R.id.TroubleAlarmContainer);
        CheckBox TroubleAlarmCheckbox = view.findViewById(R.id.TroubleAlarmCheckbox);
        ImageButton TroubleAlarmIcon = view.findViewById(R.id.TroubleAlarmIcon);

        View ShootingAlarmContainer = view.findViewById(R.id.ShootingAlarmContainer);
        CheckBox ShootingAlarmCheckbox = view.findViewById(R.id.ShootingAlarmCheckbox);
        ImageButton ShootingAlarmIcon = view.findViewById(R.id.ShootingAlarmIcon);





        // Set OnClickListener for both VehicularAccidentContainer and VehicularAccidentIcon
        View.OnClickListener toggleVehicleCheckboxListener = v -> {
            Log.d(TAG, "Clicked: " + v.getId());

            // Toggle the CheckBox state
            if (VehicularAccidentCheckbox != null) {
                boolean currentState = VehicularAccidentCheckbox.isChecked();
                VehicularAccidentCheckbox.setChecked(!currentState);
                Log.d(TAG, "VehicularAccidentCheckbox toggled to: " + !currentState);
            }
        };

        // Apply the listener to both the container and the image button
        VehicularAccidentContainer.setOnClickListener(toggleVehicleCheckboxListener);
        VehicularAccidentIcon.setOnClickListener(toggleVehicleCheckboxListener);


      
        View.OnClickListener toggleDomesticCheckboxListener = v -> {
            Log.d(TAG, "Clicked: " + v.getId());

            // Toggle the CheckBox state
            if (DomesticViolenceCheckbox != null) {
                boolean currentState = DomesticViolenceCheckbox.isChecked();
                DomesticViolenceCheckbox.setChecked(!currentState);
                Log.d(TAG, "VehicularAccidentCheckbox toggled to: " + !currentState);
            }
        };

        // Apply the listener to both the container and the image button
        DomesticViolenceContainer.setOnClickListener(toggleDomesticCheckboxListener);
        DomesticViolenceIcon.setOnClickListener(toggleDomesticCheckboxListener);



        View.OnClickListener toggleRobberyCheckboxListener = v -> {
            Log.d(TAG, "Clicked: " + v.getId());

            // Toggle the CheckBox state
            if (RobberyAlarmCheckbox != null) {
                boolean currentState = RobberyAlarmCheckbox.isChecked();
                RobberyAlarmCheckbox.setChecked(!currentState);
                Log.d(TAG, "VehicularAccidentCheckbox toggled to: " + !currentState);
            }
        };

        // Apply the listener to both the container and the image button
        RobberyAlarmContainer.setOnClickListener(toggleRobberyCheckboxListener);
        RobberyAlarmIcon.setOnClickListener(toggleRobberyCheckboxListener);


        View.OnClickListener toggleTroubleCheckboxListener = v -> {
            Log.d(TAG, "Clicked: " + v.getId());

            // Toggle the CheckBox state
            if (TroubleAlarmCheckbox != null) {
                boolean currentState = TroubleAlarmCheckbox.isChecked();
                TroubleAlarmCheckbox.setChecked(!currentState);
                Log.d(TAG, "VehicularAccidentCheckbox toggled to: " + !currentState);
            }
        };

        // Apply the listener to both the container and the image button
        TroubleAlarmContainer.setOnClickListener(toggleTroubleCheckboxListener);
        TroubleAlarmIcon.setOnClickListener(toggleTroubleCheckboxListener);



        View.OnClickListener toggleShootingCheckboxListener = v -> {
            Log.d(TAG, "Clicked: " + v.getId());

            // Toggle the CheckBox state
            if (ShootingAlarmCheckbox != null) {
                boolean currentState = ShootingAlarmCheckbox.isChecked();
                ShootingAlarmCheckbox.setChecked(!currentState);
                Log.d(TAG, "VehicularAccidentCheckbox toggled to: " + !currentState);
            }
        };

        // Apply the listener to both the container and the image button
        ShootingAlarmContainer.setOnClickListener(toggleShootingCheckboxListener);
        ShootingAlarmIcon.setOnClickListener(toggleShootingCheckboxListener);







        return view;
    }
}
