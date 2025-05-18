package com.example.billiard_idopontfoglalo_app;

public class ReservationData {
    private String email;
    private String tableNumber;
    private String date;
    private String time;
    public ReservationData() {}

    public ReservationData(String email, String tableNumber, String date, String time) {
        this.email = email;
        this.tableNumber = tableNumber;
        this.date = date;
        this.time = time;
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
