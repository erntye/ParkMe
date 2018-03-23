package com.example.chiilek.parkme;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class ViewMapViewModel extends ViewModel{
    private MutableLiveData<Destination> destination;

    public MutableLiveData<Destination> getDestination() {
        if (destination == null) {
            destination = new MutableLiveData<Destination>();
            loadDestination();
        }
        return destination;
    }

    private void loadDestination() {
        //get data from persistence
    }

}

