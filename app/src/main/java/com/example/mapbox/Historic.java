package com.example.mapbox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.HeatmapLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.heatmapDensity;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.linear;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgb;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgba;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.expressions.Expression.zoom;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleStrokeColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleStrokeWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapIntensity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapWeight;

public class Historic extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {
    private PermissionsManager permissionsManager;
    private MapView mapView;
    private MapboxMap map;
    private String postCode;
    private LocationEngine locationEngine;
    private LocationChangeListeningActivityLocationCallback callback =
            new LocationChangeListeningActivityLocationCallback(this);
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 3000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 500;
    private JSONObject j = new JSONObject();
    private static final String EARTHQUAKE_SOURCE_URL = "https://disastermateapi.azurewebsites.net/historicalbushfires.geojson";
    private static final String EARTHQUAKE_SOURCE_ID = "earthquakes";
    private static final String HEATMAP_LAYER_ID = "earthquakes-heat";
    private static final String HEATMAP_LAYER_SOURCE = "earthquakes";
    private static final String CIRCLE_LAYER_ID = "earthquakes-circle";

    private Toolbar mTopToolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiZnprODg4IiwiYSI6ImNqemh1a3M4MzB6eGgzbmxrMWx0c3Q3b3AifQ.--BckGBvrRT-TXTMJsaDAA");
        setContentView(R.layout.activity_historic);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
//        getDetailAsyncTask getDetailAsyncTask =new getDetailAsyncTask();
//        getDetailAsyncTask.execute("3125");

        //adding a back menu
        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                map = mapboxMap;
                mapboxMap.setStyle(Style.DARK, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        addEarthquakeSource(style);
                        addHeatmapLayer(style);
                        addCircleLayer(style);

                        //enableLocationComponent(style);
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


    }

    private void addEarthquakeSource(@NonNull Style loadedMapStyle) {
        try {
            loadedMapStyle.addSource(new GeoJsonSource(EARTHQUAKE_SOURCE_ID, new URI(EARTHQUAKE_SOURCE_URL)));
            Log.i("geojson",loadedMapStyle.getSource(EARTHQUAKE_SOURCE_ID).toString());
        } catch (URISyntaxException uriSyntaxException) {
            Log.e("error",uriSyntaxException.getMessage());
        }
    }

    private void addHeatmapLayer(@NonNull Style loadedMapStyle) {
        HeatmapLayer layer = new HeatmapLayer(HEATMAP_LAYER_ID, EARTHQUAKE_SOURCE_ID);
        layer.setMaxZoom(9);
        layer.setSourceLayer(HEATMAP_LAYER_SOURCE);
        layer.setProperties(

// Color ramp for heatmap.  Domain is 0 (low) to 1 (high).
// Begin color ramp at 0-stop with a 0-transparency color
// to create a blur-like effect.
                heatmapColor(
                        interpolate(
                                linear(), heatmapDensity(),
                                literal(0), rgba(33, 102, 172, 0),
                                literal(0.2), rgb(103, 169, 207),
                                literal(0.4), rgb(209, 229, 240),
                                literal(0.6), rgb(253, 219, 199),
                                literal(0.8), rgb(239, 138, 98),
                                literal(1), rgb(178, 24, 43)
                        )
                ),

// Increase the heatmap weight based on frequency and property magnitude
                heatmapWeight(
                        interpolate(
                                linear(), get("power"),
                                stop(0, 0),
                                stop(6, 1)
                        )
                ),

// Increase the heatmap color weight weight by zoom level
// heatmap-intensity is a multiplier on top of heatmap-weight
                heatmapIntensity(
                        interpolate(
                                linear(), zoom(),
                                stop(0, 1),
                                stop(9, 3)
                        )
                ),

// Adjust the heatmap radius by zoom level
                heatmapRadius(
                        interpolate(
                                linear(), zoom(),
                                stop(0, 2),
                                stop(9, 20)
                        )
                ),

// Transition from heatmap to circle layer by zoom level
                heatmapOpacity(
                        interpolate(
                                linear(), zoom(),
                                stop(7, 1),
                                stop(9, 0)
                        )
                )
        );

        loadedMapStyle.addLayerAbove(layer, "waterway-label");
    }

    private void addCircleLayer(@NonNull Style loadedMapStyle) {
        CircleLayer circleLayer = new CircleLayer(CIRCLE_LAYER_ID, EARTHQUAKE_SOURCE_ID);
        circleLayer.setProperties(

// Size circle radius by earthquake magnitude and zoom level
                circleRadius(
                        interpolate(
                                linear(), zoom(),
                                literal(7), interpolate(
                                        linear(), get("power"),
                                        stop(1, 1),
                                        stop(6, 4)
                                ),
                                literal(16), interpolate(
                                        linear(), get("power"),
                                        stop(1, 5),
                                        stop(6, 50)
                                )
                        )
                ),

// Color circle by earthquake magnitude
                circleColor(
                        interpolate(
                                linear(), get("power"),
                                literal(1), rgba(33, 102, 172, 0),
                                literal(2), rgb(103, 169, 207),
                                literal(3), rgb(209, 229, 240),
                                literal(4), rgb(253, 219, 199),
                                literal(5), rgb(239, 138, 98),
                                literal(6), rgb(178, 24, 43)
                        )
                ),

// Transition from heatmap to circle layer by zoom level
                circleOpacity(
                        interpolate(
                                linear(), zoom(),
                                stop(7, 0),
                                stop(8, 1)
                        )
                ),
                circleStrokeColor("white"),
                circleStrokeWidth(1.0f)
        );

        loadedMapStyle.addLayerBelow(circleLayer, HEATMAP_LAYER_ID);
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

        private final WeakReference<Historic> activityWeakReference;

        LocationChangeListeningActivityLocationCallback(Historic activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            Historic activity = activityWeakReference.get();

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
            Historic activity = activityWeakReference.get();
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
//    private class getDetailAsyncTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... params) {
//
//            return Restful.findByPostcode(params[0]);
//        }
//
//        @Override
//        protected void onPostExecute(String details) {
//            JSONArray jsonArray = null;
//            try {
//                jsonArray = new JSONArray(details);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            try {
//                j = jsonArray.getJSONObject(0);
//                risk.setText(j.getString("bushfireRiskRating"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }


    //adding the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.back) {

            //Add logic here
            Intent intent = new Intent(Historic.this,MainActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    }
