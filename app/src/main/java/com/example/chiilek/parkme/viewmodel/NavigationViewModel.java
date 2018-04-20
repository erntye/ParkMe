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
import com.example.chiilek.parkme.repository.callbacks.GetRoutesCallback;
import com.example.chiilek.parkme.repository.LocationRepository;
import com.example.chiilek.parkme.repository.Repository;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NavigationViewModel extends AndroidViewModel {

    private MediatorLiveData mediatorCurrentLoc = new MediatorLiveData<>();

    private LatLng previousLocation;
    private MutableLiveData<LatLng> currentLocation;

    private LocationRepository mLocationRepo;
    private Repository mRepository;

    private MutableLiveData<LatLng> startPoint;
    private MutableLiveData<LatLng> endPoint;
    private DirectionsAndCPInfo chosenRoute;
    private MutableLiveData<GoogleMapsDirections> updatingRouteDirections;
    private boolean navigationStarted = true;
    private DirectionsAndCPInfo alternativeRoute;
    private MutableLiveData<Boolean> isAvailZero;

    public NavigationViewModel(Application application){
        super(application);
        Log.d("NavigationViewModel","creating navigation view model");
        mLocationRepo = LocationRepository.getLocationRepository(application.getApplicationContext());
        currentLocation = mLocationRepo.getLocation();
        previousLocation = currentLocation.getValue();
        mRepository = Repository.getInstance(application);
        isAvailZero = new MutableLiveData<Boolean>();
        isAvailZero.setValue(false);
    }

    public NavigationViewModel(Application application, CarParkInfo carParkInfo){
        this(application);
        Log.d("NavigationViewModel", "constructor with static info");
        mRepository.updateRoutes(currentLocation.getValue(), carParkInfo.getLatLng(),
            new DirectionsCallback(){
                @Override
                public void onSuccess(GoogleMapsDirections gMapsDirections) {
                    Log.d("NavigationViewModel","succeeded in creating route from static info in constructor");
                    chosenRoute = new DirectionsAndCPInfo(carParkInfo,gMapsDirections,currentLocation.getValue());
                    createMediator();
                }

                @Override
                public void onFailure() {
                    Log.d("NavigationViewModel","failed to create route from static info in constructor");
                }
            });
    }

    public NavigationViewModel(@NonNull Application application, DirectionsAndCPInfo initialChosenRoute) {
        this(application);
        Log.d("NavigationViewModel", "constructor with chosen route");
        this.chosenRoute = initialChosenRoute;
        updatingRouteDirections = new MutableLiveData<>();
        updatingRouteDirections.setValue(chosenRoute.getGoogleMapsDirections());
        createMediator();

    }

    public DirectionsAndCPInfo getChosenRoute(){
        return chosenRoute;
    }
    public PolylineOptions getInitialRoute(){
        return chosenRoute.getGoogleMapsDirections().getPolylineOptions();
    }

    public LiveData<GoogleMapsDirections> getUpdatingRoute(){
        return updatingRouteDirections;
    }

//    @Override
//    protected void onCleared() {
//        super.onCleared();
//        mLocationRepo.stopLocationUpdates();
//    }

    private void createMediator(){
        mediatorCurrentLoc.addSource(currentLocation, newCurrentLocation -> {
            Log.d("NavigationViewModel", "mediatorCurrentLoc changed");
            if(navigationStarted){
                Log.d("NavigationViewModel", "current loc changed and  navigation started");
                //if location is changed
                if (!currentLocation.getValue().equals(previousLocation)){
                    Log.d("NavigationViewModel", "current loc: " + currentLocation.getValue().toString() + " prev loc: " + previousLocation.toString());
                    Log.d("NavigationViewModel", "current loc != previous loc, calling update routes");
                    mRepository.updateRoutes((LatLng) newCurrentLocation, chosenRoute.getDestinationLatLng(),
                            new DirectionsCallback() {
                                @Override
                                public void onSuccess(GoogleMapsDirections gMapsDirections) {
                                    updatingRouteDirections.postValue(gMapsDirections);
                                    Log.d("NavigationViewModel", "navigation: updated route directions with new current loc.");
                                }

                                @Override
                                public void onFailure() {
                                    Log.d("NavigationViewModel", "navigation: update route failed");
                                }
                            });
                    previousLocation = currentLocation.getValue();
                }else Log.d("NavigationViewModel", "location not changed, do not need to update route");

            }
        });

        //timer to simulate availability = 0
        Timer mTimer = new Timer();
        mTimer.schedule(new TimerTask(){
            @Override
            public void run(){
                onAvailZero();
            };
        },5000,100000000);
    }

    private void onAvailZero(){
        Log.d("NavigationViewModel", "onAvailZero: in avail zero");
        mRepository.getDirectionsAndCPs(currentLocation.getValue(), chosenRoute.getDestinationLatLng(), new GetRoutesCallback() {
            @Override
            public void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList) {
                String oldCarParkNumber = chosenRoute.getCarParkInfo().getCPNumber();
                if(directionsAndCPInfoList.get(0).getCarParkInfo().getCPNumber().equals(oldCarParkNumber)){
                    directionsAndCPInfoList.remove(0);
                }
                alternativeRoute = directionsAndCPInfoList.get(0);
                isAvailZero.setValue(true);
            }
            @Override
            public void onFailure() {
                Log.d("NavigationViewModel", "on avail zero onFailure: Directions and CP API call failed.");
            }
        });
    }

    public MutableLiveData<LatLng> getCurrentLoc(){
        return currentLocation;
    }

    private void rerouteToNewCarPark(){
        chosenRoute = alternativeRoute;
        updatingRouteDirections.postValue(alternativeRoute.getGoogleMapsDirections());
    }

    public MutableLiveData<Boolean> getIsAvailZero() { return isAvailZero; }

    public DirectionsAndCPInfo getAlternativeRoute() { return alternativeRoute; }
}