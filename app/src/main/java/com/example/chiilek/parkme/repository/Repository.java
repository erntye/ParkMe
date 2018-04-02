package com.example.chiilek.parkme.repository;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.example.chiilek.parkme.Location;
import com.example.chiilek.parkme.data_classes.CarParkDatum;
import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
import com.example.chiilek.parkme.data_classes.source.AppDatabase;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Repository {

    private AppDatabase appDatabase;
    //singleton pattern
    private static Repository INSTANCE;

    public Repository(Context context){
        this.appDatabase = AppDatabase.getInstance(context);
    }

    public static Repository getInstance(Context context){
        if (INSTANCE == null)
            INSTANCE = new Repository(context);
        return INSTANCE;
    }

    //this function is called by the SelectRouteViewModel to return the top 5 car parks
    public LiveData<List<CarParkDatum>> searchTop5(Location startpoint, Location destination){
        Log.d("Repo", "Called searchTop5(" + startpoint + "," + destination + ")");
        //call database getClosest10()
        //call API getTop5CarParks()
        return null;
    }

    /**
     * Searches for the car parks near a selected location.
     * One usage is for plotting the car parks near a searched location.
     * @param searchTerm
     * @return
     */
    public LiveData<List<CarParkStaticInfo>> searchNearby(LatLng searchTerm){
        Log.d("Repo", "Called setSearchTerm(" + searchTerm + ")");
        //call database getClosest10()
        List<CarParkStaticInfo> closest10CarParks = appDatabase.CPInfoDao()
                .getNearestCarParks(searchTerm.latitude, searchTerm.longitude);

        MutableLiveData<List<CarParkStaticInfo>> liveData = new MutableLiveData<>();
        liveData.setValue(closest10CarParks);
        return liveData;
    }
}