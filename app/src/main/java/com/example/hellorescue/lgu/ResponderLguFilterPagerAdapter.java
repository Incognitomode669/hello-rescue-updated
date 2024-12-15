package com.example.hellorescue.lgu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ResponderLguFilterPagerAdapter extends FragmentStateAdapter {

    public ResponderLguFilterPagerAdapter(@NonNull AdminLguActivity fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Only return the TypeofIncidentFilterPoliceFragment
        return new ResponderTypeFilterLguFragment();
    }

    @Override
    public int getItemCount() {
        return 1; // Only one page now
    }
}
