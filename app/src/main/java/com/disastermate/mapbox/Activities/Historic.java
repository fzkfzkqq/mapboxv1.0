package com.disastermate.mapbox.Activities;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.disastermate.mapbox.R;
import com.disastermate.mapbox.other.BushfireModel;
import com.disastermate.mapbox.other.HistoricfireModel;
import com.disastermate.mapbox.other.Restful;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
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

public class Historic extends BaseDrawerActivity implements OnMapReadyCallback, PermissionsListener {
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
    private static final String EARTHQUAKE_SOURCE_ID = "bushfire";
    private static final String HEATMAP_LAYER_ID = "earthquakes-heat";
    private static final String HEATMAP_LAYER_SOURCE = "bushfire";
    private static final String CIRCLE_LAYER_ID = "earthquakes-circle";
    private boolean isMarkShown = false;
    private Toolbar mTopToolbar;
    private TextView month,count;


    private List<HistoricfireModel> Jan = new ArrayList<>();
    private List<HistoricfireModel> Feb = new ArrayList<>();
    private List<HistoricfireModel> Mar = new ArrayList<>();
    private List<HistoricfireModel> Apr = new ArrayList<>();
    private List<HistoricfireModel> May = new ArrayList<>();
    private List<HistoricfireModel> Jun = new ArrayList<>();
    private List<HistoricfireModel> Jul = new ArrayList<>();
    private List<HistoricfireModel> Aug = new ArrayList<>();
    private List<HistoricfireModel> Sep = new ArrayList<>();
    private List<HistoricfireModel> Oct = new ArrayList<>();
    private List<HistoricfireModel> Nov = new ArrayList<>();
    private List<HistoricfireModel> Dec = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiZnprODg4IiwiYSI6ImNqemh1a3M4MzB6eGgzbmxrMWx0c3Q3b3AifQ.--BckGBvrRT-TXTMJsaDAA");
        getLayoutInflater().inflate(R.layout.activity_historic, frameLayout);
        BaseDrawerActivity.toolbar.setTitle("Historic Bushfires");


//        setContentView(R.layout.activity_historic);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        month = findViewById(R.id.month);
        count = findViewById(R.id.count);
//        getDetailAsyncTask getDetailAsyncTask =new getDetailAsyncTask();
//        getDetailAsyncTask.execute("3125");




        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                map = mapboxMap;
                mapboxMap.setStyle(Style.DARK, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        addEarthquakeSource(style);
                        addHeatmapLayer(style);
//                        addCircleLayer(style);

                        // Initialize the Seekbar slider
                        final SeekBar liveWithinMinutesSeekbar =
                                findViewById(R.id.isochrone_minute_seekbar_slider);
                        liveWithinMinutesSeekbar.setMax(12);
                        liveWithinMinutesSeekbar.incrementProgressBy(1);
                        liveWithinMinutesSeekbar.setProgress(0);



                        liveWithinMinutesSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    switch (progress) {
                                        case 0:
                                            iteratelist(Jan);
                                            month.setText("January");
                                            count.setText(Jan.size() + " Fires");
                                            break;
                                        case 1:
                                            iteratelist(Feb);
                                            month.setText("February");
                                            count.setText(Feb.size() + " Fires");
                                            break;
                                        case 2:
                                            iteratelist(Mar);
                                            month.setText("March");
                                            count.setText(Mar.size() + " Fires");
                                            break;
                                        case 3:
                                            iteratelist(Apr);
                                            month.setText("April");
                                            count.setText(Apr.size() + " Fires");
                                            break;
                                        case 4:
                                            iteratelist(May);
                                            month.setText("May");
                                            count.setText(May.size() + " Fires");
                                            break;
                                        case 5:
                                            iteratelist(Jun);
                                            month.setText("June");
                                            count.setText(Jun.size() + " Fires");
                                            break;
                                        case 6:
                                            iteratelist(Jul);
                                            month.setText("July");
                                            count.setText(Jul.size() + " Fires");
                                            break;
                                        case 7:
                                            iteratelist(Aug);
                                            month.setText("August");
                                            count.setText(Aug.size() + " Fires");
                                            break;
                                        case 8:
                                            iteratelist(Sep);
                                            month.setText("September");
                                            count.setText(Sep.size() + " Fires");
                                            break;
                                        case 9:
                                            iteratelist(Oct);
                                            month.setText("October");
                                            count.setText(Oct.size() + " Fires");
                                            break;
                                        case 10:
                                            iteratelist(Nov);
                                            month.setText("November");
                                            count.setText(Nov.size() + " Fires");
                                            break;
                                        case 11:
                                            iteratelist(Dec);
                                            month.setText("December");
                                            count.setText(Dec.size() + " Fires");
                                            break;
                                }
                            }
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
// Not needed in this example.
                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                            }
                        });

