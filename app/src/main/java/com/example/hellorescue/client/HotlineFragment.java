package com.example.hellorescue.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellorescue.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HotlineFragment extends Fragment {
    private RecyclerView recyclerView;
    private HotlineAdapter adapter;
    private DatabaseReference hotlinesRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotline, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.client_hotline_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HotlineAdapter(getContext());
        recyclerView.setAdapter(adapter);

        // Initialize Firebase
        hotlinesRef = FirebaseDatabase.getInstance().getReference().child("hotlines");
        loadHotlines();

        return view;
    }

    private void loadHotlines() {
        List<Hotline> hotlineList = new ArrayList<>();

        hotlinesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hotlineList.clear();
                for (DataSnapshot roleSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot hotlineSnapshot : roleSnapshot.getChildren()) {
                        Hotline hotline = hotlineSnapshot.getValue(Hotline.class);
                        if (hotline != null) {
                            // Make sure the role is set correctly from the parent node
                            hotline.setRole(roleSnapshot.getKey());
                            hotlineList.add(hotline);
                        }
                    }
                }
                adapter.setHotlines(hotlineList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error loading hotlines: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}