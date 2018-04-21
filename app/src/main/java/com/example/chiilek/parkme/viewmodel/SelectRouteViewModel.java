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

import java.util.ArrayList;
import java.util.List;

public class SelectRouteViewModel extends AndroidViewModel {
    private MutableLiveData<LatLng> endPoint;
    private MutableLiveData<LatLng> startPoint;
    private MutableLiveData<List<DirectionsAndCPInfo>> directionsAndCarParksList;
    private MediatorLiveData mediatorDirAndCPList = new MediatorLiveData<>();
    private Repository mRepository;
    private LocationRepository mLocationRepo;
    private MutableLiveData<LatLng> currentLocation;
    private DirectionsAndCPInfo chosenRoute;
    private MutableLiveData<GoogleMapsDirections> updatingRouteDirections;
    private boolean navigationStarted = false;
    private int status = 0;


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
        directionsAndCarParksList = new MutableLiveData<>();

        mediatorDirAndCPList.addSource(startPoint,newStartPoint ->{
            Log.d("SelectRouteViewModel", "mediator activated on start point changed, get directions and CP called");
            mRepository.getDirectionsAndCPs((LatLng) newStartPoint, endPoint.getValue(),
                    new GetRoutesCallback() {
                        @Override
                        public void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList) {
                            directionsAndCarParksList.postValue(directionsAndCPInfoList);
                        }
                        @Override
                        public void onFailure(int errorCode) {
                            Log.d("SelectRouteViewModel", "onFailure add source endPoint get routes callback");
                            status = errorCode;
                            directionsAndCarParksList.postValue(new ArrayList<DirectionsAndCPInfo>());
                        }
                    });
        });
    }

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
                public void onFailure(int errorCode) {
                    Log.d("SelectRouteViewModel", "observation exposure onFailure add source endPoint get routes callback");
                    status = errorCode;
                    directionsAndCarParksList.setValue(new ArrayList<DirectionsAndCPInfo>());
                }});
        }
        return directionsAndCarParksList;
    }

    //expose for observation to viewmodel
    public MediatorLiveData getMediatorDirAndCPList() {
        return mediatorDirAndCPList;
    }

    public int getStatus(){return status;}

    public LiveData<GoogleMapsDirections> getGoogleMapsDirections(){ return updatingRouteDirections; }

}