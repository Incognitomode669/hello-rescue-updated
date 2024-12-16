package com.example.hellorescue.responderpolice;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellorescue.R;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class HotlineAdapter extends RecyclerView.Adapter<HotlineAdapter.HotlineViewHolder> {
    private List<Hotline> hotlineList;
    private DatabaseReference hotlineRef;

    public HotlineAdapter(List<Hotline> hotlineList, DatabaseReference hotlineRef) {
        this.hotlineList = hotlineList;
        this.hotlineRef = hotlineRef;
    }

    @NonNull
    @Override
    public HotlineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hotline_list_item, parent, false);
        return new HotlineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotlineViewHolder holder, int position) {
        Hotline hotline = hotlineList.get(position);
        holder.numberTextView.setText(hotline.getNumber());
        holder.roleTextView.setText(hotline.getRole());

        holder.deleteButton.setOnClickListener(v -> {
            // Apply click animation
            holder.deleteButton.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.button_scale));

            String hotlineKey = hotline.getKey();
            if (hotlineKey != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete this hotline?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            hotlineRef.child(hotlineKey).removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(holder.itemView.getContext(), "Hotline deleted", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(holder.itemView.getContext(), "Failed to delete hotline", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }





    @Override
    public int getItemCount() {
        return hotlineList.size();
    }

    public class HotlineViewHolder extends RecyclerView.ViewHolder {
        TextView numberTextView;
        TextView roleTextView;
        ImageView deleteButton;


        public HotlineViewHolder(@NonNull View itemView) {
            super(itemView);
            numberTextView = itemView.findViewById(R.id.hotline_number);
            roleTextView = itemView.findViewById(R.id.text_role);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }





    }
}
