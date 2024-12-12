package com.example.hellorescue.lgu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.hellorescue.R;
import com.google.android.gms.maps.MapView;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class AdminLguActivity extends AppCompatActivity{

    // Declare variables for map and other UI components
    private MapView mapView;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lgu_admin);



        navigationView = findViewById(R.id.navview);
        drawerLayout = findViewById(R.id.lgu_drawer);

        toolbar = findViewById(R.id.toolbar_lgu);
        setSupportActionBar(toolbar);

        LinearLayout AccountInformation = findViewById(R.id.change_password_username_lgu);
        LinearLayout AddResponder = findViewById(R.id.add_responder);
        LinearLayout SignOut = findViewById(R.id.sign_out_lgu);





        AccountInformation.setOnClickListener(v -> {
            Intent intent = new Intent(com.example.hellorescue.lgu.AdminLguActivity.this, AccountInformationLguFragment.class);
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






}
