package com.example.mapbox;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class details extends Fragment {
    private String postcode;
    private View vDetailView;
    // TODO: Rename and change types of parameters
    private TextView location;
    private TextView weather;
    private TextView temprature;
    private TextView humidity;
    private TextView wind;
    private TextView pressure;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vDetailView =  inflater.inflate(R.layout.fragment_details, container, false);
        location = (TextView)vDetailView.findViewById(R.id.location);
        weather = (TextView)vDetailView.findViewById(R.id.weather);
        temprature = (TextView)vDetailView.findViewById(R.id.temprature);
        humidity = (TextView)vDetailView.findViewById(R.id.humidity);
        wind = (TextView)vDetailView.findViewById(R.id.windspeed);
        pressure = (TextView)vDetailView.findViewById(R.id.pressure);
        getDetailAsyncTask getDetailAsyncTask =new getDetailAsyncTask();
        getDetailAsyncTask.execute("3125");
        return vDetailView;
    }

    private class getDetailAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            return Restful.findByPostcode(params[0]);
        }

        @Override
        protected void onPostExecute(String details) {
            System.out.println(details);
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(details);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                    JSONObject j = jsonArray.getJSONObject(0);
                    temprature.setText((j.get("airTemperature")).toString() + "°C");
                    humidity.setText((j.get("humidity")).toString() + "%");
                    wind.setText((j.get("windSpeed")).toString() + " Km/h");
                    pressure.setText((j.get("airPressure")).toString() + " hPa");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
    }

}
