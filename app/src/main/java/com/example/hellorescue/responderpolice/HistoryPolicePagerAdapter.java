package com.example.hellorescue.responderpolice;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HistoryPolicePagerAdapter extends FragmentStateAdapter {

    public HistoryPolicePagerAdapter(@NonNull HistoryPoliceFragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a fragment for "Today" or "All"
        if (position == 0) {
            return new TypeofIncidentFilterFragment();
        } else {
            return new DateFilterFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Two pages: Today and All
    }
}
