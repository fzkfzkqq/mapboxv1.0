package com.d26.mapbox.drawerLayout;



import android.app.Fragment;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.d26.mapbox.MainActivity;
import com.d26.mapbox.R;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
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
import com.mapbox.pluginscalebar.ScaleBarOptions;
import com.mapbox.pluginscalebar.ScaleBarPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class MainFragment extends Fragment implements
        OnMapReadyCallback, PermissionsListener {
    private MapView mapView;
    private MapboxMap map;
    private View vmap;
    private LatLng location;
    private PermissionsManager permissionsManager;
    private Button btn_temp;
    private Button btn_humi;
    private Button btn_wind;
    private Button btn_pressure;
    private TextView risk;
    private ImageButton search;
    private EditText input_postcode;
    private TextView lastupdated;
    private TextView location_address;
    //private AppUser appUser;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreate(savedInstanceState);
        vmap = inflater.inflate(R.layout.fragment_main, container, false);
        mapView = vmap.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        btn_temp = vmap.findViewById(R.id.btn_temp);
        input_postcode = vmap.findViewById(R.id.search_location);
        btn_humi = vmap.findViewById(R.id.btn_humi);
        btn_pressure = vmap.findViewById(R.id.btn_pres);
        btn_wind = vmap.findViewById(R.id.btn_wind);
        location_address = vmap.findViewById(R.id.location_address);



        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                map = mapboxMap;

                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        ScaleBarPlugin scaleBarPlugin = new ScaleBarPlugin(mapView, mapboxMap);

                        // Create a ScaleBarOptions object to use the Plugin's default styling
                        scaleBarPlugin.create(new ScaleBarOptions(getActivity())
                                .setTextSize(20f)
                                .setBarHeight(10f)
                                .setBorderWidth(7f)
                                .setMetricUnit(true)
                                .setRefreshInterval(15)
                                .setMarginTop(30f)
                                .setMarginLeft(16f)
                                .setTextBarMargin(15f));

                        enableLocationComponent(style);
                    }
                });
            }
        });

        return vmap;
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {

            // Get an instance of the component
            LocationComponent locationComponent = map.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(getContext(), loadedMapStyle).build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            try {
                location = new LatLng(locationComponent.getLastKnownLocation().getLatitude(),
                        locationComponent.getLastKnownLocation().getLongitude());
                Log.i("location: ",location.toString());
            }catch (Exception e){
                Log.d("exception",e.getMessage());
            }

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume()
    { super.onResume();  if (mapView!=null) {mapView.onResume(); }}

    @Override
    public void onPause()
    { super.onPause();  if (mapView!=null) {mapView.onPause(); }}

    @Override
    public void onDestroyView()
    { super.onDestroyView();  if (mapView!=null) {mapView.onDestroy(); }}

    @Override
    public void onSaveInstanceState(Bundle outState)
    { super.onSaveInstanceState(outState);
        if (mapView!=null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getActivity(), "Location permission is not allowed", Toast.LENGTH_LONG).show();
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
            Toast.makeText(getActivity(),"user location permission is not granted", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

    }


}
