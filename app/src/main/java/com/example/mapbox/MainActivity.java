package com.example.mapbox;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.OnCameraTrackingChangedListener;
import com.mapbox.mapboxsdk.location.OnLocationClickListener;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.pluginscalebar.ScaleBarOptions;
import com.mapbox.pluginscalebar.ScaleBarPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;


import static com.example.mapbox.Notifications.CHANNEL_1_ID;
import static com.example.mapbox.Notifications.CHANNEL_2_ID;



public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, OnCameraTrackingChangedListener,OnLocationClickListener {
    private static  String PREFS_NAME = "Prefs" ;
    private PermissionsManager permissionsManager;
    private MapView mapView;
    private MapboxMap map;
    private Button test;
    private String postCode;
    private LocationEngine locationEngine;
    private TextView risk;
    private Address address;
    private LocationChangeListeningActivityLocationCallback callback =
    new LocationChangeListeningActivityLocationCallback(this);
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 3000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 500;
    private JSONObject j = new JSONObject();
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";
    private ImageButton search;
    private EditText input_postcode;
//TODO: Nik
    private Button btn_c_findmore;
    private Button btn_action_exp;

    private android.support.v7.widget.Toolbar mTopToolbar;


    private boolean isInTrackingMode;
    private  LocationComponent locationComponent;

    private Button dialogue_button;

    private TextView lastupdated;
    private TextView location_address;

    private Button bushfire;


    private ObjectAnimator objAnim;

    private EditText editTextTitle;
    private EditText editTextMessage;
    private NotificationManagerCompat notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiZnprODg4IiwiYSI6ImNqemh1a3M4MzB6eGgzbmxrMWx0c3Q3b3AifQ.--BckGBvrRT-TXTMJsaDAA");
        setContentView(R.layout.activity_main);



        //adding a back menu
        mTopToolbar =  findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);


        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
//        test = findViewById(R.id.btn_findmore);
        risk = (TextView) findViewById(R.id.text_riskrate);
        final Geocoder geocoder = new Geocoder(this);
        search = (ImageButton) findViewById(R.id.btn_search);
        btn_c_findmore = findViewById(R.id.btn_c_findmore);
        btn_action_exp = findViewById(R.id.btn_action_exp);
//        btn_historical_bf = findViewById(R.id.btn_historical_bf);
        input_postcode = findViewById(R.id.search_location);
        lastupdated = findViewById(R.id.lastupdated);

        location_address = findViewById(R.id.location_address);
        bushfire = findViewById(R.id.bushfire);


        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        notificationManager = NotificationManagerCompat.from(this);

        Boolean isAgree = sharedpreferences.getBoolean("d_accepted",false);

        if (isAgree == false) {

            final Dialog settingsDialog = new Dialog(this);
            settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.image_layout
                    , null));
            settingsDialog.show();

            dialogue_button = settingsDialog.findViewById(R.id.dialogue_button);
            dialogue_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("d_accepted", true);
                    editor.commit();
                    settingsDialog.dismiss();
                }
            });

        }

        bushfire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CauseActivity.bounceBaby(bushfire);
                Intent intent = new Intent(MainActivity.this,Historic.class);
                startActivity(intent);
                finish();
            }
        });

        btn_c_findmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CauseActivity.bounceBaby(btn_c_findmore);
                Intent intent = new Intent(MainActivity.this, CauseActivity.class);
                startActivity(intent);

            }
        });


        btn_action_exp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                CauseActivity.bounceBaby(btn_action_exp);
                Intent intent = new Intent(MainActivity.this, ActionActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Address search_add;

                try {
                    if (!geocoder.getFromLocationName((input_postcode.getText().toString()
                            + " Victora,Australia"),1).isEmpty())
                    {
                        locationEngine.removeLocationUpdates(callback);
                        search_add = geocoder.getFromLocationName((input_postcode.getText().toString()+ " Victora,Australia"),1).get(0);
                        address = search_add;
                        getDetailAsyncTask getSearchDeatilAsyncTask =new getDetailAsyncTask();
                        getSearchDeatilAsyncTask.execute(search_add.getPostalCode());

                        try {
                            map.addMarker(new MarkerOptions()
                                    .position(new LatLng(search_add.getLatitude(), search_add.getLongitude()))
                                    .title(address.getAddressLine(0) + "\n Risk Rate: " + j.getString("bushfireRiskRating") ));
                            location_address.setText("Location:" + address.getAddressLine(0));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(search_add.getLatitude(), search_add.getLongitude())) // Sets the new camera position
                                .build(); // Creates a CameraPosition from the builder

                        map.animateCamera(CameraUpdateFactory
                                .newCameraPosition(position), 7000);
                    }
                    else {
                        input_postcode.setError("No address found");
                        input_postcode.setText("");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    input_postcode.setError("No address found");
                }
            }
        });


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                map = mapboxMap;

                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        ScaleBarPlugin scaleBarPlugin = new ScaleBarPlugin(mapView, mapboxMap);

                        // Create a ScaleBarOptions object to use the Plugin's default styling
                        scaleBarPlugin.create(new ScaleBarOptions(MainActivity.this)
                                .setTextSize(20f)
                                .setBarHeight(10f)
                                .setBorderWidth(7f)
                                .setMetricUnit(true)
                                .setRefreshInterval(15)
                                .setMarginTop(30f)
                                .setMarginLeft(16f)
                                .setTextBarMargin(15f));

                        enableLocationComponent(style);
                        // Create an empty GeoJSON source using the empty feature collection
                        setUpSource(style);

