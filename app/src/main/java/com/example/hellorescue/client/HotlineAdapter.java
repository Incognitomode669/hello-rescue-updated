package com.example.hellorescue.client;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import java.util.Collections;
import java.util.List;

public class HotlineAdapter extends RecyclerView.Adapter<HotlineAdapter.HotlineViewHolder> {
    private List<Hotline> hotlineList;
    private Context context;

    public HotlineAdapter(Context context) {
        this.context = context;
        this.hotlineList = new ArrayList<>();
    }

    private static int getRolePriority(String role) {
        switch (role) {
            case "POLICE": return 0;
            case "FIRE": return 1;
            case "MDRRMO": return 2;
            default: return Integer.MAX_VALUE;
        }
    }

    @NonNull
    @Override
    public HotlineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.client_hotline_list, parent, false);
        return new HotlineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotlineViewHolder holder, int position) {
        Hotline hotline = hotlineList.get(position);
        holder.textRole.setText(hotline.getRole());
        holder.hotlineNumber.setText(hotline.getNumber());

        Animation scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.button_scale);

        holder.callResponderButton.setOnClickListener(v -> {
            // Start the scale animation
            holder.callResponderButton.startAnimation(scaleAnimation);

            // Set animation listener to start the call after animation ends
            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Start the call intent after animation ends
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + hotline.getNumber()));
                    context.startActivity(intent);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });
    }

    @Override
    public int getItemCount() {
        return hotlineList.size();
    }

    public void setHotlines(List<Hotline> hotlines) {
        List<Hotline> sortedHotlines = new ArrayList<>(hotlines);
        Collections.sort(sortedHotlines, (h1, h2) -> {
            int priority1 = getRolePriority(h1.getRole());
            int priority2 = getRolePriority(h2.getRole());
            return Integer.compare(priority1, priority2);
        });

        this.hotlineList = sortedHotlines;
        notifyDataSetChanged();
    }

    static class HotlineViewHolder extends RecyclerView.ViewHolder {
        TextView textRole, hotlineNumber;
        ImageView callResponderButton;

        public HotlineViewHolder(@NonNull View itemView) {
            super(itemView);
            textRole = itemView.findViewById(R.id.text_role);
            hotlineNumber = itemView.findViewById(R.id.hotline_number);
            callResponderButton = itemView.findViewById(R.id.call_responder_button);
        }
    }
}