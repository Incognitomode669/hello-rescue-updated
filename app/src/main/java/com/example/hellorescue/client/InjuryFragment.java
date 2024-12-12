package com.example.hellorescue.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.hellorescue.R;

public class InjuryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.injury, container, false);

        final Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce);

        ImageButton backButton = view.findViewById(R.id.injury_back);
        ImageButton beforeInjuryIcon = view.findViewById(R.id.beforeInjuryIcon);
        ImageButton duringInjuryIcon = view.findViewById(R.id.duringInjuryIcon);
        ImageButton afterInjuryIcon = view.findViewById(R.id.afterInjuryIcon);
        LinearLayout beforeInjuryButton = view.findViewById(R.id.beforeInjury);
        LinearLayout duringInjuryButton = view.findViewById(R.id.duringInjury);
        LinearLayout afterInjuryButton = view.findViewById(R.id.afterInjury);

        // Set an OnClickListener for the back button
        backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigateUp();
        });


        View.OnClickListener beforeInjuryClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_injury_to_beforeInjury);
        };



        beforeInjuryButton.setOnClickListener(beforeInjuryClickListener);
        beforeInjuryIcon.setOnClickListener(beforeInjuryClickListener);


        View.OnClickListener duringInjuryClickListener = v -> {
            duringInjuryIcon.startAnimation(animation);
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_injury_to_duringInjury);
        };

        duringInjuryButton.setOnClickListener(duringInjuryClickListener);
        duringInjuryIcon.setOnClickListener(duringInjuryClickListener);

        View.OnClickListener afterInjuryClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_injury_to_afterInjury);
        };

        afterInjuryButton.setOnClickListener(afterInjuryClickListener);
        afterInjuryIcon.setOnClickListener(afterInjuryClickListener);

        return view;
    }
}
