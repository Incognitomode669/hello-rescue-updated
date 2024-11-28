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

public class PoisonousContaminationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.poisoncontamination, container, false);

        // Find the views
        ImageButton backButton = view.findViewById(R.id.poisonouscontamination_back);
        ImageButton beforePoisonousContaminationIcon = view.findViewById(R.id.beforePoisonousContaminationIcon);
        ImageButton duringPoisonousContaminationIcon = view.findViewById(R.id.duringPoisonousContaminationIcon);
        ImageButton afterPoisonousContaminationIcon = view.findViewById(R.id.afterPoisonousContaminationIcon);
        LinearLayout beforePoisonousContaminationButton = view.findViewById(R.id.beforePoisonousContamination);
        LinearLayout duringPoisonousContaminationButton = view.findViewById(R.id.duringPoisonousContamination);
        LinearLayout afterPoisonousContaminationButton = view.findViewById(R.id.afterPoisonousContamination);

        // Set an OnClickListener for the back button
        backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigateUp();
        });


        View.OnClickListener beforePoisonousContaminationClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_poisonouscontamination_to_beforePoisonContamination);
        };


        beforePoisonousContaminationButton.setOnClickListener(beforePoisonousContaminationClickListener);
        beforePoisonousContaminationIcon.setOnClickListener(beforePoisonousContaminationClickListener);


        View.OnClickListener duringPoisonousContaminationClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_poisonouscontamination_to_duringPoisonContamination);
        };


        duringPoisonousContaminationButton.setOnClickListener(duringPoisonousContaminationClickListener);
        duringPoisonousContaminationIcon.setOnClickListener(duringPoisonousContaminationClickListener);

        View.OnClickListener afterPoisonousContaminationClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_poisonouscontamination_to_afterPoisonContamination);
        };

        afterPoisonousContaminationButton.setOnClickListener(afterPoisonousContaminationClickListener);
        afterPoisonousContaminationIcon.setOnClickListener(afterPoisonousContaminationClickListener);

        return view;
    }
}
