package com.d26.mapbox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.d26.mapbox.others.BushfireAdapter;
import com.d26.mapbox.others.BushfireModel;

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
//        /*ToolBar : set your title here*/
        Toolbar mTopToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);

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
