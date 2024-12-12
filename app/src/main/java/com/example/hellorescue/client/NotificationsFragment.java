package com.example.hellorescue.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hellorescue.R;

public class NotificationsFragment extends Fragment {

    private TextView todayTextView;
    private TextView allTextView;
    private ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        todayTextView = view.findViewById(R.id.today);
        allTextView = view.findViewById(R.id.all);
        viewPager = view.findViewById(R.id.viewPager);

        // Set default background for "Today"
        todayTextView.setBackgroundResource(R.drawable.filter_clicked);
        allTextView.setBackgroundResource(R.drawable.filter_not_clicked);

        // Set the ViewPager adapter
        viewPager.setAdapter(new NotificationsPagerAdapter(this));

        // Set onClick listeners for the buttons
        todayTextView.setOnClickListener(v -> {
            // Change background of Today and All
            todayTextView.setBackgroundResource(R.drawable.filter_clicked);
            allTextView.setBackgroundResource(R.drawable.filter_not_clicked);

            // Move to the "Today" page
            viewPager.setCurrentItem(0);
        });

        allTextView.setOnClickListener(v -> {
            // Change background of All and Today
            allTextView.setBackgroundResource(R.drawable.filter_clicked);
            todayTextView.setBackgroundResource(R.drawable.filter_not_clicked);

            // Move to the "All" page
            viewPager.setCurrentItem(1);
        });

        // Add page change listener to update the background when the user swipes
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Update the background based on the selected page
                if (position == 0) {
                    todayTextView.setBackgroundResource(R.drawable.filter_clicked);
                    allTextView.setBackgroundResource(R.drawable.filter_not_clicked);
                } else {
                    allTextView.setBackgroundResource(R.drawable.filter_clicked);
                    todayTextView.setBackgroundResource(R.drawable.filter_not_clicked);
                }
            }
        });

        return view;
    }
}
