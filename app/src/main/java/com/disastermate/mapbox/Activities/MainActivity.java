package com.disastermate.mapbox.Activities;

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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.disastermate.mapbox.R;
import com.disastermate.mapbox.other.Restful;
import com.google.gson.JsonObject;
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
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
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
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.zolad.zoominimageview.ZoomInImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import static com.disastermate.mapbox.other.Notifications.CHANNEL_2_ID;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;




public class MainActivity extends BaseDrawerActivity implements OnMapReadyCallback, PermissionsListener, OnCameraTrackingChangedListener,OnLocationClickListener {

    /*Declarations*/
    private static  String PREFS_NAME = "Prefs" ;
    private PermissionsManager permissionsManager;
    private MapView mapView;
    private MapboxMap map;
    private Button test;
    private String postCode;
    private LocationEngine locationEngine;
    private LocationComponent locationComponent;
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
    private ZoomInImageView zoomInImageView;
//    private EditText input_postcode;
    private Button btn_temp;
    private Button btn_humi;
    private Button btn_wind;
    private Button btn_pressure;
    private boolean isInTrackingMode;
    private Button dialogue_button;
    private TextView lastupdated;
    public static TextView location_address;
    private ObjectAnimator objAnim;
    private NotificationManagerCompat notificationManager;
    private CarmenFeature home;
    private CarmenFeature work;
    String riskString;
    private Button gotItButton;
    private ImageView dismissButton;
    private ProgressBar progressBar;
    private Dialog progressDialog;
    private static Boolean progressFlag = false;
    private static double lati;
    private static double loni;
    private MenuInflater inflater;
    private FloatingActionButton btn_help;
    private TextView factor_description;
    public static Location mylocation;
    private float distance = 0;
    private boolean isOpen = false;
    Date date1;
    SimpleDateFormat format;
    private SimpleDateFormat format2;

    private Set<String> bushfirelistpostcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /*Mapbox Key*/
        Mapbox.getInstance(this, "pk.eyJ1IjoiZnprODg4IiwiYSI6ImNqemh1a3M4MzB6eGgzbmxrMWx0c3Q3b3AifQ.--BckGBvrRT-TXTMJsaDAA");
//        setContentView(R.layout.activity_main);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);

        /*ToolBar : set your title here*/
//        mTopToolbar =  findViewById(R.id.my_toolbar);
//        setSupportActionBar(mTopToolbar);
//        setSupportActionBar(toolbar);
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
        BaseDrawerActivity.toolbar.setTitle("Bushfire Prediction");
        mylocation = new Location("me");



        /*Declare all UI to Objects herer*/
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        risk = (TextView) findViewById(R.id.text_riskrate);
//        search = (ImageButton) findViewById(R.id.btn_search);
        gotItButton = findViewById(R.id.btn_gotit);
        lastupdated = findViewById(R.id.lastupdated);
        btn_humi = findViewById(R.id.btn_humi);
        btn_pressure = findViewById(R.id.btn_pres);
        btn_temp = findViewById(R.id.btn_temp);
        btn_wind = findViewById(R.id.btn_wind);
        location_address = findViewById(R.id.location_address);
        zoomInImageView = findViewById(R.id.factor_image);
        factor_description = findViewById(R.id.factor_description);
        btn_help = (FloatingActionButton) findViewById(R.id.help);
        /*Declare other variables here*/
        final Geocoder geocoder = new Geocoder(this);
        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        notificationManager = NotificationManagerCompat.from(this);
        Boolean isAgree = sharedpreferences.getBoolean("d_accepted",false);
        riskString = "null";
        progressBar = findViewById(R.id.indeterminateBar);

        /*Make sure that the dialogue does not repeat twice*/
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

