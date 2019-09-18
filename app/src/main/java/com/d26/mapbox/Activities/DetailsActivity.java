package com.d26.mapbox.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.d26.mapbox.R;
import com.d26.mapbox.other.Restful;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailsActivity extends AppCompatActivity {
    private String postcode;
    // TODO: Rename and change types of parameters
    private TextView location;
    private TextView weather;
    private TextView temprature;
    private TextView humidity;
    private TextView wind;
    private TextView pressure;
    private Toolbar mTopToolbar;

//    private Button bushfire;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_details);

        location = (TextView)findViewById(R.id.location);
        weather = (TextView)findViewById(R.id.weather);
        temprature = (TextView)findViewById(R.id.temprature);
        humidity = (TextView)findViewById(R.id.humidity);
        wind = (TextView)findViewById(R.id.windspeed);
        pressure = (TextView)findViewById(R.id.pressure);

//        bushfire = findViewById(R.id.bushfire);

        //adding a back menu
        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
        setTitle("Real Time Factors");

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String postcode = bundle.getString("postcode");
        String add = bundle.getString("Address1");
        //Address address = getIntent().getExtras().getParcelable("Address1");
        location.setText(add);

//        bushfire.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CauseActivity.bounceBaby(bushfire);
//                Intent intent = new Intent(DetailsActivity.this,Historic.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        getDetailAsyncTask getDetailAsyncTask =new getDetailAsyncTask();
        getDetailAsyncTask.execute(postcode);

    }

    public class getDetailAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            return Restful.findByPostcode(params[0]);
        }

        @Override
        protected void onPostExecute(String details) {
            //System.out.println(details);
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(details);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (jsonArray.length() > 0) {
                    JSONObject j = jsonArray.getJSONObject(0);
                    temprature.setText((j.get("airTemperature")).toString() + "°C");
                    humidity.setText((j.get("humidity")).toString() + "%");
                    wind.setText((j.get("windSpeed")).toString() + " Km/h");
                    pressure.setText((j.get("airPressure")).toString() + " hPa");
                }
                else {
                    temprature.setText("No Data");
                    humidity.setText("NO Data");
                    wind.setText("No Data");
                    pressure.setText("No Data");
                }
                } catch(JSONException e){
                    e.printStackTrace();
                }

            }
    }


//    //adding the toolbar
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
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
//            Intent intent = new Intent(DetailsActivity.this,MainActivity.class);
//            startActivity(intent);
//
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

}
