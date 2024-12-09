package com.example.hellorescue.responderpolice;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import androidx.fragment.app.Fragment;
import com.example.hellorescue.R;

public class TypeOfIncidentPoliceFragment extends Fragment {

    private CheckBox vehicularAccidentCheckbox;
    private RelativeLayout vehicularAccidentContainer;

    public TypeOfIncidentPoliceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.type_of_incident_filter_police, container, false);

        // Initialize views
        vehicularAccidentCheckbox = view.findViewById(R.id.vehicularAccidentCheckbox);
        vehicularAccidentContainer = view.findViewById(R.id.VehicularAccidentContainer);

        // Set an OnClickListener for the container
        vehicularAccidentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("ClickTest", "VehicularAccidentContainer clicked");
                // Toggle the checkbox state when the container is clicked
                vehicularAccidentCheckbox.setChecked(!vehicularAccidentCheckbox.isChecked());
            }
        });

        return view;
    }
}
