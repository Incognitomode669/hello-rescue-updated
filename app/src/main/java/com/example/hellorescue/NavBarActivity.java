//package com.example.hellorescue;
//
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//
//import com.ismaeldivita.chipnavigation.ChipNavigationBar;
//
//public class NavBarActivity extends AppCompatActivity {
//
//    private ChipNavigationBar chipNavigationBar;
//    private boolean backPressedOnce = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_nav_bar);
//
//        chipNavigationBar = findViewById(R.id.bottom_nav_bar);
//        chipNavigationBar.setItemSelected(R.id.nav_home, true); // Set "Home" as default
//
//        // Load the home fragment initially
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, new HomeFragment())
//                .commit();
//
//        // Set up navigation item click listener
//        chipNavigationBar.setOnItemSelectedListener(itemId -> {
//            Fragment fragment;
//            if (itemId == R.id.nav_home) {
//                fragment = new HomeFragment();
//            } else if (itemId == R.id.nav_services) {
//                fragment = new ServicesFragment();
//            } else if (itemId == R.id.nav_notification) {
//                fragment = new NotificationFragment();
//            } else if (itemId == R.id.nav_profile) {
//                fragment = new ProfileFragment();
//            } else {
//                fragment = new HomeFragment(); // Default to home
//            }
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, fragment)
//                    .commit();
//        });
//    }
//
//    @Override
//    public void onBackPressed() {
//        // Get the currently displayed fragment
//        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//
//        // Check if the current fragment is NOT the home fragment
//        if (!(currentFragment instanceof HomeFragment)) {
//            // Navigate to the home fragment
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, new HomeFragment())
//                    .commit();
//            chipNavigationBar.setItemSelected(R.id.nav_home, true); // Set "Home" as selected
//        } else {
//            // If the back button was pressed twice within a short period, exit the app
//            if (backPressedOnce) {
//                super.onBackPressed();
//                return;
//            }
//
//            // Warn the user that pressing back again will exit
//            backPressedOnce = true;
//            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
//
//            // Reset the backPressedOnce flag after 2 seconds
//            new android.os.Handler().postDelayed(() -> backPressedOnce = false, 2000);
//        }
//    }
//}
