package com.example.hellorescue.responderpolice;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
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
    private View updateHotlineNumberBody;
    private ImageView addHotlinePolice;
    private OnHotlineSelectedListener listener;

    public HotlineAdapter(List<Hotline> hotlineList, DatabaseReference hotlineRef, View updateHotlineNumberBody, ImageView addHotlinePolice) {
        this.hotlineList = hotlineList;
        this.hotlineRef = hotlineRef;
        this.updateHotlineNumberBody = updateHotlineNumberBody;
        this.addHotlinePolice = addHotlinePolice;
    }

    public void setOnHotlineSelectedListener(OnHotlineSelectedListener listener) {
        this.listener = listener;
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

        holder.editButton.setOnClickListener(v -> {
            holder.editButton.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.button_scale));

            updateHotlineNumberBody.setVisibility(View.VISIBLE);
            Animation fadeIn = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            updateHotlineNumberBody.startAnimation(fadeIn);

            addHotlinePolice.setEnabled(false);
            addHotlinePolice.animate()
                    .alpha(0.5f)
                    .setDuration(200)
                    .start();

            if (listener != null) {
                listener.onHotlineSelected(hotline);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotlineList.size();
    }

    public interface OnHotlineSelectedListener {
        void onHotlineSelected(Hotline hotline);
    }

    public class HotlineViewHolder extends RecyclerView.ViewHolder {
        TextView numberTextView;
        TextView roleTextView;
        ImageView deleteButton;
        ImageView editButton;

        public HotlineViewHolder(@NonNull View itemView) {
            super(itemView);
            numberTextView = itemView.findViewById(R.id.hotline_number);
            roleTextView = itemView.findViewById(R.id.text_role);
            deleteButton = itemView.findViewById(R.id.button_delete);
            editButton = itemView.findViewById(R.id.button_edit);
        }
    }
}
