package com.disastermate.mapbox.Activities;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.disastermate.mapbox.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.disastermate.mapbox.other.Notifications.CHANNEL_2_ID;

public class BaseDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final String PREFS_NAME2 = "watchlist";
    DrawerLayout drawerLayout;
    FrameLayout frameLayout;
    static Toolbar toolbar;
    NavigationView navigationView;
    NotificationManagerCompat notificationManager;
    private MenuInflater inflater;
    FloatingActionButton help;
    private CarmenFeature home;
    private CarmenFeature work;
    ArrayList<String> arrPackage;
    HashMap<String, String> hmap;
    MenuItem item;

    private static int i = 0;

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
        help = findViewById(R.id.help);
        arrPackage = new ArrayList<>();
        hmap = new HashMap<>();

        Animation shake;
        shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

//        frameLayout.startAnimation(shake);


    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        //to prevent current item select over and over
        if (item.isChecked()) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        }

        if (id == R.id.nav_home) {
            // Handle the camera action
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(getApplicationContext(), Historic.class));
        } else if (id == R.id.nav_bushtodolist) {
            startActivity(new Intent(getApplicationContext(), TodoListActivity.class));
        } else if (id == R.id.nav_floodtodolist) {
            startActivity(new Intent(getApplicationContext(), Flood2doList.class));
        } else if (id == R.id.nav_disastersList) {
            startActivity(new Intent(getApplicationContext(), BushfireListActivity.class));
        } else if (id == R.id.nav_notification) {

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
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000
                    });
            builder.setContentIntent(resultPendingIntent);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(2, builder.build());

        } else if (id == R.id.nav_watchlist) {
//            addUserLocations();
            initSearchFab(2);
        }
//        else if (id == R.id.nav_share) {
//            startActivity(new Intent(getApplicationContext(), ShareActivity.class));
//        } else if (id == R.id.nav_send) {
//            startActivity(new Intent(getApplicationContext(), SendActivity.class));
//        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            // action with ID action_refresh was selected
//            case R.id.button_item:
//                Toast.makeText(getApplicationContext(), "Skip selected", Toast.LENGTH_SHORT)
//                        .show();
//                break;
//            default:
//                break;
//        }
//        return true;
//    }

    public void initSearchFab(int requestCode) {
        if (getClass() != MainActivity.class) {
            Intent intent = new Intent(this, MainActivity.class);
        }
        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.access_token))
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#EEEEEE"))
                        .limit(10)
                        .country("au")
                        .build(PlaceOptions.MODE_CARDS)
                )
                .build(this);
        startActivityForResult(intent, requestCode);

    }

    public void addUserLocations(String name, Double lat, Double longi) {


    }


    public void addMenuItemInNavMenuDrawer(final String location, String risk, final String postcode) {


        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);

        Menu menu = navView.getMenu();
        final SubMenu subMenu;

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME2, Context.MODE_PRIVATE);

        Gson gsonh = new Gson();
        String jsonh = sharedPreferences.getString("Seth", "");
        if (jsonh.isEmpty()) {
            Toast.makeText(this, "There is nothing to add", Toast.LENGTH_LONG).show();
        } else {
            menu.removeItem(menu.size() - 1);
            subMenu = menu.addSubMenu("WatchList");

            Type typeh = new TypeToken<HashMap<String,String>>()    {}.getType();

            HashMap<String,String> arrPackageDatahash = gsonh.fromJson(jsonh, typeh);
            Set set = arrPackageDatahash.entrySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext()) {
                Map.Entry mentry = (Map.Entry)iterator.next();

                //add logic here
//                System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
//                System.out.println(mentry.getValue());

               MenuItem item =  subMenu.add((String) mentry.getValue());
                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
//                        MainActivity.location_address.setText("Location:" + address.getAddressLine(0));
//                        MainActivity.risk.setText(j.getString("bushfireRiskRating"));
//
//
//                        map.animateCamera(CameraUpdateFactory.newCameraPosition(
//                                new CameraPosition.Builder()
//                                        .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
//                                                ((Point) selectedCarmenFeature.geometry()).longitude()))
//                                        .zoom(14)
//                                        .build()), 3000);
                        return true;
                    }
                });


            }

        }

        Log.i("menu", "YES");

        if (location.toString().isEmpty() && postcode.toString().isEmpty()) {
            Toast.makeText(this, "Plz Enter all the data", Toast.LENGTH_LONG).show();
        } else {

            hmap.put(postcode,location);
            gsonh = new Gson();
            jsonh = gsonh.toJson(hmap);
            SharedPreferences.Editor editor ;
            editor = sharedPreferences.edit();
            editor.putString("Seth", jsonh);
            Log.i("JSON", jsonh);
            editor.commit();

//
//


            //refreshing adapter
            for (int i = 0, count = navView.getChildCount(); i < count; i++) {
                final View child = navView.getChildAt(i);
                if (child != null && child instanceof ListView) {
                    final ListView menuView = (ListView) child;
                    final HeaderViewListAdapter adapter = (HeaderViewListAdapter) menuView.getAdapter();
                    final BaseAdapter wrapped = (BaseAdapter) adapter.getWrappedAdapter();
                    wrapped.notifyDataSetChanged();
                }
            }

            Toast.makeText(getApplicationContext(), "Item Added to Navigation Drawer", Toast.LENGTH_SHORT).show();





            navView.invalidate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}

