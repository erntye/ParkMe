package com.example.chiilek.parkme.viewmodel;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.chiilek.parkme.api.directions_api.DirectionsCallback;
import com.example.chiilek.parkme.entity.DirectionsAndCPInfo;
import com.example.chiilek.parkme.entity.directionsapi.GoogleMapsDirections;
import com.example.chiilek.parkme.repository.callbacks.GetRoutesCallback;
import com.example.chiilek.parkme.repository.LocationRepository;
import com.example.chiilek.parkme.repository.Repository;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class SelectRouteViewModel extends AndroidViewModel {
    private MutableLiveData<LatLng> endPoint;
    private MutableLiveData<LatLng> startPoint;
    private MutableLiveData<List<DirectionsAndCPInfo>> directionsAndCarParksList;
    private MediatorLiveData mediatorDirAndCPList = new MediatorLiveData<>();
    private Repository mRepository;
    private LocationRepository mLocationRepo;
    //used during navigation
    private MediatorLiveData mediatorCurrentLoc = new MediatorLiveData<>();
    private LatLng previousLocation;
    private MutableLiveData<LatLng> currentLocation;
    private DirectionsAndCPInfo chosenRoute;
    private MutableLiveData<GoogleMapsDirections> updatingRouteDirections;
    private boolean navigationStarted = false;


    //to be set up by SelectRouteActivity
    public SelectRouteViewModel(Application application, LatLng chosenDestination){
        super(application);
        this.mRepository = Repository.getInstance(this.getApplication());
        mLocationRepo = LocationRepository.getLocationRepository(this.getApplication());
        //TODO get endPoint from user from activity
        endPoint = new MutableLiveData<>();
        //endPoint.setValue(new LatLng(1.378455, 103.755149));
        endPoint.setValue(chosenDestination);
        currentLocation = mLocationRepo.getLocation();
        //start point initializes at current location but the user can type into the bar to set it
        startPoint = new MutableLiveData<>();
        startPoint.setValue(currentLocation.getValue());
        previousLocation = currentLocation.getValue();
        directionsAndCarParksList = new MutableLiveData<>();

        updatingRouteDirections = new MutableLiveData<>();

        mediatorDirAndCPList.addSource(endPoint, newDestination -> {
            Log.d("SelectRouteViewModel","mediator activated new endPoint: " + newDestination.toString() +" get directions and CP called");
            mRepository.getDirectionsAndCPs(startPoint.getValue(), (LatLng)newDestination,
            new GetRoutesCallback() {
                @Override
                public void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList) {
                    directionsAndCarParksList.postValue(directionsAndCPInfoList);
                }
                @Override
                public void onFailure() {
                    Log.d("SelectRouteViewModel", "onFailure add source endPoint get routes callback");
                }
            });
        });

        mediatorDirAndCPList.addSource(startPoint,newStartPoint ->{
            Log.d("SelectRouteViewModel", "mediator activated on start point changed, get directions and CP called");
            mRepository.getDirectionsAndCPs((LatLng) newStartPoint, endPoint.getValue(),
                    new GetRoutesCallback() {
                        @Override
                        public void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList) {
                            directionsAndCarParksList.postValue(directionsAndCPInfoList);
                        }
                        @Override
                        public void onFailure() {
                            Log.d("SelectRouteViewModel", "onFailure add source endPoint get routes callback");
                        }
                    });
        });

        //should be in navigation viewmodel, testing for now
        mediatorCurrentLoc.addSource(currentLocation, newCurrentLocation -> {
            Log.d("SelectRouteViewModel", "mediatorCurrentLoc changed");
            if(navigationStarted){
                Log.d("SelectRouteViewModel", "inside navigation started");
                //if location is changed
                if (!currentLocation.getValue().equals(previousLocation)){
                    Log.d("SelectRouteViewModel", "current loc: " + currentLocation.getValue().toString() + " prev loc: " + previousLocation.toString());
                    mRepository.updateRoutes((LatLng) newCurrentLocation, chosenRoute.getDestinationLatLng(),
                            new DirectionsCallback() {
                                @Override
                                public void onSuccess(GoogleMapsDirections gMapsDirections) {
                                    updatingRouteDirections.postValue(gMapsDirections);
                                    Log.d("SelectRouteViewModel", "navigation: updated route directions with new current loc.");
                                }

                                @Override
                                public void onFailure() {
                                    Log.d("SelectRouteViewModel", "navigation: update route failed");
                                }
                            });
                    previousLocation = currentLocation.getValue();
                }else Log.d("SelectRouteViewModel", "location not changed, do not need to update route");
            }
        });
        /*TODO: set navigationStarted when changing to navi activity, onExit set it back to false.
        * Perhaps make a function which resets all nav information upon exit.*/

//        //calls repository to search again wTransformations.switchMap(endPoint, (LatLng newDestination)->
//                        mRepository.getDirectionsAndCPs(startPoint.getValue(), newDestination,
//                                new GetRoutesCallback() {
//                                    @Override
//                                    public void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList) {
//                                        directionsAndCarParksList.setValue(directionsAndCPInfoList);
//                                    }
//
//                                    @Override
//                                    public void onFailure() {
//
//                                    }
//                                }));//whenever destination is changed by SelectRouteVM.search()
//
//        //calls repository to search again whenever start point is changed
//        directionsAndCarParksList = Transformations.switchMap(startPoint, (LatLng newStartPoint)->
//                mRepository.getDirectionsAndCPs(newStartPoint, destination.getValue()));
       /*routeToPlot = Transformations.switchMap(chosenCarPark, (CarParkDatum carpark)->
                mRepository.getRoutePolyline(carpark));*/
        //TODO initialize carParkList and route(?)

    }
