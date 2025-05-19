package com.example.billiard_idopontfoglalo_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

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

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Tély's Biliard Club", NotificationManager.IMPORTANCE_DEFAULT);

        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setDescription("Asztalfoglalással kapcsolatos informació");
        this.manager.createNotificationChannel(channel);
    }

    public void send(String message) {
        try {
            Intent intent = new Intent(context, HomePageActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Tély's Biliard Club")
                    .setContentText(message)
                    .setSmallIcon(R.drawable.billiardiconformessage)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{0, 500, 200, 500});

            this.manager.notify(NOTIFICATION_ID++, builder.build());
        } catch (Exception e) {
            Log.e("Notification", "Hiba az értesítés küldésekor", e);
        }
    }

    public void sendTicket(String message) {
        try {
            Intent intent = new Intent(context, ReservationDoneActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Tély's Biliard Club")
                    .setContentText(message)
                    .setSmallIcon(R.drawable.billiardiconformessage)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{0, 500, 200, 500});

            this.manager.notify(NOTIFICATION_ID++, builder.build());
        } catch (Exception e) {
            Log.e("Notification", "Hiba az értesítés küldésekor", e);
        }
    }

}