                        //TODO: Add search button function here initSearchFab() and addUserLocations() here
                        findViewById(R.id.fab_location_search).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                addUserLocations();
                                initSearchFab(1);
                            }});
                    }
                });
            }
        });


        /*That green button is clickable
        * Watch out! Wuuuu*/
        risk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Risk",risk.getText().toString());
                final Dialog settingsDialog = new Dialog(v.getContext());
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                if (risk.getText().toString().equals("LOW")){
                    Intent intent = new Intent(MainActivity.this,Flood2doList.class);
                    startActivity(intent);
                    settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.view_bushfirelow
                            , null));
                    settingsDialog.show();
                    Window window = settingsDialog.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialogue_button = settingsDialog.findViewById(R.id.dialogue_button);
                    dialogue_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            settingsDialog.dismiss();
                        }
                    });

                }
                else if (risk.getText().toString().equals("MEDIUM")){
                    settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.view_meidumbushfire
                            , null));
                    settingsDialog.show();
                    Window window = settingsDialog.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialogue_button = settingsDialog.findViewById(R.id.dialogue_button);
                    dialogue_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            settingsDialog.dismiss();
                        }
                    });
                }
                else if (risk.getText().toString().equals("HIGH")){
                    settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.view_highbushfire
                            , null));
                    settingsDialog.show();
                    Window window = settingsDialog.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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

        findViewById(R.id.change_flood).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), FloodActivity.class));
//                finish();
            }
        });

    gotItButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            factor_description.setVisibility(View.GONE);
            zoomInImageView.setVisibility(View.GONE);
            gotItButton.setVisibility(View.GONE);
        }
    });

    btn_temp.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            factor_description.setText("How temperature affects bush fire.\nLearn More at : http://www.bom.gov.au/climate/current/annual/aus/#tabs=Temperature");
            zoomInImageView.setImageResource(R.drawable.factor_temp);
               factor_description.setVisibility(View.VISIBLE);
               zoomInImageView.setVisibility(View.VISIBLE);
               gotItButton.setVisibility(View.VISIBLE);
        }
    });

        btn_humi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                factor_description.setText(" Relative humidity is commonly used to measure atmospheric moisture.\nLearn More at : http://www.bom.gov.au/lam/humiditycalc.shtml");
                zoomInImageView.setImageResource(R.drawable.factor_humi);
                factor_description.setVisibility(View.VISIBLE);
                zoomInImageView.setVisibility(View.VISIBLE);
                gotItButton.setVisibility(View.VISIBLE);
            }
        });

        btn_pressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                factor_description.setText(" Air pressure will push flames, sparks and firebrands into new fuel.\nLearn More at :http://www.bom.gov.au/australia/charts/viewer/?IDCODE=IDY00050 ");

                zoomInImageView.setImageResource(R.drawable.factor_airpres);
                factor_description.setVisibility(View.VISIBLE);
                zoomInImageView.setVisibility(View.VISIBLE);
                gotItButton.setVisibility(View.VISIBLE);

            }
        });

        btn_wind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                factor_description.setText(" How wind works during bush fire.\nLearn More about: http://www.bom.gov.au/marine/about/about-forecast-wind.shtml");
                zoomInImageView.setImageResource(R.drawable.factor_wind);
                factor_description.setVisibility(View.VISIBLE);
                zoomInImageView.setVisibility(View.VISIBLE);
                gotItButton.setVisibility(View.VISIBLE);
            }
        });

        btn_help.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            final Dialog settingsDialog = new Dialog(v.getContext());
                                            settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                                            settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.view_help
                                                    , null));
                                            settingsDialog.show();

                                            Window window = settingsDialog.getWindow();
                                            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);



                                            dialogue_button = settingsDialog.findViewById(R.id.okbutton);
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
                String data = event.getData();
                String location = "Carnegie";

                try {
                    JSONObject getData = new JSONObject(data);
                     location = (String) getData.get("location");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


//                Log.i("pusher",event.getProperty("location").toString());



                /*Provides with a default notification anytime the database is updated with a
                * bushfire alert. THIS IS REALTIME which makes it awesome
                * https://dashboard.pusher.com/apps/860496/console/realtime_messages
                * */
                    Notification notification = new NotificationCompat.Builder(MainActivity.this, CHANNEL_2_ID).setSmallIcon(R.drawable.logo)
                            .setContentTitle("Watch Out!")
                            .setContentText("BushFire at " + location)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000
                            })
                            .build();

                /*This is what actually triggers the alarm*/
                notificationManager.notify(2, notification);
            }
        });

        pusher.connect();