// Map is set up and the style has loaded. Now you can add data or make other map adjustments
                    }
                });

            }
        });


        findViewById(R.id.show_historic_marker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isMarkShown){
                    isMarkShown = true;
                    GetParks getpark = new GetParks();
                    getpark.execute();
                    Toast.makeText(Historic.this, "Drag the seekbar to view bushfires for each month", Toast.LENGTH_SHORT).show();
                    month.setText("January");
                    count.setText(Jan.size() + " Fires");
                }else if(isMarkShown) {
                    map.removeAnnotations();
                    isMarkShown = false;
                    Jan.clear();Feb.clear();Mar.clear();Apr.clear();May.clear();Jun.clear();
                    Jul.clear();Aug.clear();Sep.clear();Oct.clear();Nov.clear();Dec.clear();
                    Toast.makeText(Historic.this, "Slider disabled", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void addEarthquakeSource(@NonNull Style loadedMapStyle) {
        try {
            loadedMapStyle.addSource(new GeoJsonSource(EARTHQUAKE_SOURCE_ID, new URI(EARTHQUAKE_SOURCE_URL)));
//            Log.i("geojson",loadedMapStyle.getSource(EARTHQUAKE_SOURCE_ID).toString());
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
        navigationView.getMenu().getItem(1).setChecked(true);
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
//                Log.i("postcode",postCode);
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

//
//    //adding the toolbar
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.back) {
//
//            //Add logic here
//            Intent intent = new Intent(Historic.this,MainActivity.class);
//            startActivity(intent);
//
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    //here is to get the parks using AsyncTask method
    private class GetParks extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return Restful.findAllBFRecords();
        }
        @Override
        protected void onPostExecute(String details) {

            JSONObject jsonObject = null;
            Integer count = 0;
            try {
                jsonObject = new JSONObject(details);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                JSONArray jarray = jsonObject.getJSONArray("features");
                count = jarray.length();
                Log.i("count",count.toString());
                if (count > 0 ) {
                    for (int i = 0; i < count;i++)
                    {
                        try {
                            JSONObject j = jarray.getJSONObject(i);

                            Double lat  = Double.parseDouble(j.getJSONObject("geometry").getJSONArray("coordinates").getString(1));
                            Double longti = Double.parseDouble(j.getJSONObject("geometry").getJSONArray("coordinates").getString(0));
                            String temperature = j.getJSONObject("properties").getString("temperature");
                            String power = j.getJSONObject("properties").getString("power");
                            String month = j.getJSONObject("properties").getString("month");
                            String date = j.getJSONObject("properties").getString("date");
                            LatLng latLng = new LatLng(lat,longti);
                            String markerSnippet = "temprature: " + temperature +
                                    "\n power: " + power;

                            HistoricfireModel historicfireModel = new HistoricfireModel(lat,longti,power,temperature,month,date);

                            switch(historicfireModel.getMonth()) {
                                case "January":
                                    Jan.add(historicfireModel);
                                    Log.i("December","Jan Calling!");
                                    break;
                                case "February":
                                    Feb.add(historicfireModel);
                                    Log.i("December","Feb Calling!");
                                    break;
                                case "March":
                                    Mar.add(historicfireModel);
                                    Log.i("December","Mar Calling!");
                                    break;
                                case "April":
                                    Apr.add(historicfireModel);
                                    Log.i("December","Apr Calling!");
                                    break;
                                case "May":
                                    May.add(historicfireModel);
                                    Log.i("December","May Calling!");
                                    break;
                                case "June":
                                    Jun.add(historicfireModel);
                                    Log.i("December","Jun Calling!");
                                    break;
                                case "July":
                                    Jul.add(historicfireModel);
                                    Log.i("December","Jul Calling!");
                                    break;
                                case "August":
                                    Aug.add(historicfireModel);
                                    Log.i("December","Aug Calling!");
                                    break;
                                case "September":
                                    Sep.add(historicfireModel);
                                    Log.i("December","Sep Calling!");
                                    break;
                                case "October":
                                    Oct.add(historicfireModel);
                                    Log.i("December","Oct Calling!");
                                    break;
                                case "November":
                                    Nov.add(historicfireModel);
                                    Log.i("December","Nov Calling!");
                                    break;
                                case "December":
                                    Dec.add(historicfireModel);
                                    Log.i("December","December Calling!");
                                    break;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
    //here is to put marks for parks
    private void parkMarks(LatLng latLng,String snippet) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Bushfire");
        markerOptions.snippet(snippet);
        IconFactory iconFactory = IconFactory.getInstance(Historic.this);
        Icon icon = iconFactory.fromResource(R.drawable.firehistoric);
        markerOptions.setIcon(icon);
        map.addMarker(markerOptions);
    }


    public void iteratelist(List<HistoricfireModel> list){
        map.removeAnnotations();

        Iterator itr = list.iterator();

        while(itr.hasNext()){
            HistoricfireModel bushfireModel = (HistoricfireModel) itr.next();
            LatLng latLng = new LatLng(bushfireModel.getLatitude(),bushfireModel.getLongitude());
            String markerSnippet = "Temperature: " + bushfireModel.getTemperature() +
                    "\n Power: " + bushfireModel.getPower() + "\nDate " + bushfireModel.getDate();

            parkMarks(latLng,markerSnippet);
        }

    }

}
