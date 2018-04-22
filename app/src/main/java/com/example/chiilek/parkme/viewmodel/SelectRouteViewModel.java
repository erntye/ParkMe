package com.example.chiilek.parkme.viewmodel;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.chiilek.parkme.entity.DirectionsAndCPInfo;
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

    //error code for the directions api call or the static carpark search
    private int status = 0;


    //to be set up by SelectRouteActivity
    public SelectRouteViewModel(Application application, LatLng chosenDestination){
        super(application);
        this.mRepository = Repository.getInstance(this.getApplication());
        mLocationRepo = LocationRepository.getLocationRepository(this.getApplication());
        endPoint = new MutableLiveData<>();
        endPoint.setValue(chosenDestination);
        currentLocation = mLocationRepo.getLocation();
        //start point initializes at current location but the user can type into the bar to set it
        startPoint = new MutableLiveData<>();
        startPoint.setValue(currentLocation.getValue());
        directionsAndCarParksList = new MutableLiveData<>();

        //create mediator to change search terms when start point changed
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

    //called by SelectRouteActivity whenever user inputs a new search term start point
    public void setStartPoint(LatLng searchTerm){
        if (searchTerm!= null)
            Log.d("SelectRouteViewModel", "setStartPoint: " + searchTerm.toString());
            startPoint.setValue(searchTerm);
    }

    /**
     * Exposes the <code>LiveData</code> object containing the list of Directions and Info for the
     * nearest car parks. If the list is not previously set, initialize list by making a call to the
     * <code>Repository</code>
     * @return
     */
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

    /**
     * Exposes the <code>MediatorLiveData</code> for observation by <code>SelectRouteActivity</code>
     * @return the <code>MediatorLiveData</code> object
     */
    public MediatorLiveData getMediatorDirAndCPList() {
        return mediatorDirAndCPList;
    }

    /**
     * Returns the status for the route recommendation
     * 1 is no car parks in the vicinity
     * 2 is no directions from current location
     * @return status for the Route Recommendation
     */
    public int getStatus(){return status;}


}