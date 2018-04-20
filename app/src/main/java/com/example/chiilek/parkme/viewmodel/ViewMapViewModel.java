package com.example.chiilek.parkme.viewmodel;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.util.Log;

import com.example.chiilek.parkme.entity.CarParkInfo;
import com.example.chiilek.parkme.repository.LocationRepository;
import com.example.chiilek.parkme.repository.Repository;
import com.example.chiilek.parkme.repository.callbacks.SearchNearbyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class ViewMapViewModel extends AndroidViewModel{
    private MutableLiveData<LatLng> msearchTerm;
    //list of nearest carparks to search term
    private MutableLiveData<List<CarParkInfo>> mcpListFromRepo;
    private MutableLiveData<List<CarParkInfo>> mcpListForActivity;
    private MediatorLiveData mcpListMediator = new MediatorLiveData();
    private LiveData<LatLng> currentLocation;
    private Repository mRepository;
    private LocationRepository mLocationRepo;


    public ViewMapViewModel(Application application){
        super(application);
        this.mRepository = Repository.getInstance(this.getApplication());
        mLocationRepo = LocationRepository.getLocationRepository(this.getApplication());
        msearchTerm = new MutableLiveData<>();
        currentLocation = mLocationRepo.getLocation();
        mcpListForActivity = mRepository.searchNearbyCarParks(currentLocation.getValue(), new SearchNearbyCallback() {
            @Override
            public void onSuccess() {
                Log.d("ViewMapViewModel", "In constructor onSuccess, Availability call in repository completed.");
                mcpListFromRepo = mRepository.getViewMapCPList();
                //mediator live data. when mcarparklist, the live data from repo, changes, it updates the to return which is observed by activity
                mcpListMediator.addSource(mcpListFromRepo, newCPList -> {
                    Log.d("ViewMapViewModel", "Mediator Live Data detected change in CPList. posting now.");
                    mcpListForActivity.postValue((List<CarParkInfo>)newCPList);
                });
            }

            @Override
            public void onFailure() {
                Log.d("ViewMapViewModel", "In constructor onFailure, Availability call in repository failed.");
            }
        });





        //searches nearby everytime msearchterm changes, when called by VMMP.setSearchTerm()
//        mcpListFromRepo = Transformations.switchMap(msearchTerm, (LatLng newDestination)->
//                mRepository.searchNearbyCarParks(newDestination));
        //update currentLocation
        currentLocation = Transformations.map(mLocationRepo.getLocation(),newLocation->{
            return newLocation;
        });
    }
    //called by button in ViewMapActivity and triggers transformation
    public void setSearchTerm(LatLng searchTerm){
        msearchTerm.setValue(searchTerm);
    }

    /*
        // put this in the ViewMapActivity GMAP fragment with the search button to update searchTerm

        model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        searchButton.setOnClickListener(item -> {
        model.setSearchTerm(item);
        //create new SelectRouteActivity
        }
     */

    //displays popup car park info
    public MutableLiveData<List<CarParkInfo>> getCarParkInfo(LatLng location){
        mcpListForActivity = mRepository.searchNearbyCarParks(location, new SearchNearbyCallback() {
            @Override
            public void onSuccess() {
                Log.d("ViewMapViewModel", "In SearchNearbyCallback onSuccess, Availability call in repository completed.");
                mcpListFromRepo = mRepository.getViewMapCPList();
            }

            @Override
            public void onFailure() {
                Log.d("ViewMapViewModel", "In SearchNearbyCallback onFailure, Availability call in repository failed.");
            }
        });
        return mcpListForActivity;
    }


    public MutableLiveData<List<CarParkInfo>> getCarParkInfo() { return mcpListForActivity; }

    public MediatorLiveData getMcpListMediator() { return mcpListMediator; }

    public LiveData<LatLng> getSearchTerm() {
        return msearchTerm;
    }

    public LiveData<LatLng> getCurrentLocation(){return currentLocation;}

/*    @Override
    protected void onCleared() {
        super.onCleared();
        mLocationRepo.stopLocationUpdates();
    }*/
}