// Set up a new symbol layer for displaying the searched location's feature coordinates
                        setupLayer(style);
// Map is set up and the style has loaded. Now you can add data or make other map adjustments
                    }
                });
                //TODO: 1. see the other TODO code at line 236
//                //addMarker(mapboxMap);
//                GetParks getpark = new GetParks();
//                getpark.execute();
//                mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
//                    @Override
//                    public boolean onMarkerClick(@NonNull Marker marker) {
//                        return false;
//                    }
//                });
            }
        });
        risk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mapView.setVisibility(View.INVISIBLE);
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.content_frame, new
//                        Details()).commit();

//                CauseActivity.bounceBaby(risk);

                Intent intent = new Intent(MainActivity.this,Details.class);
                Bundle bundle = new Bundle();
                bundle.putString("Address1",address.getAddressLine(0));
                bundle.putString("postcode",address.getPostalCode());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

//        blink();
        pulseAnimation();
    }



    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[] {0f, -8f})
        ));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
            // Then retrieve and update the source designated for showing a selected location's symbol layer icon

            if (map != null) {
                Style style = map.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }

                    // Move map camera to the selected location
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 4000);
                }
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
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

    @SuppressWarnings( {"MissingPermission"})
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
            initLocationEngine();

// Add the location icon click listener
            locationComponent.addOnLocationClickListener((OnLocationClickListener) this);

            findViewById(R.id.back_to_camera_tracking_mode).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    locationEngine.getLastLocation(callback);
                    if (!isInTrackingMode) {
                        isInTrackingMode = true;
                        locationComponent.setCameraMode(CameraMode.TRACKING);
                        locationComponent.zoomWhileTracking(15);
//                        Toast.makeText(MainActivity.this, getString(R.string.tracking_enabled),
//                                Toast.LENGTH_SHORT).show();
                    } else {
                        locationComponent.setCameraMode(CameraMode.TRACKING);
                        locationComponent.zoomWhileTracking(15);
//                        Toast.makeText(MainActivity.this, getString(R.string.tracking_already_enabled),
//                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onCameraTrackingDismissed() {
        isInTrackingMode = false;
    }

    @Override
    public void onCameraTrackingChanged(int currentMode) {
        isInTrackingMode = false;
    }


    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @Override
    public void onLocationComponentClick() {
//        if (locationComponent.getLastKnownLocation() != null) {
//            Toast.makeText(this, String.format(getString(R.string.current_location),
//                    locationComponent.getLastKnownLocation().getLatitude(),
//                    locationComponent.getLastKnownLocation().getLongitude()), Toast.LENGTH_LONG).show();
//        }
    }

    /*
    private void addMarker(MapboxMap mapboxMap) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.title("Your home");
        markerOptions.snippet(appUser.getAddress());
        markerOptions.setIcon(icon);
        mapboxMap.addMarker(markerOptions);
    }
*/
//TODO: 1. jsonArray shows a null pointer exception due to json being changed to GeoJson
//    //here is to get the parks using AsyncTask method
//    private class GetParks extends AsyncTask<Void, Void, String> {
//        @Override
//        protected String doInBackground(Void... params) {
//
//            return Restful.findAllBFRecords();
//        }
//        @Override
//        protected void onPostExecute(String Details) {
//            JSONArray jsonArray = null;
//            try {
//                jsonArray = new JSONArray(Details);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            if (jsonArray.length() > 0 ) {
//                for (int i = 0; i < jsonArray.length();i++)
//                {
//                    try {
//                        JSONObject j = jsonArray.getJSONObject(i);
//                        Double lat  = (Double)(j.getDouble("latitude"));
//                        Double longti = (Double)(j.getDouble("longitude"));
//                        LatLng latLng = new LatLng(lat,longti);
//                        String snippet = "power:" + ((Double)j.getDouble("power")).toString()+
//                                "\nLongitude:" + longti.toString()+
//                                "\nDatetime:" + j.get("datetime")+
//                                "\nlatitude:" + lat.toString()+
//                                "\ntemp:" + ((Double)j.getDouble("temp_kelvin")).toString();
//                        fireMarks(latLng,snippet);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }}
//    //here is to put marks for parks
//    private void fireMarks(LatLng latLng,String snippet) {
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("fire");
//        markerOptions.snippet(snippet);
//        //IconFactory iconFactory = IconFactory.getInstance(getActivity());
//        //Icon icon = iconFactory.fromResource(R.drawable.mapbox_compass_icon);
//        //markerOptions.setIcon(icon);
//        map.addMarker(markerOptions);
//    }


    private static class LocationChangeListeningActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MainActivity> activityWeakReference;

        LocationChangeListeningActivityLocationCallback(MainActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            MainActivity activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

// Create a Toast which displays the new location's coordinates
//                Log.i("coordinate",result.toString());
                //TODO: 2. The toast location keeps showing with even small changes in location
//                Toast.makeText(activity,
//                        String.valueOf(result.getLastLocation().getLatitude()),
//                        Toast.LENGTH_SHORT).show();

                        //TODO: 2. The toast location keeps showing with even small changes in location
//                        Toast.makeText(activity,
//                        String.valueOf(result.getLastLocation().getLongitude()),
//                        Toast.LENGTH_SHORT).show();

// Pass the new location to the Maps SDK's LocationComponent
                if (activity.map != null && result.getLastLocation() != null) {
                    activity.map.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                }
                try {
                    activity.getCurrentPostCode(result.getLastLocation().getLatitude(),
                            result.getLastLocation().getLongitude());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can't be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
//            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void getCurrentPostCode(double  latitude,double longtitude) throws IOException {
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
                getDetailAsyncTask getDetailAsyncTask = new getDetailAsyncTask();
                getDetailAsyncTask.execute(postCode);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private class getDetailAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            return Restful.findByPostcode(params[0]);
        }

        @Override
        protected void onPostExecute(String details) {
            JSONArray jsonArray = null;

            try {
                jsonArray = new JSONArray(details);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (jsonArray.length()>0) {
                    j = jsonArray.getJSONObject(0);
                    risk.setText(j.getString("bushfireRiskRating"));
                    lastupdated.setText("Last Updated on " + j.getString("lastUpdated"));
                    location_address.setText("Location:" + address.getAddressLine(0));

                }
                else
                {
                    risk.setText("Not Available for this location");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

//To Stop Animation, simply use cancel method of ObjectAnimator object as below.

    private void stopPulseAnimation(){
    objAnim.cancel();
}



    public void sendOnChannel1(View v) {
//        String title = editTextTitle.getText().toString();
//        String message = editTextMessage.getText().toString();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Hi")
                .setContentText("notification warning")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();

        notificationManager.notify(1, notification);
    }

    public void sendOnChannel2(View v) {
//        String title = editTextTitle.getText().toString();
//        String message = editTextMessage.getText().toString();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("HI!!")
                .setContentText("Notification news")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        notificationManager.notify(2, notification);
    }


}

