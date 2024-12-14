package com.example.certicaralt4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnAddAppointment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddAppointmentActivity.class));
            }
        });

        findViewById(R.id.btnViewAppointments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ViewAppointmentsActivity.class));
            }
        });

        findViewById(R.id.btnCreateReport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Future feature
            }
        });
    }
}