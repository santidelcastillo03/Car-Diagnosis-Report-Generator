package com.example.certicaralt4;

public class Appointment {
    private String date;
    private String hour;
    private String clientName;

    public Appointment(String date, String hour, String clientName) {
        this.date = date;
        this.hour = hour;
        this.clientName = clientName;
    }

    public String getDate() {
        return date;
    }

    public String getHour() {
        return hour;
    }

    public String getClientName() {
        return clientName;
    }
}

