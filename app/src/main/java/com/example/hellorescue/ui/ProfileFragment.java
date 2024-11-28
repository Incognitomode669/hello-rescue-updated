package com.example.hellorescue.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hellorescue.R;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        LinearLayout personalInformation = view.findViewById(R.id.personal_information);
        LinearLayout changePassword = view.findViewById(R.id.change_password);
        LinearLayout howToUse = view.findViewById(R.id.how_to_use);
        LinearLayout about = view.findViewById(R.id.about);
        LinearLayout faq = view.findViewById(R.id.faq);

        // Set click listener for PersonalInformation
        personalInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the PersonalInformationActivity
                Intent intent = new Intent(getActivity(), PersonalInformationFragment.class);
                startActivity(intent);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the PersonalInformationActivity
                Intent intent = new Intent(getActivity(), ChangePasswordFragment.class);
                startActivity(intent);
            }
        });

        // Set click listener for PersonalInformation
        howToUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the PersonalInformationActivity
                Intent intent = new Intent(getActivity(), ChangePasswordFragment.class);
                startActivity(intent);
            }
        });

        howToUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the PersonalInformationActivity
                Intent intent = new Intent(getActivity(), HowToUseFragment.class);
                startActivity(intent);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the PersonalInformationActivity
                Intent intent = new Intent(getActivity(), AboutUsFragment.class);
                startActivity(intent);
            }
        });

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the PersonalInformationActivity
                Intent intent = new Intent(getActivity(), FAQFragment.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
