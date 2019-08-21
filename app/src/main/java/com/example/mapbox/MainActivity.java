package com.example.mapbox;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private MapboxMap map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiZnprODg4IiwiYSI6ImNqemh1a3M4MzB6eGgzbmxrMWx0c3Q3b3AifQ.--BckGBvrRT-TXTMJsaDAA");
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                map = mapboxMap;
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

// Map is set up and the style has loaded. Now you can add data or make other map adjustments


                    }
                });
                //addMarker(mapboxMap);
                GetParks getpark = new GetParks();
                getpark.execute();
                mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        return false;
                    }
                });
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

    //here is to get the parks using AsyncTask method
    private class GetParks extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            return Restful.findAllBFRecords();
        }
        @Override
        protected void onPostExecute(String details) {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(details);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonArray.length() > 0 ) {
                for (int i = 0; i < jsonArray.length();i++)
                {
                    try {
                        JSONObject j = jsonArray.getJSONObject(i);
                        Double lat  = (Double)(j.getDouble("latitude"));
                        Double longti = (Double)(j.getDouble("longitude"));
                        LatLng latLng = new LatLng(lat,longti);
                        String snippet = "power:" + ((Double)j.getDouble("power")).toString()+
                                "\nLongitude:" + longti.toString()+
                                "\nDatetime:" + j.get("datetime")+
                                "\nlatitude:" + lat.toString()+
                                "\ntemp:" + ((Double)j.getDouble("temp_kelvin")).toString();
                        fireMarks(latLng,snippet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }}
    //here is to put marks for parks
    private void fireMarks(LatLng latLng,String snippet) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("fire");
        markerOptions.snippet(snippet);
        //IconFactory iconFactory = IconFactory.getInstance(getActivity());
        //Icon icon = iconFactory.fromResource(R.drawable.mapbox_compass_icon);
        //markerOptions.setIcon(icon);
        map.addMarker(markerOptions);
    }

}
