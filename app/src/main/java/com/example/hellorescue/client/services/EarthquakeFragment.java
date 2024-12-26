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

public class EarthquakeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.earthquake, container, false);

        // Find the views
        ImageButton backButton = view.findViewById(R.id.earthquake_back);
        ImageButton beforeEarthquakeIcon = view.findViewById(R.id.beforeEarthquakeIcon);
        ImageButton duringEarthquakeIcon = view.findViewById(R.id.duringEarthquakeIcon);
        ImageButton afterEarthquakeIcon = view.findViewById(R.id.afterEarthquakeIcon);
        LinearLayout beforeEarthquakeButton = view.findViewById(R.id.beforeEarthquake);
        LinearLayout duringEarthquakeButton = view.findViewById(R.id.duringEarthquake);
        LinearLayout afterEarthquakeButton = view.findViewById(R.id.afterEarthquake);

        // Set an OnClickListener for the back button
        backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigateUp();
        });


        View.OnClickListener beforeEarthquakeClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_earthquake_to_beforeEarthquake);
        };


        beforeEarthquakeButton.setOnClickListener(beforeEarthquakeClickListener);
        beforeEarthquakeIcon.setOnClickListener(beforeEarthquakeClickListener);


        View.OnClickListener duringEarthquakeClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_earthquake_to_duringEarthquake);
        };

        duringEarthquakeButton.setOnClickListener(duringEarthquakeClickListener);
        duringEarthquakeIcon.setOnClickListener(duringEarthquakeClickListener);

        View.OnClickListener afterEarthquakeClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_earthquake_to_afterEarthquake);
        };

        afterEarthquakeButton.setOnClickListener(afterEarthquakeClickListener);
        afterEarthquakeIcon.setOnClickListener(afterEarthquakeClickListener);

        return view;
    }
}
