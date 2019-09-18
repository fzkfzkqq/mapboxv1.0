package com.d26.mapbox.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.d26.mapbox.R;
import com.d26.mapbox.other.BushfireAdapter;
import com.d26.mapbox.other.BushfireModel;
import com.d26.mapbox.other.Restful;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class BushfireListActivity extends BaseDrawerActivity {

    private RecyclerView recyclerView;
    private BushfireAdapter bushfireAdapter;
    private List<BushfireModel> bushfireDataList =new ArrayList();
    private List<BushfireModel> bushfireDataList2 =new ArrayList();


    private int safe,resp,undercntrl,notundcntrl = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_bushfire_list, frameLayout);
         BaseDrawerActivity.toolbar.setTitle("Current Bushfires");

        recyclerView = findViewById(R.id.recycler_view);


        bushfireAdapter=new BushfireAdapter(bushfireDataList);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(bushfireAdapter);
        getNewsAsyncTask getNewsAsyncTask = new getNewsAsyncTask();
        getNewsAsyncTask.execute();


    }


    public class getNewsAsyncTask extends AsyncTask<String, Void, String> {

        //here is to get the parks using AsyncTask method
        @Override
        protected String doInBackground(String... strings) {
            Log.i("balh","sup bitch");
            return Restful.findAllBFAlerts();

        }

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
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    try {
                       JSONObject j = jsonArray.getJSONObject(i);
                       Double lat = Double.parseDouble(j.getString("latitude"));
                       Double longti = Double.parseDouble(j.getString("longitude"));

                       LatLng latLng = new LatLng(lat, longti);

                       String location = j.getString("location") ;
                       String alertUpdated = j.getString("alertUpdated");
                       String status = j.getString("status");

                       /*Parsing string to date*/
                        Date date1=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(alertUpdated);
                        Log.i("DATE PARSING",date1.toString());
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

                        format.applyPattern("dd-MM-yyyy");

                        /*check what category it belongs to*/
                        checkcount(j.getString("status"));


                    BushfireModel bushfireModel = new BushfireModel(status,location,format.format(date1));
                       bushfireDataList2.add(bushfireModel);

                    } catch (JSONException e) {
                        e.printStackTrace();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            /*Add the latest alerts first and set the adapter*/
            Collections.reverse(bushfireDataList2);
            bushfireDataList = bushfireDataList2;
            bushfireAdapter=new BushfireAdapter(bushfireDataList);

            SharedPreferences sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("safe", safe);
            editor.putInt("responding", resp);
            editor.putInt("undercontrol", undercntrl);
            editor.putInt("notundercontrol", notundcntrl);
            editor.apply();

            recyclerView.setAdapter(bushfireAdapter);




        }

    }


    private void checkcount(String value){
        if (value.equals("Safe")){
            safe++;
        }
        else
            if(value.equals("Under Control")){
                undercntrl++;
            }
            else
                if(value.equals("Responding")){
                    resp++;
                }
                else
                    if(value.equals("Not Under Control")){
                        notundcntrl++;
                    }

    }

//    private void StudentDataPrepare() {
//        BushfireModel data=new BushfireModel("sai","25","blah");
//        bushfireDataList.add(data);
//        data=new BushfireModel("sai","23rwfed","sdaafsadf");
//        bushfireDataList.add(data);
//    }

}
