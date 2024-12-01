package com.example.hellorescue.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.hellorescue.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class AdminPoliceActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Declare variables for map and other UI components
    private MapView mapView;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_police);

        // Initialize map view and toolbar
        mapView = findViewById(R.id.mapView_police);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);  // This will call onMapReady() once the map is ready
        }

        navigationView = findViewById(R.id.navview);
        drawerLayout = findViewById(R.id.police_drawer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle(null);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        TextView rescueTextView = findViewById(R.id.rescue);

        String helloText = "Hello ";
        String rescueText = "Rescue";
        SpannableString spannableString = new SpannableString(helloText + rescueText);

        spannableString.setSpan(
                new ForegroundColorSpan(Color.BLACK),
                0,
                helloText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannableString.setSpan(
                new ForegroundColorSpan(Color.RED),
                helloText.length(),
                spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        rescueTextView.setText(spannableString);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        LatLng focusLocation = new LatLng(10.07991, 124.34261);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(focusLocation, 13));
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }
}
