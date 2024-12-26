package com.example.hellorescue.responderpolice;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.hellorescue.R;
import com.example.hellorescue.client.submitreport_police.PoliceReport;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ResponderPoliceActivity extends AppCompatActivity implements OnMapReadyCallback {

    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    // Declare variables for map and other UI components
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.responder_police);

        // Initialize map view and toolbar
        mapView = findViewById(R.id.mapView_police);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);  // This will call onMapReady() once the map is ready
        }

        navigationView = findViewById(R.id.navview);
        drawerLayout = findViewById(R.id.police_drawer);

        toolbar = findViewById(R.id.toolbar_police);
        setSupportActionBar(toolbar);
        ImageButton rescueAlertIcon = findViewById(R.id.rescue_alert_icon);
        LinearLayout AccountInformation = findViewById(R.id.change_password_username_police);
        LinearLayout UpdateHotline = findViewById(R.id.update_hotline_police);
        LinearLayout History = findViewById(R.id.history_police);
        LinearLayout HowToUse = findViewById(R.id.how_to_use_police);
        LinearLayout About = findViewById(R.id.about_police);



        rescueAlertIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ResponderPoliceActivity.this, RescueAlertPoliceFragment.class);
            startActivity(intent);
        });

        AccountInformation.setOnClickListener(v -> {
            Intent intent = new Intent(ResponderPoliceActivity.this, AccountInformationPoliceFragment.class);
            startActivity(intent);
        });

        UpdateHotline.setOnClickListener(v -> {
            Intent intent = new Intent(ResponderPoliceActivity.this, UpdateHotlinePoliceFragment.class);
            startActivity(intent);
        });

        History.setOnClickListener(v -> {
            Intent intent = new Intent(ResponderPoliceActivity.this, HistoryPoliceFragment.class);
            startActivity(intent);
        });


        HowToUse.setOnClickListener(v -> {
            Intent intent = new Intent(ResponderPoliceActivity.this, HowToUsePoliceFragment.class);
            startActivity(intent);
        });

        About.setOnClickListener(v -> {
            Intent intent = new Intent(ResponderPoliceActivity.this, AboutPoliceFragment.class);
            startActivity(intent);
        });





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

        // Listen for new police reports
        listenForPoliceReports(googleMap);
    }

    private void listenForPoliceReports(GoogleMap map) {
        // Assuming you're using Firebase
        FirebaseDatabase.getInstance().getReference("police_reports")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        map.clear(); // Clear existing markers

                        for (DataSnapshot reportSnapshot : snapshot.getChildren()) {
                            PoliceReport report = reportSnapshot.getValue(PoliceReport.class);
                            if (report != null) {
                                LatLng position = new LatLng(report.getLatitude(), report.getLongitude());

                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(position)
                                        .title(report.getType())
                                        .snippet(report.getDescription());

                                map.addMarker(markerOptions);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ResponderPoliceActivity.this,
                                "Error loading reports: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
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
