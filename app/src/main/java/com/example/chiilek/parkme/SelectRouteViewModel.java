package com.example.chiilek.parkme;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.util.Log;

import com.example.chiilek.parkme.api_controllers.directions_api.DirectionsCallback;
import com.example.chiilek.parkme.data_classes.availability_classes.CarParkDatum;
import com.example.chiilek.parkme.data_classes.DirectionsAndCPInfo;
import com.example.chiilek.parkme.data_classes.directions_classes.GoogleMapsDirections;
import com.example.chiilek.parkme.repository.GetRoutesCallback;
import com.example.chiilek.parkme.repository.LocationRepository;
import com.example.chiilek.parkme.repository.Repository;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class SelectRouteViewModel extends AndroidViewModel {
    private MutableLiveData<LatLng> destination;
    private MutableLiveData<LatLng> startPoint;
    private MutableLiveData<List<DirectionsAndCPInfo>> directionsAndCarParksList;
    private MediatorLiveData mediatorDirAndCPList = new MediatorLiveData<>();
    private Repository mRepository;
    private LocationRepository mLocationRepo;
    //used during navigation
    private MediatorLiveData mediatorCurrentLoc = new MediatorLiveData<>();
    private MutableLiveData<LatLng> currentLocation;
    private DirectionsAndCPInfo chosenRoute;
    private MutableLiveData<GoogleMapsDirections> updatingRouteDirections;
    private boolean navigationStarted = false;

    //to be set up by SelectRouteActivity
    public SelectRouteViewModel(Application application){
        super(application);
        this.mRepository = Repository.getInstance(this.getApplication());
        destination = new MutableLiveData<>();
        //TODO get destination from user
        destination.setValue(new LatLng(1.378455, 103.755149));
        mLocationRepo = LocationRepository.getLocationRepository(this.getApplication());
        startPoint = new MutableLiveData<>();
        startPoint.setValue(mLocationRepo.getLocation().getValue());
        currentLocation = mLocationRepo.getLocation();
        directionsAndCarParksList = new MutableLiveData<>();
        //TODO get chosen route from user input

        mediatorDirAndCPList.addSource(destination,  newDestination -> {
            Log.d("SelectRouteViewModel","mediator activated on destination changed, get directions and CP called");
            mRepository.getDirectionsAndCPs(startPoint.getValue(), (LatLng)newDestination,
            new GetRoutesCallback() {
                @Override
                public void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList) {
                    directionsAndCarParksList.postValue(directionsAndCPInfoList);
                }
                @Override
                public void onFailure() {
                    Log.d("SelectRouteViewModel", "onFailure add source destination get routes callback");
                }
            });
        });

        mediatorDirAndCPList.addSource(startPoint,newStartPoint ->{
            Log.d("SelectRouteViewModel", "mediator activated on start point changed, get directions and CP called");
            mRepository.getDirectionsAndCPs((LatLng) newStartPoint, destination.getValue(),
                    new GetRoutesCallback() {
                        @Override
                        public void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList) {
                            directionsAndCarParksList.postValue(directionsAndCPInfoList);
                        }
                        @Override
                        public void onFailure() {
                            Log.d("SelectRouteViewModel", "onFailure add source destination get routes callback");
                        }
                    });
        });

        mediatorCurrentLoc.addSource(currentLocation, newCurrentLocation -> {
            Log.d("SelectRouteViewModel", "mediatorCurrentLoc changed");
            if(navigationStarted){
                Log.d("SelectRouteViewModel", "inside navigation started");
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
            }
        });
        /*TODO: set navigationStarted when changing to navi activity, onExit set it back to false.
        * Perhaps make a function which resets all nav information upon exit.*/

//        //calls repository to search again wTransformations.switchMap(destination, (LatLng newDestination)->
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

    //called by SelectRouteActivity whenever user inputs a new search term destination
    public void setDestination(LatLng searchTerm){
        if (searchTerm!= null)
            destination.setValue(searchTerm);
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
            Log.d("SelectRouteVM","Start point is "+ startPoint.getValue().toString());
            Log.d("SelectRouteVM","Destination is "+ destination.getValue().toString());
            mRepository.getDirectionsAndCPs(startPoint.getValue(),destination.getValue(), new GetRoutesCallback() {
                @Override
                public void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList) {
                    Log.d("SelectRouteViewModel", "getdirectionsandcp successful");
                    directionsAndCarParksList.setValue(directionsAndCPInfoList);
                }
                @Override
                public void onFailure() {
                    Log.d("SelectRouteViewModel", "observation exposure onFailure add source destination get routes callback");
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

}