package com.example.chiilek.parkme.test;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.chiilek.parkme.repository.LocationRepository;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class TestViewModel extends AndroidViewModel{
    private MutableLiveData<Integer> searchTerm;
    private LiveData<List<TestEntity>> testList;
    private MutableLiveData<LatLng> currentLocation;
    private MediatorLiveData mediatorData = new MediatorLiveData<>();
    private TestRepo testRepo;
    private LocationRepository mLocationRepo;


    //need to use androidviewmodel class so that can get Context, which needs to be passed to Database for Room
    public TestViewModel(Application application) {
        super(application);
        Log.d("TestViewModel","viewmodel created");
        this.testRepo = TestRepo.getInstance(this.getApplication());
        //initialize database if empty
        if (testRepo.getAllEntity() == null)
            testRepo.initialize();
        //initialize variables
        searchTerm = new MutableLiveData<>();
        testList = testRepo.getAllEntity();
        //Log.d("TestViewModel", "test list size is "+ testList.getValue().size());
        mLocationRepo = LocationRepository.getLocationRepository(application.getApplicationContext());
        currentLocation = mLocationRepo.getLocation();


        mediatorData.addSource(currentLocation, newLocation ->{
                Log.d("TestViewModel","location changed: inside mediator live data ");
                testRepo.testMediatorFunc();
                }
        );
        //calls repository to search again whenever newDestination is changed by SelectRouteVM.search()
        testList = Transformations.switchMap(searchTerm, (Integer id) ->
                testRepo.getEntityById(id));
    }

    public LiveData<Integer> getSearchTerm(){
        if (searchTerm == null)
            searchTerm = new MutableLiveData<>();
        return this.searchTerm;
    }

    public LiveData<List<TestEntity>> getList(){
/*        if (testList == null) {
            testList = new MutableLiveData<>();
            testList = testRepo.getAllEntity();
        }*/
        return this.testList;
    }
    public LiveData<LatLng> getCurrentLocation(){
        return this.currentLocation;
    }

    public MediatorLiveData getMediator(){
        return mediatorData;
    }

    public void setData(int newTerm){
        Log.d("ViewModel","Data set to " + newTerm);
        this.searchTerm.setValue(newTerm);
    }
    public void setCurrentLocation(LatLng location){
        this.currentLocation.setValue(location);
    }

    public void initialize() {
        testRepo.initialize();
    }


}

