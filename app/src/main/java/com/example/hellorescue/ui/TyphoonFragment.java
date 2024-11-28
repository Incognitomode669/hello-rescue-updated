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

public class TyphoonFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.typhoon, container, false);

        // Find the views
        ImageButton backButton = view.findViewById(R.id.typhoon_back);
        ImageButton beforeTyphoonIcon = view.findViewById(R.id.beforeTyphoonIcon);
        ImageButton duringTyphoonIcon = view.findViewById(R.id.duringTyphoonIcon);
        ImageButton afterTyphoonIcon = view.findViewById(R.id.afterTyphoonIcon);
        LinearLayout beforeTyphoonButton = view.findViewById(R.id.beforeTyphoon);
        LinearLayout duringTyphoonButton = view.findViewById(R.id.duringTyphoon);
        LinearLayout afterTyphoonButton = view.findViewById(R.id.afterTyphoon);

        // Set an OnClickListener for the back button
        backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigateUp();
        });


        View.OnClickListener beforeTyphoonClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_typhoon_to_beforeTyphoon);
        };


        beforeTyphoonButton.setOnClickListener(beforeTyphoonClickListener);
        beforeTyphoonIcon.setOnClickListener(beforeTyphoonClickListener);


        View.OnClickListener duringTyphoonClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_typhoon_to_duringTyphoon);
        };

        duringTyphoonButton.setOnClickListener(beforeTyphoonClickListener);
        duringTyphoonIcon.setOnClickListener(beforeTyphoonClickListener);

        View.OnClickListener afterTyphoonClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_typhoon_to_afterTyphoon);
        };

        afterTyphoonButton.setOnClickListener(afterTyphoonClickListener);
        afterTyphoonIcon.setOnClickListener(afterTyphoonClickListener);

        return view;
    }
}
