package com.example.chiilek.parkme.repository;


import android.arch.lifecycle.LiveData;

import com.example.chiilek.parkme.Destination;
import com.example.chiilek.parkme.data_classes.CarParkDatum;

import java.util.List;

public class Repository {
    public LiveData<List<CarParkDatum>> search(Destination destination){
        //dosomething
        return null;
    }
}
