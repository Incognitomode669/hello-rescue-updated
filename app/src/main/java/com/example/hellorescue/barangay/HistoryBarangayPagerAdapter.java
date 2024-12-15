package com.example.hellorescue.barangay;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HistoryBarangayPagerAdapter extends FragmentStateAdapter {

    public HistoryBarangayPagerAdapter(@NonNull HistoryBarangayFragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a fragment for "Today" or "All"
        if (position == 0) {
            return new TypeofIncidentFilterBarangayFragment();
        } else {
            return new DateFilterBarangayFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Two pages: Today and All
    }
}
