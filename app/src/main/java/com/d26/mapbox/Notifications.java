package com.d26.mapbox;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


public class Notifications extends Application {
    public static final String CHANNEL_2_ID = "news";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Channel news",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel2.setDescription("Updates for current bushfires");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel2);
    }





}