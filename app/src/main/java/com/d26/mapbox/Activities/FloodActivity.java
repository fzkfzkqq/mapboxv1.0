package com.d26.mapbox.Activities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.d26.mapbox.R;
import com.d26.mapbox.other.Restful;
import com.google.gson.JsonObject;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.OnLocationClickListener;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.pluginscalebar.ScaleBarOptions;
import com.mapbox.pluginscalebar.ScaleBarPlugin;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import static com.d26.mapbox.other.Notifications.CHANNEL_2_ID;

public class FloodActivity extends
        BaseDrawerActivity implements OnMapReadyCallback, PermissionsListener, OnLocationClickListener {
    private static String PREFS_NAME = "Prefs";
    private PermissionsManager permissionsManager;
    private MapView mapView;
    private MapboxMap map;
    private Button test;
    private String postCode;
    private LocationEngine locationEngine;
    private TextView risk;
    private Address address;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 3000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 500;
    private JSONObject j = new JSONObject();
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";
    private Button btn_airPressure;
    private Button btn_humi;
    private Button btn_rainfall;
    private android.support.v7.widget.Toolbar mTopToolbar;
    private boolean isInTrackingMode;
    private LocationComponent locationComponent;
    private Button dialogue_button;
    private TextView lastupdated;
    private TextView location_address;
    private Button bushfire;
    private ObjectAnimator objAnim;
    private EditText editTextTitle;
    private EditText editTextMessage;
    private NotificationManagerCompat notificationManager;
    private CarmenFeature home;
    private CarmenFeature work;
    private LatLng location;
    String riskString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /*Mapbox Key*/
        Mapbox.getInstance(this, "pk.eyJ1IjoiZnprODg4IiwiYSI6ImNqemh1a3M4MzB6eGgzbmxrMWx0c3Q3b3AifQ.--BckGBvrRT-TXTMJsaDAA");
        getLayoutInflater().inflate(R.layout.activity_flood, frameLayout);


        BaseDrawerActivity.toolbar.setTitle("Flood Prediction");


        /*Declare all UI to Objects herer*/
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        risk = (TextView) findViewById(R.id.text_riskrate);
//        search = (ImageButton) findViewById(R.id.btn_search);
        lastupdated = findViewById(R.id.lastupdated);
        btn_humi = findViewById(R.id.btn_humi);
        btn_airPressure = findViewById(R.id.btn_waterlevel);
        btn_rainfall = findViewById(R.id.btn_rainfall);
        location_address = findViewById(R.id.location_address);

        /*Declare other variables here*/
        final Geocoder geocoder = new Geocoder(this);
        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        notificationManager = NotificationManagerCompat.from(this);
        Boolean isAgree = sharedpreferences.getBoolean("d_accepted",false);
        riskString = "null";

        /*Fires up the map instance and style
         * Here is where you also functions such as search and adding user locations*/
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                map = mapboxMap;

                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        ScaleBarPlugin scaleBarPlugin = new ScaleBarPlugin(mapView, mapboxMap);

                        // Create a ScaleBarOptions object to use the Plugin's default styling
                        scaleBarPlugin.create(new ScaleBarOptions(FloodActivity.this)
                                .setTextSize(20f)
                                .setBarHeight(10f)
                                .setBorderWidth(7f)
                                .setMetricUnit(true)
                                .setRefreshInterval(15)
                                .setMarginTop(30f)
                                .setMarginLeft(16f)
                                .setTextBarMargin(15f));

                        enableLocationComponent(style);
                        //TODO: Add search button function here initSearchFab() and addUserLocations() here
                        initSearchFab();
                        addUserLocations();
                    }
                });
            }
        });

        /*That green button is clickable
         * Watch out! Wuuuu*/
        risk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog settingsDialog = new Dialog(v.getContext());
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                if (risk.getText().toString().equals("LOW")){
                    settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.view_lowflood
                            , null));
                    settingsDialog.show();
                    dialogue_button = settingsDialog.findViewById(R.id.dialogue_button);
                    dialogue_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            settingsDialog.dismiss();
                        }
                    });

                }
                else if (risk.getText().toString().equals("MEDIUM")){
                    settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.view_medflood
                            , null));
                    settingsDialog.show();
                    dialogue_button = settingsDialog.findViewById(R.id.dialogue_button);
                    dialogue_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            settingsDialog.dismiss();
                        }
                    });
                }
                else if (risk.getText().toString().equals("HIGH")){
                    settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.view_lowflood
                            , null));
                    settingsDialog.show();
                    dialogue_button = settingsDialog.findViewById(R.id.dialogue_button);
                    dialogue_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            settingsDialog.dismiss();
                        }
                    });
                }
            }
        });

        findViewById(R.id.change_bushfire).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        btn_airPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog settingsDialog = new Dialog(view.getContext());
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.factor_popup_layout
                        , null));
                settingsDialog.show();
                ImageView image = settingsDialog.findViewById(R.id.factor_image);
                image.setBackgroundResource(R.drawable.flood_air);
                TextView factor_des = settingsDialog.findViewById(R.id.factor_description);
                factor_des.setText("How air pressure affects floods");
                dialogue_button = settingsDialog.findViewById(R.id.dialogue_button);
                dialogue_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        settingsDialog.dismiss();
                    }
                });
            }
        });

        btn_humi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog settingsDialog = new Dialog(view.getContext());
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.factor_popup_layout
                        , null));
                settingsDialog.show();
                ImageView image = settingsDialog.findViewById(R.id.factor_image);
                image.setBackgroundResource(R.drawable.flood_humi);
                TextView factor_des = settingsDialog.findViewById(R.id.factor_description);
                factor_des.setText("How humidity affects flood");
                dialogue_button = settingsDialog.findViewById(R.id.dialogue_button);
                dialogue_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        settingsDialog.dismiss();
                    }
                });
            }
        });

        btn_rainfall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog settingsDialog = new Dialog(view.getContext());
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.factor_popup_layout
                        , null));
                settingsDialog.show();
                ImageView image = settingsDialog.findViewById(R.id.factor_image);
                image.setBackgroundResource(R.drawable.flood_rainfall);
                TextView factor_des = settingsDialog.findViewById(R.id.factor_description);
                factor_des.setText("How rainfall lead to a flood");
                dialogue_button = settingsDialog.findViewById(R.id.dialogue_button);
                dialogue_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        settingsDialog.dismiss();
                    }
                });
            }
        });
        /*This is uses a third party application called pusher to get real time alerts
         * as of now we need 2 channels, one for updates and one for prediction alerts*/

        PusherOptions options = new PusherOptions();
        options.setCluster("ap4");
        Pusher pusher = new Pusher("42c4c1388c60931e9673", options);

        /*switch channel names here*/
        Channel channel = pusher.subscribe("my-channel");

        /*The channel is binded with the news api which is binded with firebase and pusher
         * Thank you Antony for writing the backend code in Azure Function App to make this work
         * I could have never done it alone lol
         * Author: Nikhil P.*/
        channel.bind("my-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                System.out.println(event.getData());

                /*Provides with a default notification anytime the database is updated with a
                 * bushfire alert. THIS IS REALTIME which makes it awesome
                 * https://dashboard.pusher.com/apps/860496/console/realtime_messages
                 * */
                Notification notification = new NotificationCompat.Builder(FloodActivity.this, CHANNEL_2_ID).setSmallIcon(R.drawable.logo)
                        .setContentTitle("Watch Out!")
                        .setContentText("BushFire at " + event.getData())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000
                        })
                        .build();

                /*This is what actually triggers the alarm*/
                notificationManager.notify(2, notification);
            }
        });

        pusher.connect();

        /*Just a stupid blinking animation that the mentors liked so I'll keep it*/
        pulseAnimation();
    }

    /*This is where you induce the search logic, after the intent has been called to the full screen search option
     * Took a braniac like myself to figure this one out lol*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Address search_add;
        Geocoder geocoder = new Geocoder(this);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);


            //removes any pending updates
            //locationEngine.removeLocationUpdates(callback);

            try {

                search_add = geocoder.getFromLocationName(selectedCarmenFeature.placeName(), 1).get(0);
                address = search_add;

                getFloodDetailAsyncTask getSearchDeatilAsyncTask = new getFloodDetailAsyncTask();
                getSearchDeatilAsyncTask.execute(search_add.getPostalCode());

                try {

                    /*TODO: there is a bug here which shows low for not available locations
                     *  We must either restrict the searches or show no data for that location*/
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(search_add.getLatitude(), search_add.getLongitude()))
                            .title(address.getAddressLine(0) + "\n Risk Rate: " + risk.getText().toString()));
                    Log.i("RRD", risk.getText().toString());

                    location_address.setText("Location:" + address.getAddressLine(0));
                    risk.setText(j.getString("bushfireRiskRating"));

                    Log.i("What is the meaning of life", j.getString("bushfireRiskRating"));
                    Log.i("Why do birds fly",j.getString(address.getAddressLine(0)));

                    map.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 7000);


                } catch (JSONException e) {
                    risk.setText("No Data");
                    e.printStackTrace();


                    // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
                    // Then retrieve and update the source designated for showing a selected location's symbol layer icon

                    if (map != null) {
                        Style style = map.getStyle();
                        if (style != null) {
                            GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                            if (source != null) {
                                source.setGeoJson(FeatureCollection.fromFeatures(
                                        new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())}));
                            }

                            // Move map camera to the selected location
                            map.animateCamera(CameraUpdateFactory.newCameraPosition(
                                    new CameraPosition.Builder()
                                            .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                                    ((Point) selectedCarmenFeature.geometry()).longitude()))
                                            .zoom(14)
                                            .build()), 7000);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
