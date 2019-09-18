package com.d26.mapbox.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

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

                    BushfireModel bushfireModel = new BushfireModel(status,location,format.format(date1));
                       bushfireDataList2.add(bushfireModel);

                    } catch (JSONException e) {
                        e.printStackTrace();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            Collections.reverse(bushfireDataList2);
            bushfireDataList = bushfireDataList2;
            bushfireAdapter=new BushfireAdapter(bushfireDataList);
            recyclerView.setAdapter(bushfireAdapter);


        }

    }


//    private void StudentDataPrepare() {
//        BushfireModel data=new BushfireModel("sai","25","blah");
//        bushfireDataList.add(data);
//        data=new BushfireModel("sai","23rwfed","sdaafsadf");
//        bushfireDataList.add(data);
//    }

}
