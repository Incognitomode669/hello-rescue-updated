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

public class HeartAttackFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.heartattack, container, false);

        // Find the views
        ImageButton backButton = view.findViewById(R.id.injury_back);
        ImageButton beforeHeartAttackIcon = view.findViewById(R.id.beforeInjuryIcon);
        ImageButton duringHeartAttackIcon = view.findViewById(R.id.duringInjuryIcon);
        ImageButton afterHeartAttackIcon = view.findViewById(R.id.afterInjuryIcon);
        LinearLayout beforeHeartAttackButton = view.findViewById(R.id.beforeInjury);
        LinearLayout duringHeartAttackButton = view.findViewById(R.id.duringInjury);
        LinearLayout afterHeartAttackButton = view.findViewById(R.id.afterInjury);

        // Set an OnClickListener for the back button
        backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigateUp();
        });


        View.OnClickListener beforeHeartAttackClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_heartattack_to_beforeHeartAttack);
        };


        beforeHeartAttackButton.setOnClickListener(beforeHeartAttackClickListener);
        beforeHeartAttackIcon.setOnClickListener(beforeHeartAttackClickListener);


        View.OnClickListener duringHeartAttackClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_heartattack_to_duringHeartAttack);
        };

        duringHeartAttackButton.setOnClickListener(duringHeartAttackClickListener);
        duringHeartAttackIcon.setOnClickListener(duringHeartAttackClickListener);

        View.OnClickListener afterHeartAttackClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_heartattack_to_afterHeartAttack);
        };

        afterHeartAttackButton.setOnClickListener(afterHeartAttackClickListener);
        afterHeartAttackIcon.setOnClickListener(afterHeartAttackClickListener);

        return view;
    }
}
