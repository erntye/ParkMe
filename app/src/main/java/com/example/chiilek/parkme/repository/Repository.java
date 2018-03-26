package com.example.chiilek.parkme.repository;


import android.arch.lifecycle.LiveData;

import com.example.chiilek.parkme.Location;
import com.example.chiilek.parkme.data_classes.CarParkDatum;

import java.util.List;

public class Repository {
    //this function is called by the SelectRouteViewModel to return the top 5 car parks
    public LiveData<List<CarParkDatum>> searchTop5(Location startpoint, Location destination){
        //call database getClosest10()
        //call API getTop5CarParks()
        return null;
    }

    public LiveData<List<CarParkDatum>> searchNearby(Location destination){
        //call database getClosest10()
        //call GMAPS API to plot on Map
        return null;
    }
}
