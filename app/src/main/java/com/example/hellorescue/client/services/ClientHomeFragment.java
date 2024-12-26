
package com.example.hellorescue.client.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.ScrollView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.hellorescue.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClientHomeFragment extends Fragment {

    private static final String TAG = "ClientHomeFragment";
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE_FIRE = 101;
    private static final int CAMERA_REQUEST_CODE_POLICE = 102;
    private static final String FILE_PROVIDER_AUTHORITY = "com.example.hellorescue.fileprovider";

    // Arrays for dropdown menus
    private final String[] items_mdr = {"Mdr", "Industrial Fires", "Residential Fires", "Forest and Grassland Fires", "Vehicle Fires", "Fireworks-Related Fires"};
    private final String[] items_police = {"Vehicular Accident", "Domestic Violence ", "Trouble Alarm", "Robbery Alarm", "Shooting Alarm"};
    private final String[] items_fire = {"Fire", "Industrial Fires", "Residential Fires", "Forest and Grassland Fires", "Vehicle Fires", "Fireworks-Related Fires"};
    private final String[] sexOptions = {"M", "F"};

    // UI Components
    private ConstraintLayout currentOpenModal = null;
    private boolean isAnyModalOpen = false;
    private ScrollView scrollView;
    private AutoCompleteTextView autoCompleteTxtFire;
    private AutoCompleteTextView autoCompleteTxtPolice;
    private AutoCompleteTextView autoCompleteTxtMdr;
    private Spinner sexSpinner;
    private ImageButton imageButtonFire;
    private ImageButton imageButtonPolice;

    // Camera-related variables
    private Uri imageUriFire;
    private Uri imageUriPolice;

    public ClientHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // Retain the fragment instance across configuration changes
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            );
        }

        setupKeyboardAdjustment(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(view);
        setupCameraButtons();
        setupContainersAndModals(view);
        setupDropdowns(view);
        setupTextViews(view);
        setupCornerIcons(view);

        return view;
    }

    private void initializeViews(View view) {
        // Initialize ScrollView
        scrollView = view.findViewById(R.id.scrollable);

        // Initialize image buttons
        imageButtonFire = view.findViewById(R.id.image_button_fire);
        imageButtonPolice = view.findViewById(R.id.image_button_police);

        // Set corner radius for image buttons
        float cornerRadius = getResources().getDimension(R.dimen.image_corner_radius);
        imageButtonFire.setClipToOutline(true);
        imageButtonPolice.setClipToOutline(true);
        imageButtonFire.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), cornerRadius);
            }
        });
        imageButtonPolice.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), cornerRadius);
            }
        });
        imageButtonFire.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.rounded_image_background));
        imageButtonPolice.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.rounded_image_background));

        // Initialize form elements
        autoCompleteTxtFire = view.findViewById(R.id.auto_complete_txt_fire);
        autoCompleteTxtPolice = view.findViewById(R.id.auto_complete_txt_police);
        autoCompleteTxtMdr = view.findViewById(R.id.auto_complete_txt_mdr);
        sexSpinner = view.findViewById(R.id.sex_spinner);

        // Initially disable form elements
        setFormElementsEnabled(false);
    }

    private void setupCameraButtons() {
        imageButtonFire.setOnClickListener(v -> checkCameraPermission(CAMERA_REQUEST_CODE_FIRE));
        imageButtonPolice.setOnClickListener(v -> checkCameraPermission(CAMERA_REQUEST_CODE_POLICE));
    }

    private void setupContainersAndModals(View view) {
        // Get references to containers and modals
        FrameLayout fireContainer = view.findViewById(R.id.fire_container);
        FrameLayout policeContainer = view.findViewById(R.id.police_container);
        FrameLayout mdrContainer = view.findViewById(R.id.mdr_container);

        ConstraintLayout modalFire = view.findViewById(R.id.modal_fire);
        ConstraintLayout modalPolice = view.findViewById(R.id.modal_police);
        ConstraintLayout modalMdr = view.findViewById(R.id.modal_mdr);

        ConstraintLayout modalLayoutFire = view.findViewById(R.id.modal_layout_fire);
        ConstraintLayout modalLayoutPolice = view.findViewById(R.id.modal_layout_police);
        ConstraintLayout modalLayoutMdr = view.findViewById(R.id.modal_layout_mdr);

        // Initially hide all modals and ScrollView
        modalFire.setVisibility(View.GONE);
        modalPolice.setVisibility(View.GONE);
        modalMdr.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);

        // Setup click listeners
        View.OnClickListener showModalListener = v -> {
            if (!isAnyModalOpen) {
                ConstraintLayout modalToShow = null;
                if (v.getId() == R.id.fire_container) {
                    modalToShow = modalFire;
                } else if (v.getId() == R.id.police_container) {
                    modalToShow = modalPolice;
                } else if (v.getId() == R.id.mdr_container) {
                    modalToShow = modalMdr;
                }

                if (modalToShow != null) {
                    showModal(modalToShow);
                }
            }
        };

        fireContainer.setOnClickListener(showModalListener);
        policeContainer.setOnClickListener(showModalListener);
        mdrContainer.setOnClickListener(showModalListener);

        // Setup modal touch listeners
        setupModalTouchListener(modalFire, modalLayoutFire);
        setupModalTouchListener(modalPolice, modalLayoutPolice);
        setupModalTouchListener(modalMdr, modalLayoutMdr);
    }

    private void showModal(ConstraintLayout modal) {
        Animation modalFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        Animation scrollFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);

        modal.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.VISIBLE);

        modal.startAnimation(modalFadeIn);
        scrollView.startAnimation(scrollFadeIn);

        currentOpenModal = modal;
        isAnyModalOpen = true;
        setFormElementsEnabled(true);
    }

    private void checkCameraPermission(int requestCode) {
        if (getContext() == null) return;

        // Only check for camera permission as storage permission isn't needed for Android 10+
        boolean hasCameraPermission = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        if (!hasCameraPermission) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera(requestCode);
        }
    }
    private void openCamera(int requestCode) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getActivity() == null || cameraIntent.resolveActivity(requireActivity().getPackageManager()) == null) {
            Toast.makeText(getContext(), "Camera app not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the dimensions of the target ImageButton
        ImageButton targetButton = (requestCode == CAMERA_REQUEST_CODE_FIRE) ? imageButtonFire : imageButtonPolice;
        targetButton.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int width = targetButton.getMeasuredWidth();
        int height = targetButton.getMeasuredHeight();

        try {
            File photoFile = createImageFile(requestCode);
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireContext(),
                        FILE_PROVIDER_AUTHORITY,
                        photoFile);

                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                // Add size parameters to the intent
                cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1); // 1 for front camera, 0 for back camera
                cameraIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                cameraIntent.putExtra("android.intent.extras.TARGET_WIDTH", width);
                cameraIntent.putExtra("android.intent.extras.TARGET_HEIGHT", height);
                cameraIntent.putExtra("android.intent.extra.SIZE_LIMIT", width * height);

                if (requestCode == CAMERA_REQUEST_CODE_FIRE) {
                    imageUriFire = photoURI;
                } else {
                    imageUriPolice = photoURI;
                }

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, requestCode);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error opening camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private File createImageFile(int requestCode) {
        if (getContext() == null) return null;

        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String prefix = (requestCode == CAMERA_REQUEST_CODE_FIRE) ? "FIRE_" : "POLICE_";
            String imageFileName = prefix + timeStamp + "_";

            // Use app-specific directory which doesn't require storage permission
            File storageDir = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "HelloRescue");
            if (!storageDir.exists() && !storageDir.mkdirs()) {
                throw new IOException("Could not create directory");
            }

            return File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error creating image file", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera(imageUriFire == null ? CAMERA_REQUEST_CODE_FIRE : CAMERA_REQUEST_CODE_POLICE);
            } else {
                Toast.makeText(getContext(), "Camera permission is required", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            try {
                if (requestCode == CAMERA_REQUEST_CODE_FIRE && imageUriFire != null) {
                    imageButtonFire.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageButtonFire.setImageURI(null); // Clear the image first
                    imageButtonFire.setImageURI(imageUriFire);
                    imageButtonFire.setClipToOutline(true); // Ensure clipping is enabled after setting new image
                } else if (requestCode == CAMERA_REQUEST_CODE_POLICE && imageUriPolice != null) {
                    imageButtonPolice.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageButtonPolice.setImageURI(null); // Clear the image first
                    imageButtonPolice.setImageURI(imageUriPolice);
                    imageButtonPolice.setClipToOutline(true); // Ensure clipping is enabled after setting new image
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error loading captured image", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getContext(), "Camera capture cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupKeyboardAdjustment(View rootView) {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!isAnyModalOpen) return;

                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    View focusedView = rootView.findFocus();
                    if (focusedView != null && scrollView != null) {
                        int[] location = new int[2];
                        focusedView.getLocationInWindow(location);
                        scrollView.smoothScrollTo(0, location[1] - keypadHeight);
                    }
                }
            }
        });
    }

    private void setFormElementsEnabled(boolean enabled) {
        autoCompleteTxtFire.setEnabled(enabled);
        autoCompleteTxtPolice.setEnabled(enabled);
        autoCompleteTxtMdr.setEnabled(enabled);
        sexSpinner.setEnabled(enabled);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setupModalTouchListener(ConstraintLayout modal, ConstraintLayout modalLayout) {
        modal.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float x = event.getX();
                float y = event.getY();

                Rect modalContentBounds = new Rect();
                modalLayout.getGlobalVisibleRect(modalContentBounds);

                Rect modalBounds = new Rect();
                modal.getGlobalVisibleRect(modalBounds);

                int[] modalCoords = new int[2];
                modal.getLocationOnScreen(modalCoords);
                float globalX = x + modalCoords[0];
                float globalY = y + modalCoords[1];

                if (!modalContentBounds.contains((int) globalX, (int) globalY)) {
                    closeModal(modal);
                    return true;
                }
            }
            return false;
        });
    }

    private void closeModal(ConstraintLayout modal) {
        hideKeyboard();

        Animation modalFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        Animation scrollFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);

        modalFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                modal.setVisibility(View.GONE);
                currentOpenModal = null;
                isAnyModalOpen = false;
                setFormElementsEnabled(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        scrollFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                scrollView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        modal.startAnimation(modalFadeOut);
        scrollView.startAnimation(scrollFadeOut);
    }

    private void setupDropdowns(View view) {
        // Fire dropdown setup
        ArrayAdapter<String> adapterItemsFire = new ArrayAdapter<>(
                requireContext(),
                R.layout.list_item,
                items_fire
        );
        autoCompleteTxtFire.setAdapter(adapterItemsFire);
        autoCompleteTxtFire.setOnItemClickListener((parent, view1, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            // Handle fire item selection
        });

        // Police dropdown setup
        ArrayAdapter<String> adapterItemsPolice = new ArrayAdapter<>(
                requireContext(),
                R.layout.list_item,
                items_police
        );
        autoCompleteTxtPolice.setAdapter(adapterItemsPolice);
        autoCompleteTxtPolice.setOnItemClickListener((parent, view1, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            // Handle police item selection
        });

        // MDR dropdown setup
        ArrayAdapter<String> adapterItemsMdr = new ArrayAdapter<>(
                requireContext(),
                R.layout.list_item,
                items_mdr
        );
        autoCompleteTxtMdr.setAdapter(adapterItemsMdr);
        autoCompleteTxtMdr.setOnItemClickListener((parent, view1, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            // Handle MDR item selection
        });

        // Sex spinner setup
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                sexOptions
        );
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexSpinner.setAdapter(sexAdapter);
    }

    private void setupTextViews(View view) {
        TextView helloHomeTextView = view.findViewById(R.id.HLO_home);
        String text = "Hello Rescue";
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(
                new ForegroundColorSpan(Color.parseColor("#FF5048")),
                0,
                5,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        spannableString.setSpan(
                new ForegroundColorSpan(Color.BLACK),
                6,
                12,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        helloHomeTextView.setText(spannableString);
        helloHomeTextView.setShadowLayer(6, 0, 2, Color.BLACK);
    }

    private void hideKeyboard() {
        if (getActivity() != null && getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(
                    getActivity().getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

    private void setupCornerIcons(View view) {
        ImageView fireCornerIcon = view.findViewById(R.id.fire_corner_icon);
        ImageView policeCornerIcon = view.findViewById(R.id.police_corner_icon);
        ImageView mdrCornerIcon = view.findViewById(R.id.mdr_corner_icon);

        View.OnClickListener cornerIconListener = v -> {
            // Corner icons are non-interactive
        };

        fireCornerIcon.setOnClickListener(cornerIconListener);
        policeCornerIcon.setOnClickListener(cornerIconListener);
        mdrCornerIcon.setOnClickListener(cornerIconListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up resources
        imageButtonFire.setImageDrawable(null);
        imageButtonPolice.setImageDrawable(null);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save URIs if needed
        if (imageUriFire != null) {
            outState.putString("imageUriFire", imageUriFire.toString());
        }
        if (imageUriPolice != null) {
            outState.putString("imageUriPolice", imageUriPolice.toString());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore URIs if they were saved
            String fireUriString = savedInstanceState.getString("imageUriFire");
            String policeUriString = savedInstanceState.getString("imageUriPolice");

            if (fireUriString != null) {
                imageUriFire = Uri.parse(fireUriString);
                imageButtonFire.setImageURI(imageUriFire);
            }
            if (policeUriString != null) {
                imageUriPolice = Uri.parse(policeUriString);
                imageButtonPolice.setImageURI(imageUriPolice);
            }
        }
    }
}