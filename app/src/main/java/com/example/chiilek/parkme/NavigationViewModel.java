/*
package com.example.chiilek.parkme;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

public class NavigationViewModel extends ViewModel{
    private MutableLiveData<Destination> destination;
    private MutableLiveData<List<CarPark>> carParkList;
    private Repository mRepository;

    public LiveData<List<CarPark>> getCarParks() {
        if (carParkList == null) {
            carParkList = new MutableLiveData<List<CarPark>>();
            loadCarParkList();
        }
        return carParkList;
    }

    private void loadCarParkList() {
        //get data from persistence
    }

}*/
