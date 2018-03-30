package com.example.chiilek.parkme.repository;


import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.example.chiilek.parkme.Location;
import com.example.chiilek.parkme.data_classes.CarParkDatum;
import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
import com.example.chiilek.parkme.data_classes.source.AppDatabase;

import java.util.List;

public class Repository {

    private AppDatabase appDatabase;

    public Repository(Context context){
        this.appDatabase = AppDatabase.getInstance(context);
    }


    //this function is called by the SelectRouteViewModel to return the top 5 car parks
    public LiveData<List<CarParkDatum>> searchTop5(Location startpoint, Location destination){
        //call database getClosest10()
        //call API getTop5CarParks()
        return null;
    }

    public LiveData<List<CarParkDatum>> searchNearby(Location destination){
        //call database getClosest10()
        //call GMAPS API to plot on Map

        List<CarParkStaticInfo> closest10CarParks = appDatabase.CPInfoDao().getNearestCarParks(1.0,2.0);
        return null;
    }
}
