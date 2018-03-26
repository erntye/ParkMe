package com.example.chiilek.parkme;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.example.chiilek.parkme.data_classes.CarParkDatum;
import com.example.chiilek.parkme.repository.Repository;

import java.util.List;

public class NavigationViewModel extends ViewModel{
    private MutableLiveData<Destination> destination;
    private LiveData<List<CarParkDatum>> carParkList;
    private Repository mRepository;

    public NavigationViewModel(){
        destination = new MutableLiveData<>();
        carParkList = Transformations.switchMap(destination, (Destination newDestination)->
                mRepository.search(newDestination));
    }

    public LiveData<List<CarParkDatum>> getCarParks() {
        if (carParkList == null) {
            carParkList = new MutableLiveData<List<CarParkDatum>>();
            loadCarParkList();
        }
        return carParkList;
    }

    public void search(Destination searchTerm){
        if (searchTerm!= null)
            destination.setValue(searchTerm);
    }

    private void loadCarParkList() {
        //get data from persistence
    }

}
