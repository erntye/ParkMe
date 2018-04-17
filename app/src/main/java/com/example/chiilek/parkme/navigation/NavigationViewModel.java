package com.example.chiilek.parkme.navigation;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.chiilek.parkme.api_controllers.directions_api.DirectionsCallback;
import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
import com.example.chiilek.parkme.data_classes.DirectionsAndCPInfo;
import com.example.chiilek.parkme.data_classes.availability_classes.CarParkDatum;
import com.example.chiilek.parkme.data_classes.directions_classes.GoogleMapsDirections;
import com.example.chiilek.parkme.repository.GetRoutesCallback;
import com.example.chiilek.parkme.repository.LocationRepository;
import com.example.chiilek.parkme.repository.Repository;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

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
    private boolean navigationStarted = false;
    private MutableLiveData<Boolean> availIsZero = new MutableLiveData<Boolean>();

    public NavigationViewModel(Application application){
        super(application);
        Log.d("NavigationViewModel","creating navigation view model");
        mLocationRepo = LocationRepository.getLocationRepository(application.getApplicationContext());
        currentLocation = mLocationRepo.getLocation();
        previousLocation = currentLocation.getValue();
        mRepository = Repository.getInstance(application);
    }

    public NavigationViewModel(Application application, CarParkStaticInfo carParkStaticInfo){
        this(application);
        Log.d("NavigationViewModel", "constructor with static info");
        mRepository.updateRoutes(currentLocation.getValue(),carParkStaticInfo.getLatLng(),
            new DirectionsCallback(){
                @Override
                public void onSuccess(GoogleMapsDirections gMapsDirections) {
                    Log.d("NavigationViewModel","succeeded in creating route from static info in constructor");
                    chosenRoute = new DirectionsAndCPInfo(carParkStaticInfo,gMapsDirections,currentLocation.getValue());
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
    }

    private void onAvailZero(){
        mRepository.getDirectionsAndCPs(currentLocation.getValue(), chosenRoute.getDestinationLatLng(), new GetRoutesCallback() {
            @Override
            public void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList) {
                String oldCarParkNumber = chosenRoute.getCarParkDatum().getCarParkNumber();
                if(directionsAndCPInfoList.get(0).getCarParkDatum().getCarParkNumber().equals(oldCarParkNumber)){
                    directionsAndCPInfoList.remove(0);
                }
                chosenRoute = directionsAndCPInfoList.get(0);
                updatingRouteDirections.postValue(chosenRoute.getGoogleMapsDirections());
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
}