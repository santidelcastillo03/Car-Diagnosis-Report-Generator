package com.example.certicaralt4;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ViewAppointmentsActivity extends AppCompatActivity {

    private ListView lvAppointments;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointments);

        ListView lvAppointments = findViewById(R.id.lvAppointments);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Retrieve appointments as a list of strings
        List<String> appointments = databaseHelper.getAppointments();

        // Set up a simple ArrayAdapter to display the appointments
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appointments);
        lvAppointments.setAdapter(adapter);
    }
}
