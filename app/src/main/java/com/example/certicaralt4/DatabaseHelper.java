package com.example.certicaralt4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "appointments.db";
    private static final int DATABASE_VERSION = 2; // Incremented version

    private static final String TABLE_NAME = "appointments";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_HOUR = "hour";
    private static final String COLUMN_VEHICLE_MAKE = "vehicle_make";
    private static final String COLUMN_MODEL = "model";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_PLATE = "plate";
    private static final String COLUMN_CLIENT_NAME = "client_name";
    private static final String COLUMN_CLIENT_ID = "client_id";
    private static final String COLUMN_PLAN = "plan";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_HOUR + " TEXT, " +
                COLUMN_VEHICLE_MAKE + " TEXT, " +
                COLUMN_MODEL + " TEXT, " +
                COLUMN_YEAR + " TEXT, " +
                COLUMN_PLATE + " TEXT, " +
                COLUMN_CLIENT_NAME + " TEXT, " +
                COLUMN_CLIENT_ID + " TEXT, " +
                COLUMN_PLAN + "TEXT)"; // Ensure 'plan' is part of the schema
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_PLAN + "TEXT");
        }
    }

    public void insertAppointment(String date, String hour, String vehicleMake, String model, String year,
                                  String plate, String clientName, String clientId, String plan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_HOUR, hour);
        values.put(COLUMN_VEHICLE_MAKE, vehicleMake);
        values.put(COLUMN_MODEL, model);
        values.put(COLUMN_YEAR, year);
        values.put(COLUMN_PLATE, plate);
        values.put(COLUMN_CLIENT_NAME, clientName);
        values.put(COLUMN_CLIENT_ID, clientId);
        values.put(COLUMN_PLAN, plan);

        long result = db.insert(TABLE_NAME, null, values);
        if (result == -1) {
            Log.e("DB_ERROR", "Failed to insert appointment");
        } else {
            Log.d("DB_SUCCESS", "Appointment inserted with ID: " + result);
        }
    }

    public List<String> getAppointments() {
        List<String> appointments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_DATE, COLUMN_HOUR, COLUMN_CLIENT_NAME, COLUMN_VEHICLE_MAKE, COLUMN_MODEL, COLUMN_PLAN},
                null, null, null, null, COLUMN_DATE + ", " + COLUMN_HOUR);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String hour = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HOUR));
                String clientName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLIENT_NAME));
                String vehicleMake = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_MAKE));
                String model = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODEL));
                String plan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAN));


                String appointment = "Date: " +date + "\n" + "Hour: " + hour + "\n" + plan + "\n" + "Client: " + clientName + "\n" + "Make: " + vehicleMake + "\n" + "Model: " + model;

                appointments.add(appointment);
            }
            cursor.close();
        }
        return appointments;
    }
}

