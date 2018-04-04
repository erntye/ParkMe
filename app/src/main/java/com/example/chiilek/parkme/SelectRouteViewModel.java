package com.example.chiilek.parkme;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import com.example.chiilek.parkme.data_classes.CarParkDatum;
import com.example.chiilek.parkme.repository.Repository;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class SelectRouteViewModel extends AndroidViewModel {
    private MutableLiveData<LatLng> destination;
    private MutableLiveData<LatLng> startPoint;
    private MutableLiveData<CarParkDatum> chosenCarPark;
    private LiveData<List<CarParkDatum>> carParkList;
    private LiveData<List<LatLng>> routeToPlot;
    private Repository mRepository;

    //to be set up by SelectRouteActivity
    public SelectRouteViewModel(Application application){
        super(application);
        this.mRepository = Repository.getInstance(this.getApplication());
        destination = new MutableLiveData<>();
        //calls repository to search again whenever destination is changed by SelectRouteVM.search()
        carParkList = Transformations.switchMap(destination, (LatLng newDestination)->
                mRepository.searchTop5(startPoint.getValue(), newDestination));
        //calls repository to search again whenever start point is changed
        carParkList = Transformations.switchMap(startPoint, (LatLng newStartPoint)->
                mRepository.searchTop5(newStartPoint, destination.getValue()));
/*        routeToPlot = Transformations.switchMap(chosenCarPark, (CarParkDatum carpark)->
                mRepository.getRoutePolyline(carpark));*/
        //TODO initialize carparkList and route(?)
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
    public LiveData<List<CarParkDatum>> getCarParks() {
        if (carParkList == null) {
            carParkList = new MutableLiveData<List<CarParkDatum>>();
            mRepository.searchTop5(startPoint.getValue(),destination.getValue());
        }
        return carParkList;
    }
    //expose for observation to viewmodel
    public LiveData<List<LatLng>> getRouteToPlot() {
        return routeToPlot;
    }
}
