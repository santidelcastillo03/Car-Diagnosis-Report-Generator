package com.example.certicaralt4;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ViewAppointmentsActivity extends AppCompatActivity {

    private ListView lvAppointments;
    private DatabaseHelper databaseHelper;
    private ArrayAdapter<String> adapter;
    private List<String> appointments;
    private List<Integer> appointmentIds; // To track IDs of the appointments

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointments);

        lvAppointments = findViewById(R.id.lvAppointments);
        databaseHelper = new DatabaseHelper(this);

        appointmentIds = new ArrayList<>();
        appointments = databaseHelper.getAppointments(appointmentIds); // Get appointments and IDs

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appointments);
        lvAppointments.setAdapter(adapter);

        // Long-click listener for deletion
        lvAppointments.setOnItemLongClickListener((parent, view, position, id) -> {
            int appointmentId = appointmentIds.get(position); // Get the ID of the selected appointment

            new AlertDialog.Builder(this)
                    .setTitle("Delete Appointment")
                    .setMessage("Are you sure you want to delete this appointment?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        databaseHelper.deleteAppointment(appointmentId); // Delete from database
                        appointments.remove(position); // Remove from the list
                        appointmentIds.remove(position); // Remove the corresponding ID
                        adapter.notifyDataSetChanged(); // Update the UI
                        Toast.makeText(this, "Appointment deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });
    }
}
