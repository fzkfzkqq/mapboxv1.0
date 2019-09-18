package com.d26.mapbox.other;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.d26.mapbox.R;

import java.util.List;

public class BushfireAdapter extends RecyclerView.Adapter<BushfireAdapter.MyViewHolder> {

    private Context context;
    int safe,resp,cntrl,ncntrl;
    TextView tvsafe,tvresp,tvcntrl,tvncntrl;

    List<BushfireModel> bushfireDataList;
    public BushfireAdapter(List bushfireDataList){
        this.bushfireDataList = bushfireDataList;
    }

    public BushfireAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.bushfire_item_list, viewGroup, false);
        return new MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        BushfireModel data= bushfireDataList.get(i);
        myViewHolder.location.setText(data.location);
        myViewHolder.status.setText(String.valueOf(data.status));
        myViewHolder.lastUpdated.setText(String.valueOf(data.lastUpdated));
    }


    @Override
    public int getItemCount() {
        return bushfireDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView status,location,lastUpdated;

        public MyViewHolder(View itemView) {
            super(itemView);
            status=itemView.findViewById(R.id.status);
            location=itemView.findViewById(R.id.location);
            lastUpdated=itemView.findViewById(R.id.last_updated);
            tvsafe = itemView.findViewById(R.id.safe_count);
            tvresp = itemView.findViewById(R.id.responding_count);
            tvcntrl = itemView.findViewById(R.id.undercontrol_count);
            tvncntrl = itemView.findViewById(R.id.notundercontrol_count);


            SharedPreferences sharedPreferences = context.getSharedPreferences("Prefs", Context.MODE_PRIVATE);


            safe = sharedPreferences.getInt("safe",0);
            resp = sharedPreferences.getInt("responinding",0);
            cntrl = sharedPreferences.getInt("undercontrol",0);
            ncntrl = sharedPreferences.getInt("notundercontrol",0);





        }

    }


}

