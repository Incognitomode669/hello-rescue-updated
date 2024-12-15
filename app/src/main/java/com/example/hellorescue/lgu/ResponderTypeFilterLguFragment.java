package com.example.hellorescue.lgu;

import android.content.Context;
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

public class ResponderTypeFilterLguFragment extends Fragment {
    private static final String TAG = "TypeofIncidentFilter";

    // Add interface for filter communication
    public interface FilterChangeListener {
        void onFilterChanged(String role, boolean isChecked);
    }

    private FilterChangeListener filterChangeListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FilterChangeListener) {
            filterChangeListener = (FilterChangeListener) context;
        } else {
            throw new RuntimeException(context + " must implement FilterChangeListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.types_of_responder_filter_lgu, container, false);

        View PNPContainer = view.findViewById(R.id.PNPContainer);
        CheckBox PNPCheckbox = view.findViewById(R.id.PNPCheckbox);
        ImageButton PNPIcon = view.findViewById(R.id.PNPIcon);

        View BFPContainer = view.findViewById(R.id.BFPContainer);
        CheckBox BFPCheckbox = view.findViewById(R.id.BFPCheckbox);
        ImageButton BFPIcon = view.findViewById(R.id.BFPIcon);

        View MDRRMOContainer = view.findViewById(R.id.MDRRMOContainer);
        CheckBox MDRRMOCheckbox = view.findViewById(R.id.MDRRMOCheckbox);
        ImageButton MDRRMOIcon = view.findViewById(R.id.MDRRMOIcon);

        View BARANGAYContainer = view.findViewById(R.id.BARANGAYContainer);
        CheckBox BARANGAYCheckbox = view.findViewById(R.id.BARANGAYCheckbox);
        ImageButton BARANGAYIcon = view.findViewById(R.id.BARANGAYIcon);

        // Updated click listeners with filter communication
        View.OnClickListener togglePNPCheckboxListener = v -> {
            if (PNPCheckbox != null) {
                boolean currentState = PNPCheckbox.isChecked();
                PNPCheckbox.setChecked(!currentState);
                filterChangeListener.onFilterChanged("POLICE", !currentState);
                Log.d(TAG, "Police filter toggled to: " + !currentState);
            }
        };

        View.OnClickListener toggleBFPCheckboxListener = v -> {
            if (BFPCheckbox != null) {
                boolean currentState = BFPCheckbox.isChecked();
                BFPCheckbox.setChecked(!currentState);
                filterChangeListener.onFilterChanged("FIRE", !currentState);
                Log.d(TAG, "Fire filter toggled to: " + !currentState);
            }
        };

        View.OnClickListener toggleMDRRMOCheckboxListener = v -> {
            if (MDRRMOCheckbox != null) {
                boolean currentState = MDRRMOCheckbox.isChecked();
                MDRRMOCheckbox.setChecked(!currentState);
                filterChangeListener.onFilterChanged("MDRRMO", !currentState);
                Log.d(TAG, "MDRRMO filter toggled to: " + !currentState);
            }
        };

        View.OnClickListener toggleBARANGAYCheckboxListener = v -> {
            if (BARANGAYCheckbox != null) {
                boolean currentState = BARANGAYCheckbox.isChecked();
                BARANGAYCheckbox.setChecked(!currentState);
                filterChangeListener.onFilterChanged("BARANGAY", !currentState);
                Log.d(TAG, "Barangay filter toggled to: " + !currentState);
            }
        };

        // Apply listeners to containers and icons
        PNPContainer.setOnClickListener(togglePNPCheckboxListener);
        PNPIcon.setOnClickListener(togglePNPCheckboxListener);

        BFPContainer.setOnClickListener(toggleBFPCheckboxListener);
        BFPIcon.setOnClickListener(toggleBFPCheckboxListener);

        MDRRMOContainer.setOnClickListener(toggleMDRRMOCheckboxListener);
        MDRRMOIcon.setOnClickListener(toggleMDRRMOCheckboxListener);

        BARANGAYContainer.setOnClickListener(toggleBARANGAYCheckboxListener);
        BARANGAYIcon.setOnClickListener(toggleBARANGAYCheckboxListener);

        return view;
    }
}