//        findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Dialog settingsDialog = new Dialog(getApplicationContext());
//                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//                settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.view_help
//                        , null));
//                settingsDialog.show();
//
//                Button dialogue_button = settingsDialog.findViewById(R.id.okbutton);
//                dialogue_button.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        settingsDialog.dismiss();
//
//                    }
//                });
//            }
//        });
        /*Just a stupid blinking animation that the mentors liked so I'll keep it*/
        pulseAnimation();

    }


/*some source and layer shit which does not make much sense to me. Must be for the night and day mode*/
    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[] {0f, -8f})
        ));
    }


    /*This is where you induce the search logic, after the intent has been called to the full screen search option (the yellow search button, dummy)
    * Took a braniac like myself to figure this one out lol*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Address search_add;
        Geocoder geocoder = new Geocoder(this);
        IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
        Icon icon = iconFactory.fromResource(R.drawable.placeholder);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);


            //removes any pending updates
            locationEngine.removeLocationUpdates(callback);

            try {

                search_add = geocoder.getFromLocationName(selectedCarmenFeature.placeName(), 1).get(0);
                address = search_add;

                getDetailAsyncTask getSearchDeatilAsyncTask = new getDetailAsyncTask();
                getSearchDeatilAsyncTask.execute(search_add.getPostalCode());

                try {

                    //add carmen location to the navbar and risk rating to nav bar
                    try {
                        distance = BushfireListActivity.getDistance(mylocation, address.getLatitude(), address.getLongitude());
                    }catch(Exception e){
                        distance = 0;
                    }
                     String risktext = j.getString("bushfireRiskRating");
                     Log.i("risktext",j.getString("bushfireRiskRating"));



                    location_address.setText("Location:" + address.getAddressLine(0));
                    risk.setText(j.getString("bushfireRiskRating"));
                    Log.i("risktext",j.getString("bushfireRiskRating"));

                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(search_add.getLatitude(), search_add.getLongitude()))
                            .icon(icon)
                            .title(address.getAddressLine(0))
                            .snippet("\nRisk Rate: " + risk.getText()
                                    + "\nDistance from Location: " + distance + " Km"));
                    Log.i("RRD", risk.getText().toString());

                   map.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 3000);


                } catch (JSONException e) {
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
        else
            if(resultCode == Activity.RESULT_OK && requestCode == 2){
                //logic to add carmen feature here
                // Retrieve selected location's CarmenFeature
                CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);


                //removes any pending updates
                locationEngine.removeLocationUpdates(callback);

                try {

                    //gets the address
                    search_add = geocoder.getFromLocationName(selectedCarmenFeature.placeName(), 1).get(0);
                    address = search_add;

                    //gets details for the current address
                    getDetailAsyncTask getSearchDeatilAsyncTask = new getDetailAsyncTask();
                    getSearchDeatilAsyncTask.execute(search_add.getPostalCode());

                    try {

                        // logic to add the address to as a carmen feature
                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(search_add.getLatitude(), search_add.getLongitude()))
                                .title(address.getAddressLine(0) + "\n Risk Rate: " + risk.getText().toString()));
                        Log.i("RRD", risk.getText().toString());

                        location_address.setText("Location:" + address.getAddressLine(0));
                        risk.setText(j.getString("bushfireRiskRating"));


                        map.animateCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                                ((Point) selectedCarmenFeature.geometry()).longitude()))
                                        .zoom(14)
                                        .build()), 3000);

                        //adds a menu into the watchlist
                        addMenuItemInNavMenuDrawer(address.getAddressLine(0),j.getString("bushfireRiskRating"),address.getPostalCode());


                    } catch (JSONException e) {
                        e.printStackTrace();


                        // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
                        // Then retrieve and update the source designated for showing a selected location's symbol layer icon

//                        if (map != null) {
//                            Style style = map.getStyle();
//                            if (style != null) {
//                                GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
//                                if (source != null) {
//                                    source.setGeoJson(FeatureCollection.fromFeatures(
//                                            new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())}));
//                                }
//
//                                // Move map camera to the selected location
//                                map.animateCamera(CameraUpdateFactory.newCameraPosition(
//                                        new CameraPosition.Builder()
//                                                .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
//                                                        ((Point) selectedCarmenFeature.geometry()).longitude()))
//                                                .zoom(14)
//                                                .build()), 7000);
//                            }
//                        }
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
        map.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public boolean onMapClick(@NonNull LatLng point) {

//                String string = String.format(Locale.US, "User clicked at: %s", point.toString());
//
//                Toast.makeText(MainActivity.this, string, Toast.LENGTH_LONG).show();
                return true;

            }
        });

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
        navigationView.getMenu().getItem(0).setChecked(true);

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
                locationComponent.addOnLocationClickListener(this);

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
        location_address.setText("Location:" + address.getAddressLine(0));
        try {
            risk.setText(j.getString("bushfireRiskRating"));
        } catch (JSONException e) {
            e.printStackTrace();
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
        if(mylocation != null)
        mylocation = map.getLocationComponent().getLastKnownLocation();




    }

    @Override
    public void onLocationComponentClick() {
        assert locationComponent.getLastKnownLocation() != null;
                    map.getLocationComponent().getLastKnownLocation().getLatitude();
                    map.getLocationComponent().getLastKnownLocation().getLongitude();
        Toast.makeText(MainActivity.this,
                        map.getLocationComponent().getLastKnownLocation().toString(),
                        Toast.LENGTH_LONG).show();
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
//        protected void onPostExecute(String DetailsActivity) {
//            JSONArray jsonArray = null;
//            try {
//                jsonArray = new JSONArray(DetailsActivity);
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
                mylocation = result.getLastLocation();

                if (mylocation == null) {

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
                    lati = result.getLastLocation().getLatitude();
                    loni = result.getLastLocation().getLongitude();

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

    /*This code puts up the H,M,L alert on the task bar when it is done for the first time*/
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
//                progressBar.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private class getDetailAsyncTask extends AsyncTask<String, Void, String> {

