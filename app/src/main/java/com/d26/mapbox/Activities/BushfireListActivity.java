package com.d26.mapbox.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
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
import java.util.List;


public class BushfireListActivity extends BaseDrawerActivity {

    private RecyclerView recyclerView;
    private BushfireAdapter bushfireAdapter;
    private List<BushfireModel> bushfireDataList =new ArrayList();
    private List<BushfireModel> bushfireDataList2 =new ArrayList();
//    private int safe,resp,cntrl,ncntrl;
    private TextView tvsafe,tvresp,tvcntrl,tvncntrl;
    private LinearLayout lsafe,lcont,lrep,lncont, lpulldown;

    private static int safe,resp,undercntrl,notundcntrl = 0;
    private List<BushfireModel> safeList =new ArrayList();
    private List<BushfireModel> controlList =new ArrayList();
    private List<BushfireModel> ncontrolList =new ArrayList();
    private List<BushfireModel> respList =new ArrayList();

    Date date1;
    SimpleDateFormat format;
    static String location,alertUpdated,status;
    View view;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getLayoutInflater().inflate(R.layout.activity_bushfire_list, frameLayout);
        BaseDrawerActivity.toolbar.setTitle("Current Bushfires");

        recyclerView = findViewById(R.id.recycler_view);
        tvsafe = findViewById(R.id.safe_count);
        tvresp = findViewById(R.id.responding_count);
        tvcntrl = findViewById(R.id.undercontrol_count);
        tvncntrl = findViewById(R.id.notundercontrol_count);
        lsafe = findViewById(R.id.lsafe);
        lcont = findViewById(R.id.lcont);
        lncont = findViewById(R.id.lncont);
        lrep   = findViewById(R.id.lresp);



        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(bushfireAdapter);
        getNewsAsyncTask getNewsAsyncTask = new getNewsAsyncTask();
        getNewsAsyncTask.execute();


        lsafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setAdapter(new BushfireAdapter(revList(safeList), new BushfireAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BushfireModel item) {

                        Toast.makeText(getApplicationContext(), "Item Clicked", Toast.LENGTH_LONG).show();

                    }
                }));

            }
        });

        lncont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setAdapter(new BushfireAdapter(revList(ncontrolList), new BushfireAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BushfireModel item) {
                        Toast.makeText(getApplicationContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                    }
                }));

            }
        });

        lcont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setAdapter(new BushfireAdapter(revList(controlList), new BushfireAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BushfireModel item) {
                        Toast.makeText(getApplicationContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                    }
                }));

            }
        });

        lrep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setAdapter(new BushfireAdapter(revList(respList), new BushfireAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BushfireModel item) {
                        Toast.makeText(getApplicationContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                    }
                }));

            }
        });

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

                       location = j.getString("location") ;
                       alertUpdated = j.getString("alertUpdated");
                       status = j.getString("status");

                       /*Parsing string to date*/
                         date1=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(alertUpdated);
                        Log.i("DATE PARSING",date1.toString());
                         format = new SimpleDateFormat("dd-MM-yyyy");

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



            tvsafe.setText(String.valueOf(safe));
            tvcntrl.setText(String.valueOf(undercntrl));
            tvresp.setText(String.valueOf(resp));
            tvncntrl.setText(String.valueOf(notundcntrl));

            recyclerView.setAdapter(new BushfireAdapter(bushfireDataList, new BushfireAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BushfireModel item) {
                    Toast.makeText(getApplicationContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                }
            }));



        }

    }


    private void checkcount(String value){
        if (value.equals("Safe")){
            ++safe;
            safeList.add(new BushfireModel(status,location,format.format(date1)) );
        }
        else
            if(value.equals("Under Control")){
                ++undercntrl;
                controlList.add(new BushfireModel(status,location,format.format(date1)) );

            }
            else
                if(value.equals("Responding")){
                    ++resp;
                    respList.add(new BushfireModel(status,location,format.format(date1)) );

                }
                else
                    if(value.equals("Not Yet Under Control")){
                        ++notundcntrl;
                        ncontrolList.add(new BushfireModel(status,location,format.format(date1)) );

                    }

    }

    private List<BushfireModel> revList(List<BushfireModel> list){
        List<BushfireModel> list1 = new ArrayList<>();
        Collections.reverse(list);
        list1 = list;

        return list1;
    }


}
