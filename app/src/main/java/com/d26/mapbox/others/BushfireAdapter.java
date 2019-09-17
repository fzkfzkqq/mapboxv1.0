package com.d26.mapbox.others;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.d26.mapbox.R;

import java.util.List;

public class BushfireAdapter extends RecyclerView.Adapter<BushfireAdapter.MyViewHolder> {
    List<BushfireModel> bushfireDataList;
    public BushfireAdapter(List bushfireDataList){
        this.bushfireDataList = bushfireDataList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_list, viewGroup, false);
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
        }
    }
}

