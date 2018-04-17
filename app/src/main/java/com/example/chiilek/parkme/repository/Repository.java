package com.example.chiilek.parkme.repository;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.example.chiilek.parkme.api_controllers.availability_api.AvailabilityAPIController;
import com.example.chiilek.parkme.api_controllers.availability_api.AvailabilityCallback;
import com.example.chiilek.parkme.api_controllers.directions_api.DirectionsAPIController;
import com.example.chiilek.parkme.api_controllers.directions_api.DirectionsCallback;
import com.example.chiilek.parkme.api_controllers.directions_api.GMapsDirectionsAPI;
import com.example.chiilek.parkme.api_controllers.roads_api.RoadsAPIController;
import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
import com.example.chiilek.parkme.data_classes.DirectionsAndCPInfo;
import com.example.chiilek.parkme.data_classes.availability_classes.CarParkDatum;
import com.example.chiilek.parkme.data_classes.availability_classes.Item;
import com.example.chiilek.parkme.data_classes.directions_classes.GoogleMapsDirections;
import com.example.chiilek.parkme.data_classes.roads_classes.GMapsRoads;
import com.example.chiilek.parkme.data_classes.source.AppDatabase;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Repository {

    private AppDatabase appDatabase;
    private MutableLiveData<List<CarParkStaticInfo>> viewMapCPList = new MutableLiveData<List<CarParkStaticInfo>>();
    //singleton pattern
    private static Repository INSTANCE;

    public Repository(Context context){
        this.appDatabase = AppDatabase.getInstance(context);
    }

    public static Repository getInstance(Context context){
        if (INSTANCE == null)
            INSTANCE = new Repository(context);
        Log.d("Repository", "In Singleton Pattern getInstance");
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
        Log.d("Repository", "Called getDirectionsAndCPs(startPoint: " + startPoint.toString() + ", destination: " + destination.toString() + ")");

        //gets list of all nearby car parks (within a certain range)
        List<CarParkStaticInfo> closestCarParks = appDatabase.CPInfoDao()
                .getNearestCarParks(destination.latitude, destination.longitude);
        //TODO handle function if size 0
        if (closestCarParks == null) {
            Log.d("Repository", "In getDirectionsAndCPs: closestCarParks is null");
            routesCallback.onFailure();
        }
        else if (closestCarParks.size() == 0 )
            Log.d("Repository", "In getDirectionsAndCPs: There are no closest carparks \n*******") ;
        else
            Log.d("Repository", "In getDirectionsAndCPs: closestCarParks size: " + closestCarParks.size()) ;
        //generates directions to each car park, stores in DirectionsAndCPInfo class
        //Log.d("Repository", "origin: " + startPoint.toString());
        List<DirectionsAndCPInfo> directionsAndCPList = new ArrayList<DirectionsAndCPInfo>();
        AvailabilityAPIController availAPIControl = new AvailabilityAPIController();
        AtomicInteger counter = new AtomicInteger(closestCarParks.size());
        for (CarParkStaticInfo carPark : closestCarParks) {
            DirectionsAPIController.getInstance().callDirectionsAPI(startPoint,
                    new LatLng(Double.parseDouble(carPark.getLatitude()), Double.parseDouble(carPark.getLongitude())),
                    new DirectionsCallback() {
                public void onSuccess(GoogleMapsDirections googleMapsDirections) {
                    Log.d("Repository", "onSuccess in DirectionsCallback from CP in closestCarParks");
                    Log.d("Repository","DIRECTIONS ROUTE: " + googleMapsDirections.getRoutes().get(0).getLegs().get(0).getSteps().toString());
//                    GMapsRoads roads = RoadsAPIController.getInstance().getRoads(googleMapsDirections);
//                    googleMapsDirections.setgMapsRoads(roads);
                    DirectionsAndCPInfo element = new DirectionsAndCPInfo(carPark, googleMapsDirections, destination);
                    directionsAndCPList.add(element);
                    int i = counter.decrementAndGet();
                    if(i==0){
                        Log.d("Repository", "In getDirectionsAndCPs: atomic counter is 0; GMaps API calls are done");
                        //calling Availability API with callback function.
                        availAPIControl.makeCall(new AvailabilityCallback() {
                            @Override
                            public void onSuccess(Item cpAPIItem) {
                                if(cpAPIItem.getCarParkData().size() == 0){
                                    Log.d("Repository", "In availability callback success: Size of cpData list in Item object is 0.");
                                    for(DirectionsAndCPInfo dirAndCP : directionsAndCPList){
                                        //stores CarParkDatum object into each DirectionsAndCP object
                                        dirAndCP.setCarParkDatum(new CarParkDatum());
                                    }
                                    Log.d("Repository", "In availability callback success. Created default CarParkDatum objects and stored in D&CPInfo objects.");
                                }else {
                                    for (DirectionsAndCPInfo dirAndCP : directionsAndCPList) {
                                        //stores CarParkDatum object into each DirectionsAndCP object
                                        dirAndCP.setCarParkDatum(cpAPIItem.getCarParkDatum(dirAndCP.getCarParkStaticInfo().getCPNumber()));
                                    }
                                    Log.d("Repository", "In availability callback success: CarParkDatum has been saved for all car parks in list.");
                                    Log.d("Repository", "Calling onSuccess from ViewModel's GetRoutesCallback");
                                    routesCallback.onSuccess(scoreAndSort(directionsAndCPList));
                                }
                            }

                            @Override
                            public void onFailure() {
                                Log.e("Repository", "Availability Callback onFailure");
                                Log.d("Repository", "Calling onSuccess from ViewModel's GetRoutesCallback");
                                for(DirectionsAndCPInfo dirAndCP : directionsAndCPList){
                                    //stores CarParkDatum object into each DirectionsAndCP object
                                    dirAndCP.setCarParkDatum(new CarParkDatum());
                                }
                                Log.d("Repository", "In availability callback failure. Created default CarParkDatum objects and stored in D&CPInfo objects.");
                                routesCallback.onSuccess(directionsAndCPList);
                            }
                        });
                    }

                }

                public void onFailure() {
                    Log.e("Repository", "Directions Callback onFailure.");
                    int i = counter.decrementAndGet();
                    if (i == 0) {
                        Log.d("Repository", "In directions callback failure: atomic counter is 0");
                        if(directionsAndCPList.size() == 0){
                            Log.d("Repository", "In directions callback failure, could not find routes for any car park.");
                            routesCallback.onFailure();
                        }else {
                            Log.d("Repository", "In directions callback failure, calling onSuccess from ViewModel's GetRoutesCallback");
                            routesCallback.onSuccess(directionsAndCPList);
                        }
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

        //sorts and scores by availability
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
//    private List<DirectionsAndCPInfo> callAPIForDirAndCP(List<CarParkStaticInfo> closestCarParks, LatLng startPoint){
//        List<DirectionsAndCPInfo> directionsAndCPList = new ArrayList<DirectionsAndCPInfo>();
//        AtomicInteger counter = new AtomicInteger(closestCarParks.size());
//        for(CarParkStaticInfo carPark : closestCarParks){
//            DirectionsAPIController.getInstance().callDirectionsAPI(startPoint,
//                    new LatLng(Double.parseDouble(carPark.getLatitude()),Double.parseDouble(carPark.getLongitude())),
//                    new DirectionsCallback(){
//                        public void onSuccess(GoogleMapsDirections googleMapsDirections){
//                            DirectionsAndCPInfo element = new DirectionsAndCPInfo(carPark, googleMapsDirections);
//                            directionsAndCPList.add(element);
//                            int i = counter.decrementAndGet();
//                            Log.d("Repository","counter is now " + Integer.toString(i));
//                        }
//                        public void onFailure(){
//                            Log.e("Repository","Directions Callback onFailure.");
//                            int i = counter.decrementAndGet();
//                        }
//
//                    });
//        }
//        while(counter.get() != 0){
//            Log.d("waiting",Integer.toString(counter.get()));
////            try {
////                wait(1000);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
//        }
//        Log.d("Repository", "done with calls, counter = 0");
//        return directionsAndCPList;
//    }

    /**
     * Searches for the car parks near a selected location.
     * One usage is for plotting the car parks near a searched location.
     * @param searchTerm
     * @return
     */
    public MutableLiveData<List<CarParkStaticInfo>> searchNearbyCarParks(LatLng searchTerm, SearchNearbyCallback searchNearbyCallback){
        Log.d("Repository", "Called setSearchTerm(" + searchTerm + ")");
        //call database getClosest10()
        List<CarParkStaticInfo> closestCarParks = appDatabase.CPInfoDao()
                .getNearestCarParks(searchTerm.latitude, searchTerm.longitude);
        MutableLiveData<List<CarParkStaticInfo>> liveData = new MutableLiveData<>();

        AvailabilityAPIController availAPIControl = new AvailabilityAPIController();
        availAPIControl.makeCall(new AvailabilityCallback() {
            @Override
            public void onSuccess(Item cpAPIItem) {
                if(cpAPIItem.getCarParkData().size() == 0){
                    Log.d("Repository", "searchNearbyCarParks() - In availability callback success: Size of cpData list in Item object is 0.");
                    Log.d("Repository", "searchNearbyCarParks() - Availability information will be null.");
                }else {
                    for(CarParkStaticInfo carPark : closestCarParks){
                        carPark.setAvailInfo(cpAPIItem.getCarParkDatum(carPark.getCPNumber()));
                    }
                    Log.d("Repository", "searchNearbyCarParks() - In availability callback success: CarParkDatum has been saved for all car parks in static info list.");
                    Log.d("Repository", "searchNearbyCarParks() - Calling onSuccess from ViewModel's GetRoutesCallback");
                    viewMapCPList.postValue(closestCarParks);
                    searchNearbyCallback.onSuccess();
                }
            }

            @Override
            public void onFailure() {
                Log.e("Repository", "searchNearbyCarParks() - Availability Callback onFailure");
                Log.d("Repository", "searchNearbyCarParks() - Availability information will be null.");
            }
        });
        viewMapCPList.setValue(closestCarParks);
        liveData.setValue(viewMapCPList.getValue());
        return liveData;
    }

    public void updateRoutes(LatLng startPoint, LatLng destination, DirectionsCallback directionCallback){
        DirectionsAPIController.getInstance().callDirectionsAPI(startPoint, destination, new DirectionsCallback() {
            @Override
            public void onSuccess(GoogleMapsDirections gMapsDirections) {
                Log.d("Repository", "updateRoutes callback success");
                Log.d("Repository","DIRECTIONS ROUTE: " + gMapsDirections.getRoutes().get(0).getLegs().get(0).getSteps().toString());
//                GMapsRoads roads = RoadsAPIController.getInstance().getRoads(gMapsDirections);
//                gMapsDirections.setgMapsRoads(roads);
                directionCallback.onSuccess(gMapsDirections);
            }

            @Override
            public void onFailure() {
                Log.d("Repository", "updateRoutes callback failure");
            }
        });
    }

    //expose for view map view model
    public MutableLiveData<List<CarParkStaticInfo>> getViewMapCPList(){
        return viewMapCPList;
    }
}