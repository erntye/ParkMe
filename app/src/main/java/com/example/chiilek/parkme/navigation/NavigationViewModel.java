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
                    DirectionsAndCPInfo generatedRoute = new DirectionsAndCPInfo(carParkStaticInfo,gMapsDirections,currentLocation.getValue());
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


    public PolylineOptions getInitialRoute(){
        return chosenRoute.getGoogleMapsDirections().getPolylineOptions();
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
}