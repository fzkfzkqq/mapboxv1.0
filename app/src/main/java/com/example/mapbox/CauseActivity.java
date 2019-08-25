package com.example.mapbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mapbox.mapboxsdk.maps.MapView;

import org.w3c.dom.Text;


public class CauseActivity extends AppCompatActivity implements View.OnClickListener {

    private Button fuel_load;
    private Button fuel_moisture;
    private Button wind_speed;
    private Button ambient_temperature;
    private Button relative_humidity;
    private Button slope_angle;
    private Button ignition_source;

    private EditText fuelload_text;
    private EditText fuelmoisture_text;
    private EditText windspeed_text;
    private EditText ambienttemperature_text;
    private EditText relativehumidity_text;
    private EditText slopeangle_text;
    private EditText ignitionsource_text;

    private EditText temporary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.causes);

        fuel_load = findViewById(R.id.fuel_load);
        fuel_moisture = findViewById(R.id.fuel_moisture);
        wind_speed = findViewById(R.id.wind_speed);
        ambient_temperature = findViewById(R.id.ambient_temperature);
        relative_humidity = findViewById(R.id.relative_humidity);
        slope_angle = findViewById(R.id.slope_angle);
        ignition_source = findViewById(R.id.ignition_source);

        fuelload_text = findViewById(R.id.fuel_load_text);
        fuelmoisture_text = findViewById(R.id.fuel_moisture_text);
        windspeed_text = findViewById(R.id.wind_speed_text);
        ambienttemperature_text = findViewById(R.id.ambient_temperature_text);
        relativehumidity_text = findViewById(R.id.relative_humidity_text);
        slopeangle_text = findViewById(R.id.slope_angle_text);
        ignitionsource_text = findViewById(R.id.ignitionsource_text);


        fuel_load.setOnClickListener(this);
        fuel_moisture.setOnClickListener(this);
        wind_speed.setOnClickListener(this);
        ambient_temperature.setOnClickListener(this);
        relative_humidity.setOnClickListener(this);
        slope_angle.setOnClickListener(this);
        ignition_source.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fuel_load:
                fuelload_text.setVisibility(View.VISIBLE);
                if(temporary!=null){
                    temporary.setVisibility(View.GONE);
                }
                temporary = fuelload_text;
                break;
            case R.id.fuel_moisture:
                fuelmoisture_text.setVisibility(View.VISIBLE);
                if(temporary!=null){
                    temporary.setVisibility(View.GONE);
                }
                temporary = fuelmoisture_text;
                break;
            case R.id.wind_speed:
                windspeed_text.setVisibility(View.VISIBLE);
                if(temporary!=null){
                    temporary.setVisibility(View.GONE);
                }
                temporary = windspeed_text;
                break;
            case R.id.ambient_temperature:
                ambienttemperature_text.setVisibility(View.VISIBLE);
                if(temporary!=null){
                    temporary.setVisibility(View.GONE);
                }
                temporary = ambienttemperature_text;
                break;
            case R.id.relative_humidity:
                relativehumidity_text.setVisibility(View.VISIBLE);
                if(temporary!=null){
                    temporary.setVisibility(View.GONE);
                }
                temporary = relativehumidity_text;
                break;
            case R.id.slope_angle:
                slopeangle_text.setVisibility(View.VISIBLE);
                if(temporary!=null){
                    temporary.setVisibility(View.GONE);
                }
                temporary = slopeangle_text;
                break;
            case R.id.ignition_source:
                ignitionsource_text.setVisibility(View.VISIBLE);
                if(temporary!=null){
                    temporary.setVisibility(View.GONE);
                }
                temporary = ignitionsource_text;
        }

    }
}