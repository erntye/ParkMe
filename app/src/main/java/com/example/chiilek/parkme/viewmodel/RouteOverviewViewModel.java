package com.example.chiilek.parkme.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.chiilek.parkme.api.directions_api.DirectionsCallback;
import com.example.chiilek.parkme.entity.CarParkInfo;
import com.example.chiilek.parkme.entity.DirectionsAndCPInfo;
import com.example.chiilek.parkme.entity.directionsapi.GoogleMapsDirections;
import com.example.chiilek.parkme.repository.LocationRepository;
import com.example.chiilek.parkme.repository.Repository;
import com.google.android.gms.maps.model.LatLng;

/**
 * This <code>ViewModel</code> is created for <code>RouteOverviewActivity</code>. It uses
 * the <code>RouteOverviewViewModelCarParkFactory</code> and <code>RouteOverviewViewModelRouteFactory</code>
 * to overload its constructors in order to accept a CarParkInfo object if the object is created
 * from <code>CarParkPopUpActivity</code> or a <code>DirectionsAndCPInfo</code> object if
 * the activity is created from <code>SelectRouteActivity</code>. In the former case, it will call
 * the <code>Repository</code> to generate a <code>DirectionsAndCPInfo</code> so that the route
 * can be displayed in <code>RouteOverviewActivity</code>
 *
 * @see com.example.chiilek.parkme.ui.RouteOverviewActivity
 * @see RouteOverviewViewModelCarParkFactory
 * @see RouteOverviewViewModelRouteFactory
 * @see Repository
 * @see com.example.chiilek.parkme.database.AppDatabase
 * @see LiveData
 * @see DirectionsAndCPInfo
 * @see CarParkInfo
 */
public class RouteOverviewViewModel extends AndroidViewModel {

    private MediatorLiveData mediatorCurrentLoc = new MediatorLiveData<>();

    private LatLng previousLocation;
    private MutableLiveData<LatLng> currentLocation;

    private LocationRepository mLocationRepo;
    private Repository mRepository;

    private MutableLiveData<LatLng> startPoint;
    private MutableLiveData<LatLng> endPoint;
    private MutableLiveData<DirectionsAndCPInfo> chosenRoute;
    private MutableLiveData<GoogleMapsDirections> updatingRouteDirections;
    private boolean navigationStarted = false;

    /**
     * Default constructor which is to be used by the other constructors
     * @param application To be passed into <code>Repository</code> to instantiate <code>AppDatabase</code>
     */
    private RouteOverviewViewModel(Application application){
        super(application);
        Log.d("RouteOverviewViewModel","creating Routeoverview view model");
        mLocationRepo = LocationRepository.getLocationRepository(application.getApplicationContext());
        currentLocation = mLocationRepo.getLocation();
        previousLocation = currentLocation.getValue();
        mRepository = Repository.getInstance(application);
        chosenRoute = new MutableLiveData<>();
    }

    /**
     * Overloaded constructor which takes in a <code>CarParkInfo</code> object passed from <code>CarParkPopUpActivity</code>
     * @param application To be passed into <code>Repository</code> to instantiate <code>AppDatabase</code>
     * @param carParkInfo To be used to create a <code>DirectionsAndCPInfo</code> object
     */
    public RouteOverviewViewModel(Application application, CarParkInfo carParkInfo){
        this(application);
        Log.d("RouteOverviewViewModel", "constructor with static info");
        mRepository.updateRoutes(currentLocation.getValue(), carParkInfo.getLatLng(),
            new DirectionsCallback(){
                @Override
                public void onSuccess(GoogleMapsDirections gMapsDirections) {
                    Log.d("RouteOverviewViewModel","succeeded in creating route from static info in constructor");
                    chosenRoute.setValue(new DirectionsAndCPInfo(carParkInfo,gMapsDirections,currentLocation.getValue()));
                }

                @Override
                public void onFailure() {
                    Log.d("RouteOverviewViewModel","failed to create route from static info in constructor");
                }
            });
    }

    /**
     * Overloaded constructor which takes in a <code>DirectionsAndCPInfo</code> object passed from
     * <code>SelectRouteActivity</code>
     * @param application To be passed into <code>Repository</code> to instantiate <code>AppDatabase</code>
     * @param initialChosenRoute Used to display a route on the <code>RouteOverviewActivity</code>
     */
    public RouteOverviewViewModel(@NonNull Application application, DirectionsAndCPInfo initialChosenRoute) {
        this(application);
        Log.d("RouteOverviewViewModel", "constructor with chosen route");
        chosenRoute.setValue(initialChosenRoute);
        updatingRouteDirections = new MutableLiveData<>();
        updatingRouteDirections.setValue(chosenRoute.getValue().getGoogleMapsDirections());

    }

    /**
     * Exposes the chosenRoute <code>LiveData</code> for observation by <code>RouteOverviewActivity</code>
     * @return
     */
    public LiveData<DirectionsAndCPInfo> getChosenRoute(){
        return chosenRoute;
    }

    public LiveData<GoogleMapsDirections> getUpdatingRoute(){
        return updatingRouteDirections;
    }

    public LiveData<LatLng> getCurrentLoc(){
        return currentLocation;
    }
}