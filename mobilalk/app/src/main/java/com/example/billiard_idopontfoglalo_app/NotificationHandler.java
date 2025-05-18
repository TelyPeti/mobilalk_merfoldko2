package com.example.billiard_idopontfoglalo_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHandler {
    private Context context;

    private int NOTIFICATION_ID = 0;
    private NotificationManager manager;

    private static final String CHANNEL_ID ="billiard_club_notification_channel";


    public NotificationHandler(Context context) {
        this.context = context;
        this.manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createChannel();
    }

    private void createChannel(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            return;
        }

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Tély's Billiard Club", NotificationManager.IMPORTANCE_DEFAULT);

        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setDescription("Asztalfoglalással kapcsolatos informació");
        this.manager.createNotificationChannel(channel);
    }

    public void send(String message){
        Intent intent = new Intent(context, ReservationDoneActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Tély's Billiard Club")
                .setContentText(message)
                .setSmallIcon(R.drawable.billiardicon)
                .setContentIntent(pendingIntent);


        this.manager.notify(NOTIFICATION_ID, builder.build());
    }
}
