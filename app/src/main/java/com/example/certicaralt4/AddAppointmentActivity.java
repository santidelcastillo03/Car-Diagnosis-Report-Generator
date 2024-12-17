package com.example.certicaralt4;



import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddAppointmentActivity extends AppCompatActivity {

    private TextView tvDate; // Changed from EditText to TextView
    private EditText etHour, etVehicleMake, etModel, etYear, etPlate, etClientName, etClientId;
    private Spinner spnPlan;
    private Button btnSave;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        // Initialize views
        tvDate = findViewById(R.id.tvDate); // Updated to TextView
        etHour = findViewById(R.id.etHour);
        etVehicleMake = findViewById(R.id.etVehicleMake);
        etModel = findViewById(R.id.etModel);
        etYear = findViewById(R.id.etYear);
        etPlate = findViewById(R.id.etPlate);
        etClientName = findViewById(R.id.etClientName);
        etClientId = findViewById(R.id.etClientId);
        spnPlan = findViewById(R.id.spnPlan);
        btnSave = findViewById(R.id.btnSave);

        // Set up Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.plan_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPlan.setAdapter(adapter);

        databaseHelper = new DatabaseHelper(this);

        // Set up DatePickerDialog for the date TextView
        tvDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(
                    AddAppointmentActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format the selected date as DD-MM-YYYY
                        String formattedDate = String.format("%02d/%02d/%04d",
                                selectedDay, selectedMonth + 1, selectedYear);
                        tvDate.setText(formattedDate);
                    },
                    year, month, day
            ).show();
        });

        // Save appointment on button click
        btnSave.setOnClickListener(v -> {
            String date = tvDate.getText().toString(); // Updated to get text from TextView
            String hour = etHour.getText().toString();
            String vehicleMake = etVehicleMake.getText().toString();
            String model = etModel.getText().toString();
            String year = etYear.getText().toString();
            String plate = etPlate.getText().toString();
            String clientName = etClientName.getText().toString();
            String clientId = etClientId.getText().toString();

            // Prevent NullPointerException with Spinner
            String plan = spnPlan.getSelectedItem() != null ? spnPlan.getSelectedItem().toString() : "";

            // Save appointment to the database
            databaseHelper.insertAppointment(date, hour, vehicleMake, model, year, plate, clientName, clientId, plan);
            Toast.makeText(AddAppointmentActivity.this, "Appointment Saved!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}

