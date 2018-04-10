package com.example.chiilek.parkme;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.util.Log;

import com.example.chiilek.parkme.data_classes.availability_classes.CarParkDatum;
import com.example.chiilek.parkme.data_classes.DirectionsAndCPInfo;
import com.example.chiilek.parkme.repository.GetRoutesCallback;
import com.example.chiilek.parkme.repository.LocationRepository;
import com.example.chiilek.parkme.repository.Repository;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class SelectRouteViewModel extends AndroidViewModel {
    private MutableLiveData<LatLng> destination;
    private MutableLiveData<LatLng> startPoint;
    private MutableLiveData<CarParkDatum> chosenCarPark;
    private MutableLiveData<List<DirectionsAndCPInfo>> directionsAndCarParksList;
    private MediatorLiveData mediatorDirAndCPList = new MediatorLiveData<>();
    private LiveData<List<LatLng>> routeToPlot;
    private Repository mRepository;
    private LocationRepository mLocationRepo;

    //to be set up by SelectRouteActivity
    public SelectRouteViewModel(Application application){
        super(application);
        this.mRepository = Repository.getInstance(this.getApplication());
        destination = new MutableLiveData<>();
        mLocationRepo = LocationRepository.getLocationRepository(this.getApplication());
        startPoint.setValue(mLocationRepo.getLocation().getValue());

        mediatorDirAndCPList.addSource(destination,  newDestination -> mRepository.getDirectionsAndCPs(startPoint.getValue(), (LatLng)newDestination,
                new GetRoutesCallback() {
                    @Override
                    public void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList) {
                        directionsAndCarParksList.setValue(directionsAndCPInfoList);
                    }
                    @Override
                    public void onFailure() {
                        Log.d("SelectRouteViewModel", "onFailure add source destination get routes callback");
                    }}));

        mediatorDirAndCPList.addSource(startPoint,newStartPoint -> mRepository.getDirectionsAndCPs((LatLng)newStartPoint, destination.getValue(),
                new GetRoutesCallback() {
                    @Override
                    public void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList) {
                        directionsAndCarParksList.setValue(directionsAndCPInfoList);
                    }
                    @Override
                    public void onFailure() {
                        Log.d("SelectRouteViewModel", "onFailure add source destination get routes callback");
                    }}));

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
    public void setDestination(LiveData<LatLng> searchTerm){
        if (searchTerm!= null)
            destination.setValue(searchTerm.getValue());
    }
    //called by SelectRouteActivity whenever user inputs a new search term start point
    public void setStartPoint(LiveData<LatLng> searchTerm){
        if (searchTerm!= null)
            startPoint.setValue(searchTerm.getValue());
    }
    //called by SelectRouteActivity upon choosing a certain car park to trigger transformation
    public void setChosenCarPark(CarParkDatum choice){
        chosenCarPark.setValue(choice);
    }
    //expose for observation to viewmodel
    public LiveData<List<DirectionsAndCPInfo>> getDirectionsAndCarParks() {
        if (directionsAndCarParksList == null) {
            directionsAndCarParksList = new MutableLiveData<List<DirectionsAndCPInfo>>();
            mRepository.getDirectionsAndCPs(startPoint.getValue(),destination.getValue(), new GetRoutesCallback() {
                @Override
                public void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList) {
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
    public LiveData<List<LatLng>> getRouteToPlot() {
        return routeToPlot;
    }
}