package com.example.chiilek.parkme.repository;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.example.chiilek.parkme.api_controllers.availability_api.AvailabilityAPIController;
import com.example.chiilek.parkme.api_controllers.availability_api.AvailabilityCallback;
import com.example.chiilek.parkme.api_controllers.directions_api.DirectionsAPIController;
import com.example.chiilek.parkme.api_controllers.directions_api.DirectionsCallback;
import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
import com.example.chiilek.parkme.data_classes.DirectionsAndCPInfo;
import com.example.chiilek.parkme.data_classes.availability_classes.Item;
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
    public void getDirectionsAndCPs(LatLng startPoint, LatLng destination, GetRoutesCallback routesCallback) {
        Log.d("Repository", "Called getDirectionsAndCPs(" + startPoint.toString() + "," + destination.toString() + ")");

        //gets list of all nearby car parks (within a certain range)
        List<CarParkStaticInfo> closestCarParks = appDatabase.CPInfoDao()
                .getNearestCarParks(destination.latitude, destination.longitude);

        //generates directions to each car park, stores in DirectionsAndCPInfo class
        Log.d("Repository", "origin: " + startPoint.toString());
        List<DirectionsAndCPInfo> directionsAndCPList = new ArrayList<DirectionsAndCPInfo>();
        AvailabilityAPIController availAPIControl = new AvailabilityAPIController();
        AtomicInteger counter = new AtomicInteger(closestCarParks.size());
        for (CarParkStaticInfo carPark : closestCarParks) {
            DirectionsAPIController.getInstance().callDirectionsAPI(startPoint,
                    new LatLng(Double.parseDouble(carPark.getLatitude()), Double.parseDouble(carPark.getLongitude())),
                    new DirectionsCallback() {
                public void onSuccess(GoogleMapsDirections googleMapsDirections) {
                    Log.d("repository", "in success");
                    DirectionsAndCPInfo element = new DirectionsAndCPInfo(carPark, googleMapsDirections);
                    directionsAndCPList.add(element);
                    int index = directionsAndCPList.indexOf(element);

                    //calling Availability API with callback function.
                    availAPIControl.makeCall(index, new AvailabilityCallback() {
                        @Override
                        public void onSuccess(int index, Item cpAPIItem) {
                            DirectionsAndCPInfo newElement = directionsAndCPList.get(index);
                            //passes in the car park number of the destination car park.
                            newElement.setCarParkDatum(cpAPIItem.getCarParkDatum(newElement.getCarParkStaticInfo().getCPNumber()));
                            int i = counter.decrementAndGet();
                            if (i == 0) {
                                Log.d("repository", "in avail callback success: atomic counter is 0");
                                Log.d("repository", "calling back to routes callback");
                                routesCallback.onSuccess(scoreAndSort(directionsAndCPList));
                            }
                        }

                        @Override
                        public void onFailure() {
                            Log.e("Repository", "Availability Callback onFailure");
                            int i = counter.decrementAndGet();
                            if (i == 0) {
                                Log.d("repository", "in avail callback failure: atomic counter is 0");
                                Log.d("repository", "calling back to routes callback");
                                routesCallback.onSuccess(directionsAndCPList);
                            }
                        }
                    });
                }

                public void onFailure() {
                    Log.e("Repository", "Directions Callback onFailure.");
                    int i = counter.decrementAndGet();
                    if (i == 0) {
                        Log.d("repository", "directions callback failure: atomic counter is 0");
                        Log.d("repository", "calling back to routes callback");
                        routesCallback.onSuccess(directionsAndCPList);
                    }
                }
            });
        }
    }

    private List<DirectionsAndCPInfo> scoreAndSort(List<DirectionsAndCPInfo> directionsAndCPList){
        int size = directionsAndCPList.size();
        //sorts by distance and scores each element
        directionsAndCPList.sort(Comparator.comparingDouble(DirectionsAndCPInfo::getDistance));
        int score = size;
        for(DirectionsAndCPInfo element : directionsAndCPList){
            element.setDistanceScore(score);
            score--;
        }

        //sorts and scores by duration
        directionsAndCPList.sort(Comparator.comparingDouble(DirectionsAndCPInfo::getDuration));
        score = size;
        for(DirectionsAndCPInfo element : directionsAndCPList){
            element.setDurationScore(score);
            score--;
        }

        directionsAndCPList.sort(Comparator.comparingDouble(DirectionsAndCPInfo::getAvailability));
        score = size;
        for(DirectionsAndCPInfo element : directionsAndCPList){
            element.setAvailabilityScore(score);
            score--;
        }

        //sorts by overall score
        directionsAndCPList.sort(Comparator.comparingDouble(DirectionsAndCPInfo::getOverallScore));

        return directionsAndCPList;
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
                            Log.d("Repository","counter is now " + Integer.toString(i));
                        }
                        public void onFailure(){
                            Log.e("Repository","Directions Callback onFailure.");
                            int i = counter.decrementAndGet();
                        }

                    });
        }
        while(counter.get() != 0){
            Log.d("waiting",Integer.toString(counter.get()));
//            try {
//                wait(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
        Log.d("Repository", "done with calls, counter = 0");
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