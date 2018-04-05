package com.example.chiilek.parkme.ViewMap;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import com.example.chiilek.parkme.data_classes.CarParkDatum;
import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
import com.example.chiilek.parkme.repository.LocationRepository;
import com.example.chiilek.parkme.repository.Repository;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class ViewMapViewModel extends AndroidViewModel{
    private MutableLiveData<LatLng> msearchTerm;
    //list of nearest carparks to search term
    private LiveData<List<CarParkStaticInfo>> mcarParkList;
    private LiveData<LatLng> currentLocation;
    private Repository mRepository;
    private LocationRepository mLocationRepo;

    public ViewMapViewModel(Application application){
        super(application);
        this.mRepository = Repository.getInstance(this.getApplication());
        mLocationRepo = LocationRepository.getLocationRepository(this.getApplication());
        msearchTerm = new MutableLiveData<>();
        //TODO get current location from repository?
        currentLocation = mLocationRepo.getLocation();
        mcarParkList = mRepository.searchNearbyCarParks(currentLocation.getValue());

        //searches nearby everytime msearchterm changes, when called by VMMP.setSearchTerm()
        mcarParkList = Transformations.switchMap(msearchTerm, (LatLng newDestination)->
                mRepository.searchNearbyCarParks(newDestination));
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

    //displays popup carpark info
    //TODO find out what google maps returns when you press on a pin
    public CarParkDatum getCarParkInfo(String toFind){
        /*if (mcarParkList != null){
            for ( CarParkStaticInfo carpark: mcarParkList.getValue()){
                if (carpark.getCarParkNumber().equals(toFind))
                    return carpark;
            }
        }*/
        return null;
    }

    public LiveData<LatLng> getSearchTerm() {
        return msearchTerm;
    }

    public LiveData<List<CarParkStaticInfo>> getCarParkList() {
        return mcarParkList;
    }

    public LiveData<LatLng> getCurrentLocation(){return currentLocation;}


}

