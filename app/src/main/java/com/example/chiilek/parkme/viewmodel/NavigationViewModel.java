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
import com.example.chiilek.parkme.repository.callbacks.AvailabilityCountCallback;
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
    private MutableLiveData<Integer> availabilityStatus;
    private int carParkAvailabilityThreshold = 5;

    public NavigationViewModel(Application application){
        super(application);
        Log.d("NavigationViewModel","creating navigation view model");
        mLocationRepo = LocationRepository.getLocationRepository(application.getApplicationContext());
        currentLocation = mLocationRepo.getLocation();
        previousLocation = currentLocation.getValue();
        mRepository = Repository.getInstance(application);
        availabilityStatus = new MutableLiveData<Integer>();
        availabilityStatus.setValue(-1);
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
                                    updatingRouteDirections.setValue(gMapsDirections);
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

        //timer to check availability every 5 minutes
        Timer mTimer = new Timer();
        mTimer.schedule(new TimerTask(){
            @Override
            public void run(){
                String cpNumber = chosenRoute.getCarParkInfo().getCPNumber();
                mRepository.checkAvailability(cpNumber, new AvailabilityCountCallback() {
                    @Override
                    public void onSuccess(int availabilityCount) {
                        Log.d("NavigationViewModel", "in 5 min update, avail onSuccess");
                        if (availabilityCount < carParkAvailabilityThreshold) {
                            onAvailZero();
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d("NavigationViewModel", "in 5 min update, avail onFailure");
                        // do nothing
                    }
                });
            };
            //check every 5 min (18,000 seconds)
        },5000,10000);
    }

    private void onAvailZero(){
        Log.d("NavigationViewModel", "onAvailZero: in avail zero");
        mRepository.getDirectionsAndCPs(currentLocation.getValue(), chosenRoute.getDestinationLatLng(), new GetRoutesCallback() {
            @Override
            public void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList) {
                String oldCarParkNumber = chosenRoute.getCarParkInfo().getCPNumber();
                if(directionsAndCPInfoList.size() == 1){
                    availabilityStatus.setValue(1);
                } else if(directionsAndCPInfoList.get(0).getCarParkInfo().getCPNumber().equals(oldCarParkNumber)){
                    directionsAndCPInfoList.remove(0);
                }
                alternativeRoute = directionsAndCPInfoList.get(0);
                availabilityStatus.setValue(0);

            }
            @Override
            public void onFailure(int errorCode) {
                Log.d("NavigationViewModel", "on avail zero onFailure: Directions and CP API call failed.");
            }
        });
    }

    public MutableLiveData<LatLng> getCurrentLoc(){ return currentLocation; }

    public MediatorLiveData getMediatorCurrentLoc() { return mediatorCurrentLoc; }

    //TODO: remove this method, dont think its being used for the rerouting.
//    private void rerouteToNewCarPark(){
//        chosenRoute = alternativeRoute;
//        updatingRouteDirections.postValue(alternativeRoute.getGoogleMapsDirections());
//    }

    public MutableLiveData<Integer> getAvailabilityStatus() { return availabilityStatus; }

    public DirectionsAndCPInfo getAlternativeRoute() { return alternativeRoute; }

}