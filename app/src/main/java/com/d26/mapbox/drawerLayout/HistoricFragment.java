package com.d26.mapbox.drawerLayout;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.d26.mapbox.Historic;
import com.d26.mapbox.R;
import com.d26.mapbox.Restful;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.HeatmapLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.pluginscalebar.ScaleBarOptions;
import com.mapbox.pluginscalebar.ScaleBarPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

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

public class HistoricFragment extends Fragment {
    private MapView mapView;
    private MapboxMap map;
    private View vHistorymap;
    private static final String EARTHQUAKE_SOURCE_URL = "https://disastermateapi.azurewebsites.net/historicalbushfires.geojson";
    private static final String EARTHQUAKE_SOURCE_ID = "bushfire";
    private static final String HEATMAP_LAYER_ID = "earthquakes-heat";
    private static final String HEATMAP_LAYER_SOURCE = "bushfire";
    private static final String CIRCLE_LAYER_ID = "earthquakes-circle";
    private boolean isMarkShown = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreate(savedInstanceState);
        vHistorymap = inflater.inflate(R.layout.fragment_history, container, false);
        mapView = vHistorymap.findViewById(R.id.historyMap);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                map = mapboxMap;
                mapboxMap.setStyle(Style.DARK, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        addEarthquakeSource(style);
                        addHeatmapLayer(style);
                        addCircleLayer(style);

// Map is set up and the style has loaded. Now you can add data or make other map adjustments
                    }
                });

            }
        });

        vHistorymap.findViewById(R.id.show_historic_marker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isMarkShown){
                    isMarkShown = true;
                    GetParks getpark = new GetParks();
                    getpark.execute();
                }else
                    Toast.makeText(getActivity(), "The details are already shown",
                            Toast.LENGTH_LONG).show();


            }
        });
        return vHistorymap;
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
                            LatLng latLng = new LatLng(lat,longti);
                            String markerSnippet = "temprature: " + j.getJSONObject("properties").getString("temperature")+
                                    "\n power: " + j.getJSONObject("properties").getString("power");
                            parkMarks(latLng,markerSnippet);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }}
    //here is to put marks for parks
    private void parkMarks(LatLng latLng,String snippet) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Bushfire");
        markerOptions.snippet(snippet);
        IconFactory iconFactory = IconFactory.getInstance(getActivity());
        Icon icon = iconFactory.fromResource(R.drawable.fire2);
        markerOptions.setIcon(icon);
        map.addMarker(markerOptions);
    }
}