//            input_postcode.setError("No address found");
//            input_postcode.setText("");
            System.out.println("do nothing take a chill pill");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        getNewsAsyncTask getNewsAsyncTask = new getNewsAsyncTask();
        getNewsAsyncTask.execute();

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "No user permission", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            map.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "permission not granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            final LocationComponent locationComponent = map.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);


            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            locationComponent.addOnLocationClickListener((OnLocationClickListener) this);
            try {
               location = new LatLng(locationComponent.getLastKnownLocation().getLatitude(),
                      locationComponent.getLastKnownLocation().getLongitude());
               this.getCurrentLocationDeatial(location.getLatitude(),location.getLongitude());
            } catch (IOException e) {
                e.printStackTrace();
            }
            findViewById(R.id.back_to_camera_tracking_mode).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                         location = new LatLng(locationComponent.getLastKnownLocation().getLatitude(),
                                locationComponent.getLastKnownLocation().getLongitude());
                        locationComponent.setCameraMode(CameraMode.TRACKING);
                        locationComponent.zoomWhileTracking(15);            try {
                            getCurrentLocationDeatial(location.getLatitude(),location.getLongitude());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.i("location: ",location.toString());
                    }catch (Exception e){
                        Log.d("exception",e.getMessage());
                    }
                }
            });
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void pulseAnimation(){
        objAnim= ObjectAnimator.ofPropertyValuesHolder(risk, PropertyValuesHolder.ofFloat("scaleX", 0.9f), PropertyValuesHolder.ofFloat("scaleY", 0.9f));
        objAnim.setDuration(1200);
        objAnim.setRepeatCount(ObjectAnimator.INFINITE);
        objAnim.setRepeatMode(ObjectAnimator.REVERSE);
        objAnim.start();
    }

    private void stopPulseAnimation(){
        objAnim.cancel();
    }

    private void initSearchFab() {
        findViewById(R.id.fab_location_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.access_token))
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#EEEEEE"))
                                .limit(10)
                                .country("au")
                                .addInjectedFeature(home)
                                .addInjectedFeature(work)
                                .build(PlaceOptions.MODE_CARDS)
                        )
                        .build(FloodActivity.this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);

            }
        });
    }

    public void parkMarks(LatLng latLng, String snippet) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Bushfire");
        markerOptions.snippet(snippet);
        IconFactory iconFactory = IconFactory.getInstance(this);
        Icon icon = iconFactory.fromResource(R.drawable.flood);
        markerOptions.setIcon(icon);
        try {
            map.addMarker(markerOptions);
        }catch (Exception e){
            System.out.println("FUCK");
            e.getStackTrace();
        }
    }

    public void getCurrentLocationDeatial(double  latitude,double longtitude) throws IOException {
        Geocoder geocoder = new Geocoder(this);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude,longtitude,1);
            if (addresses.size() > 0) {
                Address add = addresses.get(0);
                address = add;
//                Log.i("address",address.getAddressLine(0));
                postCode = add.getPostalCode();
//                Log.i("postcode",postCode);
                getFloodDetailAsyncTask getFloodDetailAsyncTask= new getFloodDetailAsyncTask();
                getFloodDetailAsyncTask.execute(postCode);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void addUserLocations() {
        home = CarmenFeature.builder().text("Home")
                .geometry(Point.fromLngLat(-122.3964485, 37.7912561))
                .placeName("50 Beale St, San Francisco, CA")
                .id("mapbox-sf")
                .properties(new JsonObject())
                .build();

        work = CarmenFeature.builder().text("Work")
                .placeName("740 15th Street NW, Washington DC")
                .geometry(Point.fromLngLat(-77.0338348, 38.899750))
                .id("mapbox-dc")
                .properties(new JsonObject())
                .build();
    }

    @Override
    public void onLocationComponentClick() {

    }

    public class getNewsAsyncTask extends AsyncTask<String, Void, String> {

        //here is to get the parks using AsyncTask method
        @Override
        protected String doInBackground(String... strings) {
            return Restful.findAllFloodAlerts();
        }

        @Override
        protected void onPostExecute(String details) {
            JSONArray jsonArray = null;

//            JSONObject jsonObject = null;
            Integer count = 0;
            try {
//                jsonObject = new JSONObject(details);
                jsonArray = new JSONArray(details);
                count = jsonArray.length();
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            try {


//                JSONArray jarray = jsonObject.getJSONArray("features");
//                JSONObject
//                Log.i("count", count.toString());
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    try {
                        JSONObject j = jsonArray.getJSONObject(i);
                        Double lat = Double.parseDouble(j.getString("latitude"));
                        Double longti = Double.parseDouble(j.getString("longitude"));

                        LatLng latLng = new LatLng(lat, longti);

                        String markerSnippet = "Location: " + j.getString("location") +
                                "\n Updated on: " + j.getString("alertUpdated");
                        Log.i("wtf happened here", j.toString());

                        if (j.getString("location") != null) {
                            parkMarks(latLng, markerSnippet);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }
//            } catch (JSONException e) {
//                e.printStackTrace();
        }

    }



    private class getFloodDetailAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            return Restful.findFloodByPostcode(params[0]);
        }

        @Override
        protected void onPostExecute(String details) {
            JSONArray jsonArray = null;
            Log.i("life","So boring");
            try {
                jsonArray = new JSONArray(details);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (jsonArray.length()>0) {
                    j = jsonArray.getJSONObject(0);
                    risk.setText(j.getString("floodRiskRating"));
                    Log.i("Json J",j.toString());
                    lastupdated.setText("Updated："+ j.getString("lastUpdated"));
                    location_address.setText( address.getAddressLine(0));
                    btn_airPressure.setText((j.get("airPressure")).toString() + " hPa");
                    btn_humi.setText((j.get("humidity")).toString() + "%");
                    btn_rainfall.setText((j.get("rainfall")).toString() + " mm");
                }
                else
                {
                    risk.setText("Not Available");
                    riskString = risk.toString();
                }
            } catch (JSONException e) {
                risk.setText("Not Available");
                Log.i("RRDG", "onPostExecute: ");

                e.printStackTrace();
            }

        }


    }
}
