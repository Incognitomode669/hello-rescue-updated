package com.example.hellorescue.client;

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

public class FloodFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flood, container, false);

        // Find the views
        ImageButton backButton = view.findViewById(R.id.flood_back);
        ImageButton beforeFloodIcon = view.findViewById(R.id.beforeFloodIcon);
        ImageButton duringFloodIcon = view.findViewById(R.id.duringFloodIcon);
        ImageButton afterFloodIcon = view.findViewById(R.id.afterFloodIcon);
        LinearLayout beforeFloodButton = view.findViewById(R.id.beforeFlood);
        LinearLayout duringFloodButton = view.findViewById(R.id.duringFlood);
        LinearLayout afterFloodButton = view.findViewById(R.id.afterFlood);

        // Set an OnClickListener for the back button
        backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigateUp();
        });


        View.OnClickListener beforeFloodClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_flood_to_beforeFlood);
        };


        beforeFloodButton.setOnClickListener(beforeFloodClickListener);
        beforeFloodIcon.setOnClickListener(beforeFloodClickListener);


        View.OnClickListener duringFloodClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_flood_to_duringFlood);
        };

        duringFloodButton.setOnClickListener(duringFloodClickListener);
        duringFloodIcon.setOnClickListener(duringFloodClickListener);

        View.OnClickListener afterFloodClickListener = v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_flood_to_afterFlood);
        };

        afterFloodButton.setOnClickListener(afterFloodClickListener);
        afterFloodIcon.setOnClickListener(afterFloodClickListener);

        return view;
    }
}
