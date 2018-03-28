package com.example.chiilek.parkme.test;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.List;

public class testViewModel extends ViewModel{
    private MutableLiveData<String> searchTerm;
    private LiveData<List<String>> testList;
    private TestRepo testRepo;

    public testViewModel() {
        searchTerm = new MutableLiveData<>();
        //calls repository to search again whenever newDestination is changed by SelectRouteVM.search()
        testList = Transformations.switchMap(searchTerm, (String newTerm) ->
                testRepo.foo(newTerm));
    }

    public LiveData<String> getSearchTerm(){
        return this.searchTerm;
    }

    public void setData(String newTerm){
        this.searchTerm.setValue(newTerm);
    }

    private void loadDestination() {
        //get data from persistence
    }


}

