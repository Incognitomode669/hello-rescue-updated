package com.example.hellorescue.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class NotificationsPagerAdapter extends FragmentStateAdapter {

    public NotificationsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a fragment for "Today" or "All"
        if (position == 0) {
            return new TodayNotificationsFragment();
        } else {
            return new AllNotificationsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Two pages: Today and All
    }
}
