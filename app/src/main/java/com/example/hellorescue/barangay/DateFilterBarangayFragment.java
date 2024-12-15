package com.example.hellorescue.barangay;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.hellorescue.R;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateFilterBarangayFragment extends Fragment {

    String[] items_custom = {"None", "Last 7 days", "Last 30 days", "Last 60 days", "Custom"};
    TextView startDateText, endDateText;
    Calendar calendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.date_filter_barangay, container, false);

        AutoCompleteTextView autoCompleteTxtCustom = view.findViewById(R.id.auto_complete_txt_custom);
        TextInputLayout textInputLayout = view.findViewById(R.id.TextInputLayoutCustom_container);

        ArrayAdapter<String> adapterItemsFire = new ArrayAdapter<>(getContext(), R.layout.list_item, items_custom);
        autoCompleteTxtCustom.setAdapter(adapterItemsFire);

        // Manage hints for the dropdown
        autoCompleteTxtCustom.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                textInputLayout.setHint(null);
            } else if (autoCompleteTxtCustom.getText().toString().isEmpty()) {
                textInputLayout.setHint("Select Option");
            }
        });

        autoCompleteTxtCustom.setOnItemClickListener((parent, view1, position, id) -> {
            textInputLayout.setHint(null);
            updateCalendarButtonsState(autoCompleteTxtCustom.getText().toString());
        });

        // Set up date pickers for "Start" and "End" buttons
        startDateText = view.findViewById(R.id.start_date_text);
        endDateText = view.findViewById(R.id.end_date_text);
        ImageButton startDateButton = view.findViewById(R.id.start_date_button);
        ImageButton endDateButton = view.findViewById(R.id.end_date_button);


        startDateButton.setEnabled(false);
        endDateButton.setEnabled(false);

        startDateButton.setOnClickListener(v -> showDatePickerDialog(startDateText));
        endDateButton.setOnClickListener(v -> showDatePickerDialog(endDateText));

        return view;
    }


    // Method to update the state of the calendar buttons based on dropdown selection
    private void updateCalendarButtonsState(String selectedOption) {
        ImageButton startDateButton = getView().findViewById(R.id.start_date_button);
        ImageButton endDateButton = getView().findViewById(R.id.end_date_button);
        RelativeLayout startDateContainer = getView().findViewById(R.id.start_date_container);
        RelativeLayout endDateContainer = getView().findViewById(R.id.end_date_container);

        if ("Custom".equals(selectedOption)) {
            // Enable buttons if "Custom" is selected
            startDateButton.setEnabled(true);
            endDateButton.setEnabled(true);

            // Change background for Custom selection
            startDateContainer.setBackgroundResource(R.drawable.date_filter_shape_bg_custom);
            endDateContainer.setBackgroundResource(R.drawable.date_filter_shape_bg_custom);

            // Reset calendar to the current date
            calendar = Calendar.getInstance();

            // Reset the start and end date TextViews to default text
            startDateText.setText("Select start date");
            endDateText.setText("Select end date");

            // Reset text color to default (Custom color)
            startDateText.setTextColor(Color.parseColor("#413e3f"));
            endDateText.setTextColor(Color.parseColor("#413e3f"));

        } else {
            // Reset backgrounds to not custom
            startDateContainer.setBackgroundResource(R.drawable.date_filter_shape_bg_not_custom);
            endDateContainer.setBackgroundResource(R.drawable.date_filter_shape_bg_not_custom);

            if ("Last 7 days".equals(selectedOption)) {
                updateDatesForLastDays(7);
                startDateText.setTextColor(Color.parseColor("#413e3f"));
                endDateText.setTextColor(Color.parseColor("#413e3f"));
            } else if ("Last 30 days".equals(selectedOption)) {
                updateDatesForLastDays(30);
                startDateText.setTextColor(Color.parseColor("#413e3f"));
                endDateText.setTextColor(Color.parseColor("#413e3f"));
            } else if ("Last 60 days".equals(selectedOption)) {
                updateDatesForLastDays(60);
                startDateText.setTextColor(Color.parseColor("#413e3f"));
                endDateText.setTextColor(Color.parseColor("#413e3f"));
            } else if ("None".equals(selectedOption)) {
                // Reset dates and text color for "None"
                startDateText.setText("Select start date");
                endDateText.setText("Select end date");
                startDateText.setTextColor(Color.parseColor("#949494"));
                endDateText.setTextColor(Color.parseColor("#949494"));
            } else {
                // Reset dates for other options
                startDateText.setText("Select start date");
                endDateText.setText("Select end date");
            }

            // Disable buttons for non-Custom options
            startDateButton.setEnabled(false);
            endDateButton.setEnabled(false);
        }
    }





    // Helper method to calculate and set dates for "Last X days"
    private void updateDatesForLastDays(int days) {
        // Set the end date to today
        Calendar endDate = Calendar.getInstance();
        String formattedEndDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(endDate.getTime());
        endDateText.setText(formattedEndDate);

        // Set the start date to (days - 1) before today
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_YEAR, -(days - 1)); // Subtract days to include today
        String formattedStartDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(startDate.getTime());
        startDateText.setText(formattedStartDate);
    }




    private void showDatePickerDialog(TextView dateTextView) {
        // Create a DatePickerDialog and set the current date as default
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.CustomDatePickerDialog,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    String selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
                    dateTextView.setText(selectedDate);

                    // Update the minimum date for the end date picker if the start date is set
                    if (dateTextView == startDateText) {
                        updateEndDateMinDate();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        // Restrict the maximum selectable date to the current date
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Set the minimum date for the end date picker if it is already selected
        if (dateTextView == endDateText && !startDateText.getText().toString().equals("Select start date")) {
            Calendar startDateCalendar = getSelectedDateCalendar(startDateText);
            datePickerDialog.getDatePicker().setMinDate(startDateCalendar.getTimeInMillis());
        }

        // Show the dialog
        datePickerDialog.show();

        // Modify the button colors after the dialog is shown
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#ff746c"));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#ff746c"));
    }

    // Helper method to update the minimum date for the end date picker
    private void updateEndDateMinDate() {
        if (!startDateText.getText().toString().equals("Select start date")) {
            Calendar startDateCalendar = getSelectedDateCalendar(startDateText);

            // Parse the end date if it was previously selected
            if (!endDateText.getText().toString().equals("Select end date")) {
                Calendar endDateCalendar = getSelectedDateCalendar(endDateText);
                if (endDateCalendar.before(startDateCalendar)) {
                    // Reset the end date text if it is earlier than the start date
                    endDateText.setText("Select end date");
                }
            }
        }
    }

    // Helper method to parse a date from a TextView into a Calendar object
    private Calendar getSelectedDateCalendar(TextView dateTextView) {
        Calendar selectedCalendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            selectedCalendar.setTime(sdf.parse(dateTextView.getText().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return selectedCalendar;
    }

}
