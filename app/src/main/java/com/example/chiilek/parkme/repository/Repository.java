package com.example.chiilek.parkme.repository;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.example.chiilek.parkme.apirepository.DirectionsAPIController;
import com.example.chiilek.parkme.apirepository.DirectionsCallback;
import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
import com.example.chiilek.parkme.data_classes.DirectionsAndCPInfo;
import com.example.chiilek.parkme.data_classes.directions_classes.GoogleMapsDirections;
import com.example.chiilek.parkme.data_classes.source.AppDatabase;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        Log.d("Repo", "got Repo singleton");
        return INSTANCE;
    }


    /**
     * Called by SelectRouteViewModel to return list of directions to nearby car parks, sorted.
     * Car parks will be sorted based on a weighted score of flying distance and travel duration.
     * @param startPoint
     * @param destination
     * @return LiveData List of DirectionsAndCPInfo, containing the routes and static info of
     *          all nearby car parks, sorted by weighted score
     */
    public LiveData<List<DirectionsAndCPInfo>> getDirectionsAndCPs(LatLng startPoint, LatLng destination){
        Log.d("Repo", "Called getDirectionsAndCPs(" + startPoint.toString() + "," + destination.toString() + ")");

        //gets list of all nearby car parks (within a certain range)
        List<CarParkStaticInfo> closestCarParks = appDatabase.CPInfoDao()
                .getNearestCarParks(destination.latitude,destination.longitude);

        //generates directions to each car park, stores in DirectionsAndCPInfo class
        List<DirectionsAndCPInfo> directionsAndCPList = callAPIForDirAndCP(closestCarParks, startPoint);

        //sorts by distance and scores each element
        directionsAndCPList.sort(Comparator.comparingDouble(DirectionsAndCPInfo::getDistance));
        int score = directionsAndCPList.size();
        for(DirectionsAndCPInfo element : directionsAndCPList){
            element.setDistanceScore(score);
            score--;
        }

        //sorts and scores by duration
        directionsAndCPList.sort(Comparator.comparingDouble(DirectionsAndCPInfo::getDuration));
        score = directionsAndCPList.size();
        for(DirectionsAndCPInfo element : directionsAndCPList){
            element.setDurationScore(score);
            score--;
        }

        //sorts by overall score
        directionsAndCPList.sort(Comparator.comparingDouble(DirectionsAndCPInfo::getOverallScore));

        //wraps it as mutable live data
        MutableLiveData<List<DirectionsAndCPInfo>> liveData = new MutableLiveData<>();
        liveData.setValue(directionsAndCPList);

        return liveData;
    }

    //function to manage async calls to the Directions API for generation directions to each car park
    private List<DirectionsAndCPInfo> callAPIForDirAndCP(List<CarParkStaticInfo> closestCarParks, LatLng startPoint){
        List<DirectionsAndCPInfo> directionsAndCPList = new ArrayList<DirectionsAndCPInfo>();
        AtomicInteger counter = new AtomicInteger(closestCarParks.size());
        for(CarParkStaticInfo carPark : closestCarParks){
            DirectionsAPIController.getInstance().callDirectionsAPI(startPoint,
                    new LatLng(Double.parseDouble(carPark.getLatitude()),Double.parseDouble(carPark.getLongitude())),
                    new DirectionsCallback(){
                        public void onSuccess(GoogleMapsDirections googleMapsDirections){
                            DirectionsAndCPInfo element = new DirectionsAndCPInfo(carPark, googleMapsDirections);
                            directionsAndCPList.add(element);
                            int i = counter.decrementAndGet();
                        }
                        public void onFailure(){
                            Log.e("Repository","Directions Callback onFailure.");
                            int i = counter.decrementAndGet();
                        }

                    });
        }
        while(counter.get() != 0){
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return directionsAndCPList;
    }

    /**
     * Searches for the car parks near a selected location.
     * One usage is for plotting the car parks near a searched location.
     * @param searchTerm
     * @return
     */
    public LiveData<List<CarParkStaticInfo>> searchNearbyCarParks(LatLng searchTerm){
        Log.d("Repo", "Called setSearchTerm(" + searchTerm + ")");
        //call database getClosest10()
        List<CarParkStaticInfo> closestCarParks = appDatabase.CPInfoDao()
                .getNearestCarParks(searchTerm.latitude, searchTerm.longitude);

        MutableLiveData<List<CarParkStaticInfo>> liveData = new MutableLiveData<>();
        liveData.setValue(closestCarParks);
        return liveData;
    }
}