package com.example.hellorescue.client.services;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.hellorescue.R;

public class FacilitiesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.emergency_facilities, container, false);

        // Find the back button
        ImageButton backButton = view.findViewById(R.id.facilities_back);


        LinearLayout PoliceMapView = view.findViewById(R.id.police_view_map);

        // Set an OnClickListener on the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the NavController and navigate back to ServicesFragment
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigateUp();
            }
        });

        View.OnClickListener PoliceMapViewClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_facilities_to_PoliceStation);
        };


        PoliceMapView.setOnClickListener(PoliceMapViewClickListener);


        return view;
    }
}
