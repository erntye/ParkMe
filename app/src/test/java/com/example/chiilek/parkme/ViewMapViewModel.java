package com.example.chiilek.parkme;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class ViewMapViewModel extends ViewModel{
    private MutableLiveData<String> destination;

    public LiveData<String> getDestination() {
        if (destination == null) {
            destination = new MutableLiveData<String>();
            loadDestination();
        }
        return destination;
    }

    private void loadDestination() {
        //get data from persistence
    }

}

