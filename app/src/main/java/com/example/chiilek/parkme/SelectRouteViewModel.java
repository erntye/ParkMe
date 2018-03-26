package com.example.chiilek.parkme;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.example.chiilek.parkme.data_classes.CarParkDatum;
import com.example.chiilek.parkme.repository.Repository;

import java.util.List;

public class SelectRouteViewModel extends ViewModel{
    private MutableLiveData<Location> destination;
    private MutableLiveData<Location> startPoint;
    private LiveData<List<CarParkDatum>> carParkList;
    private Repository mRepository;

    //to be set up by SelectRouteActivity
    public SelectRouteViewModel(){
        destination = new MutableLiveData<>();
        //calls repository to search again whenever newDestination is changed by SelectRouteVM.search()
        carParkList = Transformations.switchMap(destination, (Location newDestination)->
                mRepository.searchTop5(startPoint.getValue(), newDestination));
    }

    public void search(LiveData<Location> searchTerm){
        if (searchTerm!= null)
            destination.setValue(searchTerm.getValue());
    }

    public LiveData<List<CarParkDatum>> getCarParks() {
        if (carParkList == null) {
            carParkList = new MutableLiveData<List<CarParkDatum>>();
            search(destination);
        }
        return carParkList;
    }

    //called by SelectRouteActivity upon choosing a certain car park
    public List<Location> plotRoute(){
        //calls REPO to call GMAP API to get the polyline
        return null;
    }

}
