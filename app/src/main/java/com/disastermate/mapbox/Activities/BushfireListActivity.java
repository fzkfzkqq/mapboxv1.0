package com.disastermate.mapbox.Activities;

import android.annotation.SuppressLint;
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
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.disastermate.mapbox.R;
import com.disastermate.mapbox.other.BushfireAdapter;
import com.disastermate.mapbox.other.BushfireModel;
import com.disastermate.mapbox.other.Restful;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.pusher.client.channel.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class BushfireListActivity extends BaseDrawerActivity implements AdapterView.OnItemSelectedListener {

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

    private FloatingActionButton showFloods,showFire;
    private Location mylocation;

    Date date1;
    SimpleDateFormat format;
    static String location,alertUpdated,status;
    View view;

    TextView dist;
    private MenuInflater inflater;

    private Spinner firespinner;
    double lato,longo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = getLayoutInflater().inflate(R.layout.activity_bushfire_list, frameLayout);
        BaseDrawerActivity.toolbar.setTitle("Current Bushfires");
        getlatlong();
        initialise();

        showFireSpinner();

        initaliseRecyclerView();

        getNewsAsyncTask getNewsAsyncTask = new getNewsAsyncTask();
        getNewsAsyncTask.execute();

        getNewsAsyncTaskF getNewsAsyncTaskF = new getNewsAsyncTaskF();
        getNewsAsyncTaskF.execute();


//        setOnclickers();

        // Spinner click listener
        firespinner.setOnItemSelectedListener(this);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        if (item.equals("Less than 20km radius")){

            sortByDistance(bushfireDataList);
            Iterator itr = bushfireDataList.iterator();
            List<BushfireModel> fireSublist = new ArrayList<>();

            while(itr.hasNext()){
                BushfireModel bushfireModel = (BushfireModel) itr.next();
                if(bushfireModel.getDistancebtwn() < 20){
                    fireSublist.add(bushfireModel);
                }
            }

            setAdapter(fireSublist);

        }else if (item.equals("All Safe")){
            sortByDistance(safeList);
            setAdapter(safeList);
        }else if (item.equals("All Under Control")){
            sortByDistance(controlList);
            setAdapter(controlList);
        }else if (item.equals("All Responding")){
            sortByDistance(respList);
            setAdapter(respList);
        }else if (item.equals("All Not Under Control")){
            sortByDistance(ncontrolList);
            setAdapter(ncontrolList);
        }else if (item.equals("Show All")){
            sortByDistance(bushfireDataList);
            setAdapter(bushfireDataList);
        }else if (item.equals("Less than 20kms radius")) {
            sortByDistance(floodDataList);
            Iterator itr = floodDataList.iterator();
            List<BushfireModel> floodSublist = new ArrayList<>();

            while (itr.hasNext()) {
                BushfireModel bushfireModel = (BushfireModel) itr.next();
                if (bushfireModel.getDistancebtwn() < 20) {
                    floodSublist.add(bushfireModel);
                }
            }
        }else if (item.equals("All Flooding")) {
            sortByDistance(floodList);
            setAdapter(floodList);
        }else if (item.equals("All Request for Assistance")) {
            sortByDistance(assistanceList);
            setAdapter(assistanceList);
        }else if (item.equals("All Complete")) {
            sortByDistance(completeList);
            setAdapter(completeList);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public class getNewsAsyncTask extends AsyncTask<String, Void, String> {

        //here is to get the parks using AsyncTask method
        @Override
        protected String doInBackground(String... strings) {
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
            sortByDistance(bushfireDataList);
            recyclerView.setAdapter(new BushfireAdapter(bushfireDataList, new BushfireAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BushfireModel item) {

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

                        }
                    }));

                    showFireSpinner();

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

                      }
                  }));

                showFloodSpinner();

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


    private void setAdapter(List<BushfireModel> list){
        recyclerView.setAdapter(new BushfireAdapter(list, new BushfireAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BushfireModel item) {

            }
        }));

    }

    private void sortByDistance(List<BushfireModel> list){
       list.sort(Comparator.comparing(BushfireModel::getDistancebtwn));
    }

    private void showFireSpinner(){
        //Initialize list for fire spinner
        List<String> categories = new ArrayList<String>();

        categories.add("Less than 20km radius");
        categories.add("All Safe");
        categories.add("All Under Control");
        categories.add("All Responding");
        categories.add("All Not Under Control");
        categories.add("All Bushfires");



        //create the adapter for the fire spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);


        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        firespinner.setAdapter(dataAdapter);

    }

    private void showFloodSpinner(){
        //initialising flood spinner
        //Initialize list for fire spinner
        List<String> categories = new ArrayList<String>();

        categories.add("Less than 20kms radius");
        categories.add("All Flooding");
        categories.add("All Request for Assistance");
        categories.add("All Complete");

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);


        // Drop down layout style - list view with radio button
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        firespinner.setAdapter(dataAdapter2);

    }

    public void setOnclickers(){

        lsafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapter(safeList);

            }
        });

        lncont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapter(ncontrolList);

            }
        });

        lcont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapter(controlList);

            }
        });

        lrep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapter(respList);

            }
        });

        lcomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapter(completeList);

            }
        });

        lflood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapter(floodList);
            }
        });

        lreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapter(assistanceList);

            }
        });


    }

    public void initialise(){


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

        showFloods = findViewById(R.id.change_flood);
        showFire = findViewById(R.id.change_fire);
        firespinner = findViewById(R.id.fire_spinner);
    }

    public void getlatlong(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        lato = sharedPreferences.getFloat("Latitude",0);
        longo = sharedPreferences.getFloat("Longitude",0);


        mylocation = new Location("");
        mylocation.setLatitude(lato);
        mylocation.setLongitude(longo);

    }

    public void initaliseRecyclerView(){
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(bushfireAdapter);

    }
}
