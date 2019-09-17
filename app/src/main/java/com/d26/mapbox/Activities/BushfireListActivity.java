package com.d26.mapbox.Activities;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.d26.mapbox.R;
import com.d26.mapbox.other.BushfireAdapter;
import com.d26.mapbox.other.BushfireModel;

import java.util.ArrayList;
import java.util.List;

public class BushfireListActivity extends BaseDrawerActivity {

    private RecyclerView recyclerView;
    private BushfireAdapter bushfireAdapter;
    private List bushfireDataList =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_bushfire_list, frameLayout);
         BaseDrawerActivity.toolbar.setTitle("List");

        recyclerView = findViewById(R.id.recycler_view);
        bushfireAdapter=new BushfireAdapter(bushfireDataList);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(bushfireAdapter);
        StudentDataPrepare();
    }

    private void StudentDataPrepare() {
        BushfireModel data=new BushfireModel("sai","25","blah");
        bushfireDataList.add(data);
        data=new BushfireModel("sai","23rwfed","sdaafsadf");
        bushfireDataList.add(data);
    }

}
