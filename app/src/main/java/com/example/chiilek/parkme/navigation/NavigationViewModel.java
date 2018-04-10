package com.example.chiilek.parkme.navigation;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.Layout;

import com.example.chiilek.parkme.api_controllers.directions_api.DirectionsCallback;
import com.example.chiilek.parkme.data_classes.DirectionsAndCPInfo;
import com.example.chiilek.parkme.data_classes.directions_classes.GoogleMapsDirections;
import com.example.chiilek.parkme.repository.LocationRepository;
import com.example.chiilek.parkme.repository.Repository;
import com.google.android.gms.maps.model.LatLng;

public class NavigationViewModel extends AndroidViewModel {

    private MediatorLiveData<GoogleMapsDirections> currentRoute = new MediatorLiveData<GoogleMapsDirections>();
    private MutableLiveData<GoogleMapsDirections> liveCurrentRoute;
    private MutableLiveData<LatLng> currentLocation;
    private LocationRepository mLocationRepo;
    private DirectionsAndCPInfo initialRoute;
    private Repository mRepository;


    public NavigationViewModel(@NonNull Application application) {
        super(application);
        currentLocation.setValue(mLocationRepo.getLocation().getValue());
        mRepository = Repository.getInstance(application);
        //TODO: initialise initialRoute
        LatLng destinationCP = new LatLng(initialRoute.getDestinationLatitude(), initialRoute.getDestinationLongitude());


        currentRoute.addSource(currentLocation, newCurrentLocation ->
                mRepository.updateRoutes(newCurrentLocation, destinationCP, new DirectionsCallback() {
            @Override
            public void onSuccess(GoogleMapsDirections gMapsDirections) {

            }

            @Override
            public void onFailure() {

            }
        }));
    }
}