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

/**
 * This <code>ViewModel</code> is created for <code>ViewMapActivity</code>. When the <code>Activity</code>
 * is initialized or when the P button is pressed, this <code>ViewModel</code> makes an asynchronous call
 * to the <code>Repository </code> for the list of nearest car park and subscribes to the notification
 * using MediatorLiveData. After the <code>Repository </code> has been updated, it notifies
 * <code>ViewMapActivity</code>, which will then update the information on the UI
 * <p>
 * The <code>ViewModel</code> also tracks the current location <code>LiveData</code> of the user from
 * the <code>LocationRepository</code> to display on the map
 *
 * @see com.example.chiilek.parkme.ui.ViewMapActivity
 * @see Repository
 * @see com.example.chiilek.parkme.database.AppDatabase
 * @see LiveData
 * @see MediatorLiveData
 */
public class ViewMapViewModel extends AndroidViewModel{
    //list of nearest carparks to search term
    private MutableLiveData<List<CarParkInfo>> mcpListFromRepo;
    private MutableLiveData<List<CarParkInfo>> mcpListForActivity;
    private MediatorLiveData mcpListMediator = new MediatorLiveData();
    private LiveData<LatLng> currentLocation;
    private Repository mRepository;
    private LocationRepository mLocationRepo;

    /**
     * Default constructor.
     * @param application To be passed into <code>Repository</code> to instantiate <code>AppDatabase</code>
     */
    public ViewMapViewModel(Application application){
        super(application);
        this.mRepository = Repository.getInstance(this.getApplication());
        mLocationRepo = LocationRepository.getLocationRepository(this.getApplication());
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
        Log.d("ViewMapViewModel", "calling repo searchNearbyCarparks");

    }

    /**
     * First returns a static list from database and then makes an asynchronous call from <code>Repository</code>
     * When the call is completed, mcpListFromRepo is updated and the <code>MediatorLiveData</code>
     * is then notified, which in turn then notifies the <code>Activity</code> through mcpListForActivity
     * which is being observed by the <code>getCarParkInfo()</code> method
     * @return the <code>LiveData</code> object containing a list of CarParkInfo objects from
     *          <code>Repository</code>
     */
    public LiveData<List<CarParkInfo>> getCarParkInfo(LatLng location){
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

    /**
     * Exposes the list of car park info <code>LiveData</code> for observation by <code>ViewMapActivity</code>
     * @return the <code>LiveData</code> object containing a list of CarParkInfo objects
     */
    public LiveData<List<CarParkInfo>> getCarParkInfo() { return mcpListForActivity; }

    /**
     * Exposes the <code>MediatorLiveData</code> for observation by <code>ViewMapActivity</code>
     * @return the <code>MediatorLiveData</code> object
     */
    public MediatorLiveData getMcpListMediator() { return mcpListMediator; }

    public LiveData<LatLng> getCurrentLocation(){return currentLocation;}

}

