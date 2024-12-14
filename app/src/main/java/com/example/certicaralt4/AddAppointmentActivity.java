package com.example.certicaralt4;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class AddAppointmentActivity extends AppCompatActivity {

    private EditText etDate, etHour, etVehicleMake, etModel, etYear, etPlate, etClientName, etClientId;
    private Spinner spnPlan;
    private Button btnSave;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        EditText etDate = findViewById(R.id.etDate);
        EditText etHour = findViewById(R.id.etHour);
        EditText etVehicleMake = findViewById(R.id.etVehicleMake);
        EditText etModel = findViewById(R.id.etModel);
        EditText etYear = findViewById(R.id.etYear);
        EditText etPlate = findViewById(R.id.etPlate);
        EditText etClientName = findViewById(R.id.etClientName);
        EditText etClientId = findViewById(R.id.etClientId);
        Spinner spnPlan = findViewById(R.id.spnPlan);
        Button btnSave = findViewById(R.id.btnSave);

        // Set up Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.plan_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPlan.setAdapter(adapter);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        btnSave.setOnClickListener(v -> {
            String date = etDate.getText().toString();
            String hour = etHour.getText().toString();
            String vehicleMake = etVehicleMake.getText().toString();
            String model = etModel.getText().toString();
            String year = etYear.getText().toString();
            String plate = etPlate.getText().toString();
            String clientName = etClientName.getText().toString();
            String clientId = etClientId.getText().toString();

            // Prevent NullPointerException with Spinner
            String plan = spnPlan.getSelectedItem() != null ? spnPlan.getSelectedItem().toString() : "";

            databaseHelper.insertAppointment(date, hour, vehicleMake, model, year, plate, clientName, clientId, plan);
            Toast.makeText(AddAppointmentActivity.this, "Appointment Saved!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }


}
