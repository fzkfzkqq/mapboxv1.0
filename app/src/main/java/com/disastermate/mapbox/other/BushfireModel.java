package com.disastermate.mapbox.other;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class BushfireModel  {

    String status;
    String location;
    String lastUpdated;
    float distancebtwn;


    public BushfireModel(String status, String location, String lastUpdated) {
        this.status = status;
        this.location = location;
        this.lastUpdated = lastUpdated;
        this.distancebtwn = 0;
    }

    public BushfireModel(String status, String location, String lastUpdated,float distancebtwn) {
        this.status = status;
        this.location = location;
        this.lastUpdated = lastUpdated;
        this.distancebtwn = distancebtwn;
    }


    public void setDistancebtwn(float distancebtwn){
        this.distancebtwn = distancebtwn;
    }

    public float getDistancebtwn(){
        return distancebtwn;
    }

//    @Override
//    public int compareTo(BushfireModel o) {
//        if (getDistancebtwn() == 0 || o.getDistancebtwn() == 0) {
//            return 0;
//        }
//
//        return getDistancebtwn().compareTo(o.getDistancebtwn());
//    }
}
