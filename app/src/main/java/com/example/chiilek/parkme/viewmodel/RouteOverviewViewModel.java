package com.example.chiilek.parkme.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.chiilek.parkme.api.directions_api.DirectionsCallback;
import com.example.chiilek.parkme.entity.CarParkInfo;
import com.example.chiilek.parkme.entity.DirectionsAndCPInfo;
import com.example.chiilek.parkme.entity.directionsapi.GoogleMapsDirections;
import com.example.chiilek.parkme.repository.LocationRepository;
import com.example.chiilek.parkme.repository.Repository;
import com.google.android.gms.maps.model.LatLng;

public class RouteOverviewViewModel extends AndroidViewModel {

    private MediatorLiveData mediatorCurrentLoc = new MediatorLiveData<>();

    private LatLng previousLocation;
    private MutableLiveData<LatLng> currentLocation;

    private LocationRepository mLocationRepo;
    private Repository mRepository;

    private MutableLiveData<LatLng> startPoint;
    private MutableLiveData<LatLng> endPoint;
    private MutableLiveData<DirectionsAndCPInfo> chosenRoute;
    private MutableLiveData<GoogleMapsDirections> updatingRouteDirections;
    private boolean navigationStarted = false;

    public RouteOverviewViewModel(Application application){
        super(application);
        Log.d("RouteOverviewViewModel","creating Routeoverview view model");
        mLocationRepo = LocationRepository.getLocationRepository(application.getApplicationContext());
        currentLocation = mLocationRepo.getLocation();
        previousLocation = currentLocation.getValue();
        mRepository = Repository.getInstance(application);
        chosenRoute = new MutableLiveData<>();
    }

    public RouteOverviewViewModel(Application application, CarParkInfo carParkInfo){
        this(application);
        Log.d("RouteOverviewViewModel", "constructor with static info");
        mRepository.updateRoutes(currentLocation.getValue(), carParkInfo.getLatLng(),
            new DirectionsCallback(){
                @Override
                public void onSuccess(GoogleMapsDirections gMapsDirections) {
                    Log.d("RouteOverviewViewModel","succeeded in creating route from static info in constructor");
                    chosenRoute.setValue(new DirectionsAndCPInfo(carParkInfo,gMapsDirections,currentLocation.getValue()));
                    createMediator();
                }

                @Override
                public void onFailure() {
                    Log.d("RouteOverviewViewModel","failed to create route from static info in constructor");
                }
            });
    }

    public RouteOverviewViewModel(@NonNull Application application, DirectionsAndCPInfo initialChosenRoute) {
        this(application);
        Log.d("RouteOverviewViewModel", "constructor with chosen route");
        chosenRoute.setValue(initialChosenRoute);
        updatingRouteDirections = new MutableLiveData<>();
        updatingRouteDirections.setValue(chosenRoute.getValue().getGoogleMapsDirections());
        createMediator();

    }

    public LiveData<DirectionsAndCPInfo> getChosenRoute(){
        return chosenRoute;
    }

    public LiveData<GoogleMapsDirections> getUpdatingRoute(){
        return updatingRouteDirections;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mLocationRepo.stopLocationUpdates();
    }

    private void createMediator(){
        mediatorCurrentLoc.addSource(currentLocation, newCurrentLocation -> {
            Log.d("SelectRouteViewModel", "mediatorCurrentLoc changed");
            if(navigationStarted){
                Log.d("SelectRouteViewModel", "current loc changed and  navigation started");
                //if location is changed
                if (!currentLocation.getValue().equals(previousLocation)){
                    Log.d("SelectRouteViewModel", "current loc: " + currentLocation.getValue().toString() + " prev loc: " + previousLocation.toString());
                    Log.d("SelectRouteViewModel", "current loc != previous loc, calling update routes");
                    mRepository.updateRoutes((LatLng) newCurrentLocation, chosenRoute.getValue().getDestinationLatLng(),
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

    public LiveData<LatLng> getCurrentLoc(){
        return currentLocation;
    }
}