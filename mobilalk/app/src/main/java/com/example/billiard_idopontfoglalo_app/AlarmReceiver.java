package com.example.billiard_idopontfoglalo_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new com.example.billiard_idopontfoglalo_app.NotificationHandler(context)
                .send("Billiárdozz nálunk! Foglalj egyszerűen az alkalmazáson keresztül!");
    }
}