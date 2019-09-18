package com.d26.mapbox.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
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

    private static final String PREFS_NAME = "Prefs";
    private RecyclerView recyclerView;
    private BushfireAdapter bushfireAdapter;
    private static List<BushfireModel> bushfireDataList =new ArrayList();
    private static List<BushfireModel> bushfireDataList2 =new ArrayList();


    private static List<BushfireModel> floodDataList =new ArrayList();
    private static List<BushfireModel> floodDataList2 =new ArrayList();

//    private int safe,resp,cntrl,ncntrl;
    private TextView tvsafe,tvresp,tvcntrl,tvncntrl,req,flood,compl;
    private LinearLayout lsafe,lcont,lrep,lncont,lreq,lflood,lcomplete,layFire,layFlood;
    private float distance;
    private String lati,longi;


    private static int safe,resp,undercntrl,notundcntrl,floodcount,compcount,asscount = 0;
    private static List<BushfireModel> safeList =new ArrayList();
    private static List<BushfireModel> controlList =new ArrayList();
    private static List<BushfireModel> ncontrolList =new ArrayList();
    private static List<BushfireModel> respList =new ArrayList();

    private static List<BushfireModel> floodList =new ArrayList();
    private static List<BushfireModel> completeList =new ArrayList();
    private static List<BushfireModel> assistanceList =new ArrayList();

    private FloatingActionButton showAll,showFloods,showFire;
    private Location mylocation;

    Date date1;
    SimpleDateFormat format;
    static String location,alertUpdated,status;
    View view;

    TextView dist;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        double lato,longo;
        view = getLayoutInflater().inflate(R.layout.activity_bushfire_list, frameLayout);
        BaseDrawerActivity.toolbar.setTitle("Current Bushfires");
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        lato = sharedPreferences.getFloat("Latitude",0);
        longo = sharedPreferences.getFloat("Longitude",0);
