package com.example.hellorescue.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.hellorescue.R;

public class SafetyTipsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_safetytips, container, false);


        ImageButton backButton = view.findViewById(R.id.SafetyTips_back);
        ImageButton floodButton = view.findViewById(R.id.imageButton_Flood);
        ImageButton typhoonButton = view.findViewById(R.id.imageButton_Typhoon);
        ImageButton landslideButton = view.findViewById(R.id.imageButton_Landslide);
        ImageButton earthquakeButton = view.findViewById(R.id.imageButton_Earthquake);
        ImageButton heartattackButton = view.findViewById(R.id.imageButton_HeartAttack);
        ImageButton poisoncontaminationButton = view.findViewById(R.id.imageButton_PoisonContamination);
        ImageButton injuryButton = view.findViewById(R.id.imageButton_Injury);


        // Set an OnClickListener on the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the NavController and navigate back to ServicesFragment
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigateUp();
            }
        });

        floodButton.setOnClickListener(v -> {

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_safetytips_to_flood);
        });

        typhoonButton.setOnClickListener(v -> {

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_safetytips_to_typhoon);
        });


        landslideButton.setOnClickListener(v -> {

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_safetytips_to_landslide);
        });

        earthquakeButton.setOnClickListener(v -> {

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_safetytips_to_earthquake);
        });

        heartattackButton.setOnClickListener(v -> {

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_safetytips_to_heartattack);
        });



        poisoncontaminationButton.setOnClickListener(v -> {

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_safetytips_to_poisoncontamination);
        });

        injuryButton.setOnClickListener(v -> {

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_safetytips_to_injury);
        });



        return view;
    }
}
