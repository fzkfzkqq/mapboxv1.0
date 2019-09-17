package com.d26.mapbox.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.TextView;

import com.d26.mapbox.R;


public class CauseActivity extends BaseDrawerActivity implements View.OnClickListener {


    private Button fuel_load;
    private Button fuel_moisture;
    private Button wind_speed;
    private Button ambient_temperature;
    private Button relative_humidity;
    private Button slope_angle;
    private Button ignition_source;

    private TextView fuelload_text;
    private TextView fuelmoisture_text;
    private TextView windspeed_text;
    private TextView ambienttemperature_text;
    private TextView relativehumidity_text;
    private TextView slopeangle_text;
    private TextView ignitionsource_text;

    private TextView temporary;

    private Toolbar mTopToolbar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Bushfires Can Be Caused By");
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

        //adding a back menu
        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);





        fuel_load.setOnClickListener(this);
        fuel_moisture.setOnClickListener(this);
        wind_speed.setOnClickListener(this);
        ambient_temperature.setOnClickListener(this);
        relative_humidity.setOnClickListener(this);
        slope_angle.setOnClickListener(this);
        ignition_source.setOnClickListener(this);


    }

    public static void bounceBaby(final Button button){
        final ViewPropertyAnimator animator = button.animate();
        animator.setDuration(200);

        button.setEnabled(false);
        animator.scaleXBy(-0.06f)
                .scaleYBy(-0.06f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                        animator.scaleXBy(0.06f)
                                .scaleYBy(0.06f)
                                .setListener(null)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        button.setEnabled(true);
                                    }
                                });
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fuel_load:
                bounceBaby(fuel_load);
                fuelload_text.setVisibility(View.VISIBLE);
                if(temporary!=null){
                    temporary.setVisibility(View.GONE);
                }
                temporary = fuelload_text;
                break;
            case R.id.fuel_moisture:
                bounceBaby(fuel_moisture);
                fuelmoisture_text.setVisibility(View.VISIBLE);
                if(temporary!=null){
                    temporary.setVisibility(View.GONE);
                }
                temporary = fuelmoisture_text;
                break;
            case R.id.wind_speed:
                bounceBaby(wind_speed);

                windspeed_text.setVisibility(View.VISIBLE);
                if(temporary!=null){
                    temporary.setVisibility(View.GONE);
                }
                temporary = windspeed_text;
                break;
            case R.id.ambient_temperature:
                bounceBaby(ambient_temperature);

                ambienttemperature_text.setVisibility(View.VISIBLE);
                if(temporary!=null){
                    temporary.setVisibility(View.GONE);
                }
                temporary = ambienttemperature_text;
                break;
            case R.id.relative_humidity:
                bounceBaby(relative_humidity);

                relativehumidity_text.setVisibility(View.VISIBLE);
                if(temporary!=null){
                    temporary.setVisibility(View.GONE);
                }
                temporary = relativehumidity_text;
                break;
            case R.id.slope_angle:
                bounceBaby(slope_angle);

                slopeangle_text.setVisibility(View.VISIBLE);
                if(temporary!=null){
                    temporary.setVisibility(View.GONE);
                }
                temporary = slopeangle_text;
                break;
            case R.id.ignition_source:
                bounceBaby(ignition_source);

                ignitionsource_text.setVisibility(View.VISIBLE);
                if(temporary!=null){
                    temporary.setVisibility(View.GONE);
                }
                temporary = ignitionsource_text;
        }

    }


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
            Intent intent = new Intent(CauseActivity.this,MainActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}