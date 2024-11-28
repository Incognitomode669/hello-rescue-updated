package com.example.hellorescue;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import com.example.hellorescue.databinding.ActivityMainBinding;
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem;

public class Main_Navigation extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int activeIndex = savedInstanceState != null ? savedInstanceState.getInt("activeIndex") : 2;

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_services,
                R.id.navigation_notifications,
                R.id.navigation_hotline,
                R.id.navigation_profile
        ).build();

        //  bottom navigation menu items
        CbnMenuItem[] menuItems = new CbnMenuItem[]{
                new CbnMenuItem(R.drawable.ic_services, R.drawable.avd_services, R.id.navigation_services),
                new CbnMenuItem(R.drawable.ic_notification, R.drawable.avd_notification, R.id.navigation_notifications),
                new CbnMenuItem(R.drawable.ic_home, R.drawable.avd_home, R.id.navigation_home),
                new CbnMenuItem(R.drawable.ic_call, R.drawable.avd_call, R.id.navigation_hotline),
                new CbnMenuItem(R.drawable.ic_profile, R.drawable.avd_profile, R.id.navigation_profile)
        };

        binding.navView.setMenuItems(menuItems, activeIndex);
        binding.navView.setupWithNavController(navController);

        //  colors for the navigation view
        binding.navView.setSelectedColor(Color.BLACK);
        binding.navView.setUnSelectedColor(Color.WHITE);
        binding.navView.setNavBackgroundColor(Color.parseColor("#FF5E57"));
        binding.navView.setFabBackgroundColor(Color.WHITE);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("activeIndex", binding.navView.getSelectedIndex());
        super.onSaveInstanceState(outState);
    }
}