//        mylocation.setLatitude(0);
//        mylocation.setLongitude(0);
//        mylocation.setLatitude(lato);
//        mylocation.setLatitude(longo);


        mylocation = new Location("");
        mylocation.setLatitude(lato);
        mylocation.setLongitude(longo);

        recyclerView = findViewById(R.id.recycler_view);
        tvsafe = findViewById(R.id.safe_count);
        tvresp = findViewById(R.id.responding_count);
        tvcntrl = findViewById(R.id.undercontrol_count);
        tvncntrl = findViewById(R.id.notundercontrol_count);
        lsafe = findViewById(R.id.lsafe);
        lcont = findViewById(R.id.lcont);
        lncont = findViewById(R.id.lncont);
        lrep   = findViewById(R.id.lresp);
        req = findViewById(R.id.req_count);
        flood = findViewById(R.id.flood_count);
        compl = findViewById(R.id.complete_count);
        lreq = findViewById(R.id.lreq);
        lflood = findViewById(R.id.lflood);
        lcomplete = findViewById(R.id.lcomplete);
        layFire = findViewById(R.id.layFire);
        layFlood = findViewById(R.id.layFlood);
        dist = findViewById(R.id.distance);

        showAll = findViewById(R.id.show_all);
        showFloods = findViewById(R.id.change_flood);
        showFire = findViewById(R.id.change_fire);






        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(bushfireAdapter);
        getNewsAsyncTask getNewsAsyncTask = new getNewsAsyncTask();
        getNewsAsyncTask.execute();

        getNewsAsyncTaskF getNewsAsyncTaskF = new getNewsAsyncTaskF();
        getNewsAsyncTaskF.execute();



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

        lcomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setAdapter(new BushfireAdapter(revList(completeList), new BushfireAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BushfireModel item) {
                        Toast.makeText(getApplicationContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                    }
                }));
            }
        });

        lflood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setAdapter(new BushfireAdapter(revList(floodList), new BushfireAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BushfireModel item) {
                        Toast.makeText(getApplicationContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                    }
                }));
            }
        });

        lreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setAdapter(new BushfireAdapter(revList(assistanceList), new BushfireAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BushfireModel item) {
                        Toast.makeText(getApplicationContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                    }
                }));
            }
        });



        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layFlood.setVisibility(View.GONE);
                layFire.setVisibility(View.GONE);
                bushfireDataList.addAll(floodDataList);
                recyclerView.setAdapter(new BushfireAdapter(revList(bushfireDataList), new BushfireAdapter.OnItemClickListener() {
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
                        lati = j.getString("latitude");
                        longi = j.getString("longitude");

                        distance = getDistance(mylocation,Double.parseDouble(lati),Double.parseDouble(longi));


                       /*Parsing string to date*/
                         date1=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(alertUpdated);
                        Log.i("DATE PARSING",date1.toString());
                         format = new SimpleDateFormat("dd-MM-yyyy");

                        format.applyPattern("dd-MM-yyyy");

                        /*check what category it belongs to*/
                        checkcount(j.getString("status"));


                    BushfireModel bushfireModel = new BushfireModel( "Bushfire\n" + status ,location,format.format(date1),distance);
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


            showFire.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View v) {
                    showFire.setVisibility(View.GONE);
                    showFloods.setVisibility(View.VISIBLE);
                    layFire.setVisibility(View.VISIBLE);
                    layFlood.setVisibility(View.GONE);
                    recyclerView.setAdapter(new BushfireAdapter(bushfireDataList, new BushfireAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BushfireModel item) {
                            Toast.makeText(getApplicationContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                        }
                    }));

                }
            });



        }

    }

    public class getNewsAsyncTaskF extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return Restful.findAllFloodAlerts();

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
                        lati = j.getString("latitude");
                        longi = j.getString("longitude");

                        distance = getDistance(mylocation,Double.parseDouble(lati),Double.parseDouble(longi));
                        Log.i("DIST",String.valueOf(distance));

                        /*Parsing string to date*/
                        date1=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(alertUpdated);
                        Log.i("DATE PARSING",date1.toString());
                        format = new SimpleDateFormat("dd-MM-yyyy");

                        format.applyPattern("dd-MM-yyyy");

                        /*check what category it belongs to*/
                        checkcountF(j.getString("status"));


                        BushfireModel bushfireModel = new BushfireModel("Flood\n" + status,location,format.format(date1),distance);
                        floodDataList2.add(bushfireModel);

                    } catch (JSONException e) {
                        e.printStackTrace();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            /*Add the latest alerts first and set the adapter*/
            Collections.reverse(floodDataList2);
            floodDataList = floodDataList2;



            flood.setText(String.valueOf(floodcount));
            req.setText(String.valueOf(asscount));
            compl.setText(String.valueOf(compcount));


          showFloods.setOnClickListener(new View.OnClickListener() {
              @SuppressLint("RestrictedApi")
              @Override
              public void onClick(View v) {
                  showFloods.setVisibility(View.GONE);
                  showFire.setVisibility(View.VISIBLE);
                  layFire.setVisibility(View.GONE);
                  layFlood.setVisibility(View.VISIBLE);
                  recyclerView.setAdapter(new BushfireAdapter(floodDataList, new BushfireAdapter.OnItemClickListener() {
                      @Override
                      public void onItemClick(BushfireModel item) {
                          Toast.makeText(getApplicationContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                      }
                  }));

              }
          });



        }

    }


    private void checkcount(String value){
        if (value.equals("Safe")){
            ++safe;
            safeList.add(new BushfireModel(status,location,format.format(date1),distance) );
        }
        else
            if(value.equals("Under Control")){
                ++undercntrl;
                controlList.add(new BushfireModel(status,location,format.format(date1),distance) );

            }
            else
                if(value.equals("Responding")){
                    ++resp;
                    respList.add(new BushfireModel(status,location,format.format(date1),distance));

                }
                else
                    if(value.equals("Not Yet Under Control")){
                        ++notundcntrl;
                        ncontrolList.add(new BushfireModel(status,location,format.format(date1),distance) );

                    }

    }

    private void checkcountF(String value){
        if (value.equals("Flooding")){
            ++floodcount;
            floodList.add(new BushfireModel(status,location,format.format(date1),distance) );
        }
        else
        if(value.equals("Request For Assistance")){
            ++asscount;
            assistanceList.add(new BushfireModel(status,location,format.format(date1),distance) );

        }
        else
        if(value.equals("Complete")){
            ++compcount;
            completeList.add(new BushfireModel(status,location,format.format(date1),distance) );

        }


    }


    private List<BushfireModel> revList(List<BushfireModel> list){
        List<BushfireModel> list1 = new ArrayList<>();
        Collections.reverse(list);
        list1 = list;

        return list1;
    }


    private float getDistance(Location location,double lat, double longi){

        Location loc2 = new Location("");
        loc2.setLatitude(lat);
        loc2.setLongitude(longi);

        float distanceInMeters = location.distanceTo(loc2);
        return distanceInMeters/1000;
    }

}