/*
    //overloaded constructor in the meantime before the frontend can pass the backend information
    public SelectRouteViewModel(Application application){
        super(application);
        this.mRepository = Repository.getInstance(this.getApplication());
        mLocationRepo = LocationRepository.getLocationRepository(this.getApplication());
        //TODO get endPoint from user from activity
        endPoint = new MutableLiveData<>();
        endPoint.setValue(new LatLng(1.378455, 103.755149));
        //endPoint.setValue(chosenCarPark.getLatLng());
        currentLocation = mLocationRepo.getLocation();
        //start point initializes at current location but the user can type into the bar to set it
        startPoint = new MutableLiveData<>();
        startPoint.setValue(currentLocation.getValue());
        previousLocation = currentLocation.getValue();
        directionsAndCarParksList = new MutableLiveData<>();


        updatingRouteDirections = new MutableLiveData<>();

        mediatorDirAndCPList.addSource(endPoint, newDestination -> {
            Log.d("SelectRouteViewModel","mediator activated new endPoint: " + newDestination.toString() +" get directions and CP called");
            mRepository.getDirectionsAndCPs(startPoint.getValue(), (LatLng)newDestination,
                    new GetRoutesCallback() {
                        @Override
                        public void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList) {
                            directionsAndCarParksList.postValue(directionsAndCPInfoList);
                        }
                        @Override
                        public void onFailure() {
                            Log.d("SelectRouteViewModel", "onFailure add source endPoint get routes callback");
                        }
                    });
        });

        mediatorDirAndCPList.addSource(startPoint,newStartPoint ->{
            Log.d("SelectRouteViewModel", "mediator activated on start point changed, get directions and CP called");
            mRepository.getDirectionsAndCPs((LatLng) newStartPoint, endPoint.getValue(),
                    new GetRoutesCallback() {
                        @Override
                        public void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList) {
                            directionsAndCarParksList.postValue(directionsAndCPInfoList);
                        }
                        @Override
                        public void onFailure() {
                            Log.d("SelectRouteViewModel", "onFailure add source endPoint get routes callback");
                        }
                    });
        });

        //should be in navigation viewmodel, testing for now
        mediatorCurrentLoc.addSource(currentLocation, newCurrentLocation -> {
            Log.d("SelectRouteViewModel", "mediatorCurrentLoc changed");
            if(navigationStarted){
                Log.d("SelectRouteViewModel", "inside navigation started");
                //if location is changed
                if (!currentLocation.getValue().equals(previousLocation)){
                    Log.d("SelectRouteViewModel", "current loc: " + currentLocation.getValue().toString() + " prev loc: " + previousLocation.toString());
                    mRepository.updateRoutes((LatLng) newCurrentLocation, chosenRoute.getDestinationLatLng(),
                            new DirectionsCallback() {
                                @Override
                                public void onSuccess(GoogleMapsDirections gMapsDirections) {
                                    updatingRouteDirections.postValue(gMapsDirections);
                                    Log.d("SelectRouteViewModel", "navigation: updated route directions with new current loc.");
                                }

                                @Override
                                public void onFailure() {
                                    Log.d("SelectRouteViewModel", "navigation: update route failed");
                                }
                            });
                    previousLocation = currentLocation.getValue();
                }else Log.d("SelectRouteViewModel", "location not changed, do not need to update route");
            }
        });
    }
*/

    //called by SelectRouteActivity whenever user inputs a new search term endPoint
    public void setEndPoint(LatLng searchTerm){
        if (searchTerm!= null)
            endPoint.setValue(searchTerm);
    }
    //called by SelectRouteActivity whenever user inputs a new search term start point
    public void setStartPoint(LatLng searchTerm){
        if (searchTerm!= null)
            Log.d("SelectRouteViewModel", "setStartPoint: " + searchTerm.toString());
            startPoint.setValue(searchTerm);
    }

    //called by SelectRouteActivity when user presses his route
    public void setChosenRoute(DirectionsAndCPInfo newRoute){
        this.chosenRoute = newRoute;
    }

    //called by NavigationActivity when Navigation mode started
    public void setNavigationStarted(){navigationStarted = true;}

    //expose for observation to viewmodel
    public MutableLiveData<List<DirectionsAndCPInfo>> getDirectionsAndCarParks() {
        if (directionsAndCarParksList == null) {
            directionsAndCarParksList = new MutableLiveData<List<DirectionsAndCPInfo>>();
            Log.d("SelectRouteViewModel","Start point is "+ startPoint.getValue().toString());
            Log.d("SelectRouteViewModel","Destination is "+ endPoint.getValue().toString());
            mRepository.getDirectionsAndCPs(startPoint.getValue(), endPoint.getValue(), new GetRoutesCallback() {
                @Override
                public void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList) {
                    Log.d("SelectRouteViewModel", "getdirectionsandcp successful");
                    directionsAndCarParksList.setValue(directionsAndCPInfoList);
                }
                @Override
                public void onFailure() {
                    Log.d("SelectRouteViewModel", "observation exposure onFailure add source endPoint get routes callback");
                }});
        }
        return directionsAndCarParksList;
    }

    //expose for observation to viewmodel
    public MediatorLiveData getMediatorCurrentLoc() {
        return mediatorCurrentLoc;
    }

    //expose for observation to viewmodel
    public MediatorLiveData getMediatorDirAndCPList() {
        return mediatorDirAndCPList;
    }
    public boolean getNavigationStarted(){
        return navigationStarted;
    }

/*    @Override
    protected void onCleared() {
        super.onCleared();
        mLocationRepo.stopLocationUpdates();
    }*/
    public LiveData<GoogleMapsDirections> getGoogleMapsDirections(){ return updatingRouteDirections; }

}