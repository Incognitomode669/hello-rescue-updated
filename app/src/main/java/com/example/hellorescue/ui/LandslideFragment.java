package com.example.hellorescue.ui;

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

public class LandslideFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.landslide, container, false);

        // Find the views
        ImageButton backButton = view.findViewById(R.id.landslide_back);
        ImageButton beforeLandslideIcon = view.findViewById(R.id.beforeLandslideIcon);
        ImageButton duringLandslideIcon = view.findViewById(R.id.duringLandslideIcon);
        ImageButton afterLandslideIcon = view.findViewById(R.id.afterLandslideIcon);
        LinearLayout beforeLandslideButton = view.findViewById(R.id.beforeLandslide);
        LinearLayout duringLandslideButton = view.findViewById(R.id.duringLandslide);
        LinearLayout afterLandslideButton = view.findViewById(R.id.afterLandslide);

        // Set an OnClickListener for the back button
        backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigateUp();
        });


        View.OnClickListener beforeLandslideClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_landslide_to_beforeLandslide);
        };


        beforeLandslideButton.setOnClickListener(beforeLandslideClickListener);
        beforeLandslideIcon.setOnClickListener(beforeLandslideClickListener);


        View.OnClickListener duringLandslideClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_landslide_to_duringLandslide);
        };

        duringLandslideButton.setOnClickListener(duringLandslideClickListener);
        duringLandslideIcon.setOnClickListener(duringLandslideClickListener);

        View.OnClickListener afterLandslideClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_landslide_to_afterLandslide);
        };

        afterLandslideButton.setOnClickListener(afterLandslideClickListener);
        afterLandslideIcon.setOnClickListener(afterLandslideClickListener);

        return view;
    }
}