//        @Override
//        protected void onPreExecute() {
//
////        /**
////         * Setting up a Dialog box in the event that the application API calls takes time to load
////         * */
////        if(!progressFlag) {
////            progressDialog = new Dialog(MainActivity.this);
////            progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
////            progressDialog.setContentView(getLayoutInflater().inflate(R.layout.progressbar
////                    , null));
////            progressDialog.show();
////            progressFlag = true;
////        }
//
//        }
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
                onNodata();
                e.printStackTrace();
            }
            try {
                if (jsonArray.length()>0) {
                    j = jsonArray.getJSONObject(0);
                    risk.setText(j.getString("bushfireRiskRating"));
//                    risk.setBackgroundColor(Color.rgb(37,155,36));
                    lastupdated.setText("Updated："+ j.getString("lastUpdated"));
                    location_address.setText( address.getAddressLine(0));
                    btn_temp.setText((j.get("airTemperature")).toString() + "°C");
                    btn_humi.setText((j.get("humidity")).toString() + "%");
                    btn_wind.setText((j.get("windSpeed")).toString() + " Km/h");
                    btn_pressure.setText((j.get("airPressure")).toString() + " hPa");
                    if (j.getString("bushfireRiskRating").equals("MEDIUM"))
                    {
                        risk.setBackgroundColor(Color.rgb(255,174,55));
                    }
                    if (j.getString("bushfireRiskRating").equals("HIGH")){
                        risk.setBackgroundColor(Color.rgb(255,56,63));
                    }
                    SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat("Latitude",Float.parseFloat(String.valueOf(lati)));
                    editor.putFloat("Longitude",Float.parseFloat(String.valueOf(loni)));
                    editor.apply();
//                    if(progressFlag) {
//
//                        progressDialog.dismiss();
//                    }
                }
                else
                {
                   onNodata();
                    riskString = risk.toString();
                }
            } catch (JSONException e) {
                onNodata();
                Log.i("RRDG", "onPostExecute: ");

                e.printStackTrace();
            }

        }

    }

    private void onNodata(){
        risk.setText("No Data");
        risk.setBackgroundColor(Color.rgb(37,155,36));
        btn_humi.setText("No Data");
        btn_pressure.setText("No Data");
        btn_wind.setText("No Data");
        btn_temp.setText("No Data");
        lastupdated.setText("No Data");
        location_address.setText("No Data");
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




    //here is to put marks for parks
    public void parkMarks(LatLng latLng, String snippet) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Bushfire");
        markerOptions.snippet(snippet);
        IconFactory iconFactory = IconFactory.getInstance(this);
        Icon icon = iconFactory.fromResource(R.drawable.fire_current);
        markerOptions.setIcon(icon);
        try {
            map.addMarker(markerOptions);
        }catch (Exception e){
            System.out.println("FUCK");
            e.getStackTrace();
        }
    }


    public class getNewsAsyncTask extends AsyncTask<String, Void, String> {

        //here is to get the parks using AsyncTask method
        @Override
        protected String doInBackground(String... strings) {

//            notificationManager = NotificationManagerCompat.from(this);
//            Boolean isAgree = sharedpreferences.getBoolean("d_accepted",false);
            return Restful.findAllBFAlerts();
        }

        @SuppressLint("SimpleDateFormat")
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
                            Log.i("STATUSJSON",j.toString());
                            Double lat = Double.parseDouble(j.getString("latitude"));
                            Double longti = Double.parseDouble(j.getString("longitude"));
                            try {
                                if(mylocation != null)
                                    mylocation = map.getLocationComponent().getLastKnownLocation();
                                distance = BushfireListActivity.getDistance(mylocation, lat, longti);
                                Log.i("distance", String.valueOf(distance) + " " + String.valueOf(mylocation));
                            }catch(Exception e){
                                Toast.makeText(MainActivity.this, "Last Known Location Selected", Toast.LENGTH_LONG).show();
                                mylocation.setLatitude(-37.875261);
                                mylocation.setLongitude(145.044102);
                            }
                            LatLng latLng = new LatLng(lat, longti);

                            date1=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(j.getString("alertUpdated"));
                            Log.i("DATE PARSING",date1.toString());
                            format = new SimpleDateFormat("dd-MM-yyyy");
                            format2 = new SimpleDateFormat("hh:mm a");

                            format.applyPattern("dd-MM-yyyy");

                            String markerSnippet = "Location: " + j.getString("location") +
                                    "\nUpdated on: " + format.format(date1) +"\nTime: " + format2.format(date1) + "\nDistance from Current Location: " + distance + " Km" + "\nRisk Rate: Medium" + "\nStatus: " + j.getString("status");
                              Log.i("wtf happened here", j.toString());


                            if (j.getString("location") != null) {
                                parkMarks(latLng, markerSnippet);
                                SharedPreferences sharedpreferences;
                                sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor= sharedpreferences.edit();
                                editor.putString("BushfireAlerts",sharedpreferences.getString("BushfireAlerts",null) + "");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }



        }





    }




