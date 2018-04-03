package com.example.chiilek.parkme.repository;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.example.chiilek.parkme.apirepository.DirectionsAPIController;
import com.example.chiilek.parkme.apirepository.DirectionsCallback;
import com.example.chiilek.parkme.data_classes.CarParkDatum;
import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
import com.example.chiilek.parkme.data_classes.DirectionsAndCPInfo;
import com.example.chiilek.parkme.data_classes.directions_classes.GoogleMapsDirections;
import com.example.chiilek.parkme.data_classes.source.AppDatabase;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
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
    public LiveData<List<DirectionsAndCPInfo>> searchTop5(LatLng startPoint, LatLng destination){
        Log.d("Repo", "Called searchTop5(" + startPoint + "," + destination + ")");
        //call database getClosest10()
        //sort closest 10 by distance
        //get route directions for all
        //sort top 5 by score
        //return list of directionsandcpinfo

        List<CarParkStaticInfo> closest10CarParks = appDatabase.CPInfoDao()
                .getNearestCarParks(destination.latitude,destination.longitude);

        List<DirectionsAndCPInfo> directionsAndCPList = new ArrayList<DirectionsAndCPInfo>();

        for(CarParkStaticInfo carPark : closest10CarParks){
            DirectionsAPIController.getInstance().callDirectionsAPI(startPoint,
                    new LatLng(Double.parseDouble(carPark.getLatitude()),Double.parseDouble(carPark.getLongitude())),
                    new DirectionsCallback(){
                        public void onSuccess(GoogleMapsDirections googleMapsDirections){
                            DirectionsAndCPInfo element = new DirectionsAndCPInfo(carPark, googleMapsDirections);
                            directionsAndCPList.add(element);
                        }
                        public void onFailure(){
                            Log.e("Repository","Directions Callback onFailure.");
                        }

                    });
        }

        //TODO: assign distance and duration scores to each directionsAndCP, then sort them in order.
        //TODO: Wrap in a mutable live data and return.

        return null;
    }

    /**
     * Searches for the car parks near a selected location.
     * One usage is for plotting the car parks near a searched location.
     * @param destination
     * @return LiveData List of CarParkStaticInfo
     */
    public LiveData<List<CarParkStaticInfo>> searchNearbyCarParks(LatLng destination){
        Log.d("Repo", "Called setSearchTerm(" + destination + ")");
        //call database getClosest10()
        List<CarParkStaticInfo> closest10CarParks = appDatabase.CPInfoDao()
                .getNearestCarParks(destination.latitude,destination.longitude);

        MutableLiveData<List<CarParkStaticInfo>> carParkLiveData = null;
        carParkLiveData.setValue(closest10CarParks);
        return carParkLiveData;
    }
}