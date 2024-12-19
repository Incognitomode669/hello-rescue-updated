package com.example.hellorescue.responderpolice;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.view.WindowCallbackWrapper;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hellorescue.R;
import com.google.firebase.database.DatabaseReference;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HotlineAdapter extends RecyclerView.Adapter<HotlineAdapter.HotlineViewHolder> {
    private List<Hotline> hotlineList;
    private DatabaseReference hotlineRef;
    private View updateHotlineNumberBody;
    private ImageView addHotlinePolice;
    private OnHotlineSelectedListener listener;
    private boolean areButtonsEnabled = true;

    public HotlineAdapter(List<Hotline> hotlineList, DatabaseReference hotlineRef, View updateHotlineNumberBody, ImageView addHotlinePolice) {
        this.hotlineList = hotlineList;
        this.hotlineRef = hotlineRef;
        this.updateHotlineNumberBody = updateHotlineNumberBody;
        this.addHotlinePolice = addHotlinePolice;
    }

    public void setOnHotlineSelectedListener(OnHotlineSelectedListener listener) {
        this.listener = listener;
    }

    public void setButtonsEnabled(boolean enabled) {
        this.areButtonsEnabled = enabled;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HotlineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hotline_list_item, parent, false);
        return new HotlineViewHolder(view);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull HotlineViewHolder holder, int position) {
        Hotline hotline = hotlineList.get(position);
        holder.numberTextView.setText(hotline.getNumber());
        holder.roleTextView.setText(hotline.getRole());

        // Set the enabled state of the buttons
        holder.deleteButton.setEnabled(areButtonsEnabled);
        holder.editButton.setEnabled(areButtonsEnabled);

        // Adjust alpha for visual feedback
        float alpha = areButtonsEnabled ? 1.0f : 0.5f;
        holder.deleteButton.setAlpha(alpha);
        holder.editButton.setAlpha(alpha);

        if (areButtonsEnabled) {

            holder.deleteButton.setOnClickListener(v -> {
                holder.deleteButton.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.button_scale));

                String hotlineKey = hotline.getKey();
                if (hotlineKey != null) {
                    // Create custom dialog
                    Dialog customDialog = new Dialog(holder.itemView.getContext());
                    customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    customDialog.setContentView(R.layout.delete_confirmation_dialog);
                    customDialog.setCanceledOnTouchOutside(false); // Changed to false

                    // Set OnCancelListener for touch outside
                    customDialog.setOnCancelListener(dialog -> {
                        View dialogContainer = customDialog.findViewById(R.id.main_container);
                        dialogContainer.animate()
                                .translationY(2000)
                                .setDuration(200)
                                .withEndAction(() -> customDialog.dismiss())
                                .start();
                    });

                    // Override the back button press
                    customDialog.setOnKeyListener((dialog, keyCode, event) -> {
                        if (keyCode == android.view.KeyEvent.KEYCODE_BACK && event.getAction() == android.view.KeyEvent.ACTION_UP) {
                            View dialogContainer = customDialog.findViewById(R.id.main_container);
                            dialogContainer.animate()
                                    .translationY(2000)
                                    .setDuration(200)
                                    .withEndAction(() -> customDialog.dismiss())
                                    .start();
                            return true;
                        }
                        return false;
                    });

                    // Set dialog window attributes
                    Window window = customDialog.getWindow();
                    if (window != null) {
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        window.setGravity(Gravity.BOTTOM);
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        // Add touch listener for outside touches
                        window.setCallback(new WindowCallbackWrapper(window.getCallback()) {
                            @Override
                            public boolean dispatchTouchEvent(MotionEvent event) {
                                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                    float dialogX = window.getDecorView().getX();
                                    float dialogY = window.getDecorView().getY();
                                    float dialogWidth = window.getDecorView().getWidth();
                                    float dialogHeight = window.getDecorView().getHeight();

                                    // Check if touch is outside dialog bounds
                                    if (event.getX() < dialogX || event.getX() > dialogX + dialogWidth ||
                                            event.getY() < dialogY || event.getY() > dialogY + dialogHeight) {
                                        View dialogContainer = customDialog.findViewById(R.id.main_container);
                                        dialogContainer.animate()
                                                .translationY(2000)
                                                .setDuration(200)
                                                .withEndAction(() -> customDialog.dismiss())
                                                .start();
                                        return true;
                                    }
                                }
                                return super.dispatchTouchEvent(event);
                            }
                        });
                    }

                    // Get views
                    AppCompatButton yesButton = customDialog.findViewById(R.id.yes);
                    AppCompatButton noButton = customDialog.findViewById(R.id.no);
                    TextView titleText = customDialog.findViewById(R.id.filter_text);
                    TextView messageText = customDialog.findViewById(R.id.textView12);

                    // Set texts
                    titleText.setText("Delete Hotline?");
                    messageText.setText("Are you sure you want to delete this hotline?");

                    // Show dialog with animation
                    customDialog.show();

                    // Initial setup for opening animation
                    View dialogContainer = customDialog.findViewById(R.id.main_container);
                    dialogContainer.setTranslationY(2000);
                    dialogContainer.animate()
                            .translationY(0)
                            .setDuration(200)
                            .start();

                    // Set click listeners with animations
                    yesButton.setOnClickListener(dialogView -> {
                        View dialogContainer1 = customDialog.findViewById(R.id.main_container);
                        dialogContainer1.animate()
                                .translationY(2000)
                                .setDuration(200)
                                .withEndAction(() -> {
                                    customDialog.dismiss();

                                    // Show loading dialog
                                    AlertDialog loadingDialog = new AlertDialog.Builder(holder.itemView.getContext())
                                            .setMessage("Deleting hotline...")
                                            .setCancelable(false)
                                            .create();
                                    loadingDialog.show();

                                    Handler timeoutHandler = new Handler();
                                    AtomicBoolean operationCompleted = new AtomicBoolean(false);

                                    final int deletePosition = holder.getAdapterPosition();

                                    Runnable timeoutRunnable = () -> {
                                        if (!operationCompleted.getAndSet(true)) {
                                            loadingDialog.dismiss();
                                            Toast.makeText(holder.itemView.getContext(),
                                                    "Operation timed out. Please check your connection and try again.",
                                                    Toast.LENGTH_SHORT).show();

                                            hotlineRef.child(hotlineKey).setValue(hotline)
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(holder.itemView.getContext(),
                                                                "Error restoring data: " + e.getMessage(),
                                                                Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    };

                                    timeoutHandler.postDelayed(timeoutRunnable, 10000);

                                    hotlineRef.child(hotlineKey).removeValue()
                                            .addOnSuccessListener(aVoid -> {
                                                if (!operationCompleted.getAndSet(true)) {
                                                    timeoutHandler.removeCallbacks(timeoutRunnable);
                                                    loadingDialog.dismiss();

                                                    if (deletePosition != RecyclerView.NO_POSITION && deletePosition < hotlineList.size()) {
                                                        hotlineList.remove(deletePosition);
                                                        notifyItemRemoved(deletePosition);
                                                        notifyItemRangeChanged(deletePosition, getItemCount());
                                                    }

                                                    Toast.makeText(holder.itemView.getContext(),
                                                            "Hotline deleted successfully",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                if (!operationCompleted.getAndSet(true)) {
                                                    timeoutHandler.removeCallbacks(timeoutRunnable);
                                                    loadingDialog.dismiss();

                                                    hotlineRef.child(hotlineKey).setValue(hotline)
                                                            .addOnFailureListener(restoreError -> {
                                                                Toast.makeText(holder.itemView.getContext(),
                                                                        "Error restoring data: " + restoreError.getMessage(),
                                                                        Toast.LENGTH_SHORT).show();
                                                            });

                                                    Toast.makeText(holder.itemView.getContext(),
                                                            "Failed to delete hotline: " + e.getMessage(),
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                })
                                .start();
                    });

                    noButton.setOnClickListener(dialogView -> {
                        View dialogContainer1 = customDialog.findViewById(R.id.main_container);
                        dialogContainer1.animate()
                                .translationY(2000)
                                .setDuration(200)
                                .withEndAction(() -> customDialog.dismiss())
                                .start();
                    });
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
}

@Override
public int getItemCount() {
    return hotlineList.size();
}

public interface OnHotlineSelectedListener {
    void onHotlineSelected(Hotline hotline);
}

public static class HotlineViewHolder extends RecyclerView.ViewHolder {
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
