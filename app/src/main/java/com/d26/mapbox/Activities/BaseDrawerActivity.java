package com.d26.mapbox.Activities;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.d26.mapbox.R;

import static com.d26.mapbox.other.Notifications.CHANNEL_2_ID;

public class BaseDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    FrameLayout frameLayout;
    static Toolbar toolbar;
    NavigationView navigationView;
    NotificationManagerCompat notificationManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_drawer);

        setSupportActionBar(toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Bushfire Prediction");

        frameLayout = (FrameLayout) findViewById(R.id.content_frame);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawerLayout.setDrawerListener(toggle);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

//    @Override
//    public void onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        //to prevent current item select over and over
        if (item.isChecked()) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        }

//        switch (id)
//        {
//            case R.id.nav_home:
//                break;
//            case R.id.nav_gallery:
//                break;
//            case R.id.nav_disastersList:
//
//        }

        if (id == R.id.nav_home) {
            // Handle the camera action
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else if (id == R.id.nav_gallery) {
                startActivity(new Intent(getApplicationContext(), Historic.class));
        }
         else if (id == R.id.nav_disastersList) {
            startActivity(new Intent(getApplicationContext(), BushfireListActivity.class));
        }
         else if (id == R.id.nav_notification) {

            // Create an Intent for the activity you want to start
            Intent resultIntent = new Intent(this, BushfireListActivity.class);
            // Create the TaskStackBuilder and add the intent, which inflates the back stack
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            // Get the PendingIntent containing the entire back stack
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationManager = NotificationManagerCompat.from(this);

//            Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
//                        .setSmallIcon(R.drawable.logo)
//                        .setContentTitle("Test!")
//                        .setContentText("This is a test for current alerts")
//                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000
//                        })
//                        .build();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_2_ID).setSmallIcon(R.drawable.logo)
                    .setContentTitle("Test!")
                    .setContentText("This is a test for current alerts")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000
                    });
            builder.setContentIntent(resultPendingIntent);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(2, builder.build());

//            notificationManager.notify(2, notification);
            }
//        else if (id == R.id.nav_share) {
//            startActivity(new Intent(getApplicationContext(), ShareActivity.class));
//        } else if (id == R.id.nav_send) {
//            startActivity(new Intent(getApplicationContext(), SendActivity.class));
//        }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
    }
