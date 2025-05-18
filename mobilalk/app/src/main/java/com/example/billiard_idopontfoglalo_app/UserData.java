package com.example.billiard_idopontfoglalo_app;

public class UserData {
    private String nev;
    private String email;
    private String phoneNumber;

    public UserData() {}

    public UserData(String nev, String email, String phoneNumber) {
        this.nev = nev;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
