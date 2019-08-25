package com.example.mapbox;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {
    private PermissionsManager permissionsManager;
    private MapView mapView;
    private MapboxMap map;
    private Button test;
    private String postCode;
    private LocationEngine locationEngine;
    private TextView risk;
    private LocationChangeListeningActivityLocationCallback callback =
            new LocationChangeListeningActivityLocationCallback(this);
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 3000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 500;
    private JSONObject j = new JSONObject();



//TODO: Nik
    private Button btn_c_findmore;
    private Button btn_action_exp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiZnprODg4IiwiYSI6ImNqemh1a3M4MzB6eGgzbmxrMWx0c3Q3b3AifQ.--BckGBvrRT-TXTMJsaDAA");
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        test = findViewById(R.id.btn_findmore);
        risk = (TextView) findViewById(R.id.text_riskrate);


        btn_c_findmore = findViewById(R.id.btn_c_findmore);
        btn_action_exp = findViewById(R.id.btn_action_exp);

        btn_c_findmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CauseActivity.class);
                startActivityForResult(intent, 1);

            }
        });


        btn_action_exp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActionActivity.class);
                startActivityForResult(intent, 1);
            }
        });


        getDetailAsyncTask getDetailAsyncTask =new getDetailAsyncTask();
        getDetailAsyncTask.execute("3125");
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                map = mapboxMap;
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
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
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mapView.setVisibility(View.INVISIBLE);
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.content_frame, new
//                        Details()).commit();

                Intent intent = new Intent(MainActivity.this,Details.class);
                startActivity(intent);
            }
        });


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
            LocationComponent locationComponent = map.getLocationComponent();

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
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
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
                Log.i("coordinate",result.toString());
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
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
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
                Address address = addresses.get(0);
                postCode = address.getPostalCode();
                Log.i("postcode",postCode);
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
                j = jsonArray.getJSONObject(0);
                risk.setText(j.getString("bushfireRiskRating"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
