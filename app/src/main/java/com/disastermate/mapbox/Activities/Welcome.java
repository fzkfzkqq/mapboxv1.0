package com.disastermate.mapbox.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.disastermate.mapbox.R;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Welcome extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static int TIME_OUT = 1000;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private static String PREFS_NAME = "Prefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_welcome);

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);

        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Boolean isAgree = sharedpreferences.getBoolean("accepted",false);

        if(!isAgree) {
            //TODO: need to fix the size of the fullscreen image
            showDialog(0);

        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(Welcome.this, MainActivity.class);
                    startActivity(i);
                }
            }, TIME_OUT);
        }
    }



        // Set up the user interaction to manually show or hide the system UI.
//        mContentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(Welcome.this,MainActivity.class);
//                startActivity(intent);
//            }
//        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

    @Override
    protected Dialog onCreateDialog(int id){
        // show disclaimer....
        // for example, you can show a dialog box...

        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Boolean isAgree = sharedpreferences.getBoolean("accepted",false);

        if(!isAgree){



            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(" LEGAL DISCLAIMER \n\n While we make every effort to ensure that material on this application is accurate and up to date (unless denoted as archived material), such material does in no way constitute the provision of professional advice or a prediction that should be relied upon.\n" +
                    "\n" +
                    "DisasterMate does not guarantee, and accepts no legal liability whatsoever arising from or connected to, the accuracy, reliability, currency or completeness of any material contained on this application or any linked site. Users should seek appropriate independent professional advice prior to relying on or entering into any commitment based on material published here, which material is purely published for reference purposes alone.\n" +
                    "\n" +
                    "By using the application, you will be deemed to have released and discharged DisasterMate from all liability (including negligence) for all claims, losses expenses, damages, and costs the user may incur as a result of relying on the information on this application, including liability in respect of any defamatory material on any database or in respect of any dealings with any work (including software) in which you hold any copyright or other intellectual property right. By using the application, you will be assuming all risks associated with the use of this application, including risks to your phone, software or data by any virus which might be transmitted, downloaded or activated via this or an external website and/or its contents.\n" +
                    "\n" +
                    "Material, summarised views, standards or recommendations of third parties or recommendations made by this application do not necessarily reflect the views of DisasterMate or indicate a commitment to a course of action.\n")
                    .setCancelable(false)
                    .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // and, if the user accept, you can execute something like this:
                            // We need an Editor object to make preference changes.
                            // All objects are from android.context.Context
                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("accepted", true);
                            // Commit the edits!
                            editor.commit();
                            Intent i = new Intent(Welcome.this, MainActivity.class);
                            startActivity(i);

                        }
                    })
                .setNegativeButton("Disagree", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //nm.cancel(R.notification.running);
                        // cancel the NotificationManager (icon)
                        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("accepted", false);
                        // Commit the edits!
                        finish();

                    }
                });
            AlertDialog alert = builder.create();
            return alert;
        }
        else
            return null;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    BroadcastReceiver broadcast_reciever = new BroadcastReceiver() {

    @Override
    public void onReceive(Context arg0, Intent intent) {
        String action = intent.getAction();
        if (action.equals("finish")) {
    //finishing the activity
            finish();
        }
    }
};

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return super.registerReceiver(broadcast_reciever, new IntentFilter("finish"));
    }
}
