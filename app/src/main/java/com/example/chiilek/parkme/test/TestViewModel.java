package com.example.chiilek.parkme.test;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.List;

public class TestViewModel extends AndroidViewModel{
    private MutableLiveData<Integer> searchTerm;
    private LiveData<List<TestEntity>> testList;
    private TestRepo testRepo;

    public TestViewModel(Application application) {
        super(application);
        this.testRepo = TestRepo.getInstance(this.getApplication());
        searchTerm = new MutableLiveData<>();
        //calls repository to search again whenever newDestination is changed by SelectRouteVM.search()
        testList = Transformations.switchMap(searchTerm, (Integer id) ->
                testRepo.getEntityById(id));
    }

    public LiveData<Integer> getSearchTerm(){
        return this.searchTerm;
    }

    public LiveData<List<TestEntity>> getList(){
        return this.testList;
    }

    public void setData(int newTerm){
        this.searchTerm.setValue(newTerm);
    }

    public void initialize() {
        testRepo.initialize();
    }


}

