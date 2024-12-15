package com.example.hellorescue.lgu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellorescue.R;

import java.util.ArrayList;
import java.util.List;

public class ResponderAdapter extends RecyclerView.Adapter<ResponderAdapter.ResponderViewHolder> {
    private List<Responder> allResponders;
    private List<Responder> filteredResponders;
    private Context context;
    private OnResponderClickListener listener;
    private Animation clickAnimation;

    // Filtering flags
    private boolean showPolice = false;
    private boolean showMDRRMO = false;
    private boolean showFire = false;
    private boolean showBarangay = false;

    public ResponderAdapter(Context context, OnResponderClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.allResponders = new ArrayList<>();
        this.filteredResponders = new ArrayList<>();

        // Load the click animation
        clickAnimation = AnimationUtils.loadAnimation(context, R.anim.button_scale);
    }

    public void setResponders(List<Responder> responders) {
        this.allResponders = responders;
        applyFilters();
    }

    // Methods to toggle filters
    public void setPoliceFilter(boolean show) {
        showPolice = show;
        applyFilters();
    }

    public void setMDRRMOFilter(boolean show) {
        showMDRRMO = show;
        applyFilters();
    }

    public void setFireFilter(boolean show) {
        showFire = show;
        applyFilters();
    }

    public void setBarangayFilter(boolean show) {
        showBarangay = show;
        applyFilters();
    }

    private void applyFilters() {
        filteredResponders.clear();

        // If no filters are active, show all responders
        if (!showPolice && !showMDRRMO && !showFire && !showBarangay) {
            filteredResponders.addAll(allResponders);
            notifyDataSetChanged();
            return;
        }

        // Otherwise, apply the active filters
        for (Responder responder : allResponders) {
            switch (responder.getRole().toUpperCase()) {
                case "POLICE":
                    if (showPolice) filteredResponders.add(responder);
                    break;
                case "MDRRMO":
                    if (showMDRRMO) filteredResponders.add(responder);
                    break;
                case "FIRE":
                    if (showFire) filteredResponders.add(responder);
                    break;
                case "BARANGAY":
                    if (showBarangay) filteredResponders.add(responder);
                    break;
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResponderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.responder_list_item, parent, false);
        return new ResponderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResponderViewHolder holder, int position) {
        Responder responder = filteredResponders.get(position);
        holder.bind(responder);
    }

    @Override
    public int getItemCount() {
        return filteredResponders.size();
    }

    public interface OnResponderClickListener {
        void onEditClick(Responder responder);

        void onDeleteClick(Responder responder);
    }

    class ResponderViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, roleText;
        ImageView editButton, deleteButton, roleIcon;

        ResponderViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.text_username);
            roleText = itemView.findViewById(R.id.text_role);
            editButton = itemView.findViewById(R.id.button_edit);
            deleteButton = itemView.findViewById(R.id.button_delete);
            roleIcon = itemView.findViewById(R.id.role_icon);
        }

        void bind(Responder responder) {
            usernameText.setText(responder.getUsername());
            roleText.setText(responder.getRole());

            // Set the appropriate icon based on role
            switch (responder.getRole().toUpperCase()) {
                case "POLICE":
                    roleIcon.setImageResource(R.drawable.police_icon);
                    break;
                case "MDRRMO":
                    roleIcon.setImageResource(R.drawable.mdr_icon);
                    break;
                case "FIRE":
                    roleIcon.setImageResource(R.drawable.fire_icon);
                    break;
                case "BARANGAY":
                    roleIcon.setImageResource(R.drawable.barangay_icon);
                    break;
                default:
                    roleIcon.setImageResource(R.drawable.default_icon);
                    break;
            }

            editButton.setOnClickListener(v -> {
                v.startAnimation(clickAnimation);
                clickAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // Animation started
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // Animation ended, perform the edit action
                        listener.onEditClick(responder);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // Animation repeated
                    }
                });
            });

            deleteButton.setOnClickListener(v -> {
                v.startAnimation(clickAnimation);
                clickAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // Animation started
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // Animation ended, perform the delete action
                        listener.onDeleteClick(responder);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // Animation repeated
                    }
                });
            });
        }
    }
}