package com.example.hellorescue.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.hellorescue.R;

public class ServicesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);



        // Find the ImageButtons
        ImageButton imageButtonTips = view.findViewById(R.id.imageButton_Tips);
        ImageButton imageFacilities = view.findViewById(R.id.imageButton_Facilities);


        imageButtonTips.setOnClickListener(v -> {


            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_servicesFragment_to_navigation_safetytips);
        });



        imageFacilities.setOnClickListener(v -> {


            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_servicesFragment_to_navigation_facilities);
        });

        return view;
    }
}
