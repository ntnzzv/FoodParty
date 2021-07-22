package com.example.recipebook.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.recipebook.R;
import com.example.recipebook.activities.MainActivity;

public class MyForegroundService extends Service {
    String CHANNEL_ID = "recipes_channel_01";
    private static final int NOTIFICATION_ID = 1;

    static NotificationManager notificationManager = null;

    Notification.Builder builder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        initForeground();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    private void initForeground() {

        if (notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Recipes main channel", NotificationManager.IMPORTANCE_DEFAULT);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        //create an explicit intent for an activity

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP / Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        builder = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Testing Notification...")
                .setSmallIcon(R.drawable.app_icon2)
                .setContentIntent(pendingIntent);

        startForeground(NOTIFICATION_ID, updateNotification(Integer.toString(0)));


    }

    private Notification updateNotification(String details) {
        builder.setContentText(details).setOnlyAlertOnce(false);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        notificationManager.notify(NOTIFICATION_ID, notification);
        return notification;
    }


}
