package com.d26.mapbox.other;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.d26.mapbox.R;

import java.util.List;


public class BushfireAdapter extends RecyclerView.Adapter<BushfireAdapter.MyViewHolder> {

    public interface OnItemClickListener {

        void onItemClick(BushfireModel bushfireModel);
    }

    private Context context;

    private List<BushfireModel> bushfireDataList;

    /*maybe we need to make this final lets see*/
    private final OnItemClickListener listener;


    public BushfireAdapter(List<BushfireModel> bushfireDataList, OnItemClickListener listener){
        this.bushfireDataList = bushfireDataList;
        this.listener = listener;
    }

    public BushfireAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
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
        myViewHolder.bind(bushfireDataList.get(i),listener);
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

        public void bind(final BushfireModel bushfireModel,final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(bushfireModel);
                }
            });
        }
    }
    }

