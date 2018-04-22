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
import com.example.chiilek.parkme.repository.callbacks.AvailabilityCountCallback;
import com.example.chiilek.parkme.repository.callbacks.GetRoutesCallback;
import com.example.chiilek.parkme.repository.LocationRepository;
import com.example.chiilek.parkme.repository.Repository;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This <code>ViewModel</code> is created for <code>NavigationActivity</code>. It tracks the current
 * location of the user using <code>MutableLiveData</code> from the <code>LocationRepository</code>
 * and refreshes the updatingRouteDirections <code>GoogleMapsDirections</code> object which is observed
 * by <code>NavigationActivity</code> using <code>MediatorLiveData</code>. The <code>Activity </code>
 * then will then use the new updatingRouteDirections object to plot a new PolyLine on the map
 * <p>
 * The <code>ViewModel</code> also calls the <code>Repository</code> to check the lot availability
 * of the destination car park every 5 minutes. If the number of lots available is less than
 * the threshold, it will notify the <code>Activity</code>, which will then trigger the
 * <code>ReroutePopUpActivity</code>

 * @see com.example.chiilek.parkme.ui.NavigationActivity
 * @see NavigationViewModelFactory
 * @see LocationRepository
 * @see Repository
 * @see com.example.chiilek.parkme.database.AppDatabase
 * @see MutableLiveData
 * @see MediatorLiveData
 * @see LiveData
 * @see PolylineOptions
 * @see GoogleMapsDirections
 * @see DirectionsAndCPInfo
 * @see CarParkInfo
 */
public class NavigationViewModel extends AndroidViewModel {

    private MediatorLiveData mediatorCurrentLoc = new MediatorLiveData<>();

    private LatLng previousLocation;
    private MutableLiveData<LatLng> currentLocation;

    private LocationRepository mLocationRepo;
    private Repository mRepository;

    private DirectionsAndCPInfo chosenRoute;
    private MutableLiveData<GoogleMapsDirections> updatingRouteDirections;
    private boolean navigationStarted = true;
    private DirectionsAndCPInfo alternativeRoute;
    private MutableLiveData<Integer> availabilityStatus;
    private static int carParkAvailabilityThreshold = 5;

    /**
     * Default constructor which is to be used by the other constructors
     * @param application To be passed into <code>Repository</code> to instantiate <code>AppDatabase</code>
     */
    private NavigationViewModel(Application application){
        super(application);
        Log.d("NavigationViewModel","creating navigation view model");
        mLocationRepo = LocationRepository.getLocationRepository(application.getApplicationContext());
        currentLocation = mLocationRepo.getLocation();
        previousLocation = currentLocation.getValue();
        mRepository = Repository.getInstance(application);
        availabilityStatus = new MutableLiveData<Integer>();
        availabilityStatus.setValue(-1);
    }
    /**
     * Overloaded constructor which takes in a <code>DirectionsAndCPInfo</code> from <code>RouteOverviewViewModel</code>
     * @param application To be passed into <code>Repository</code> to instantiate <code>AppDatabase</code>
     * @param initialChosenRoute To be stored in the <code>ViewModel</code>as the initial chosen route.
     *                           This is used to get the initial <code>PolylineOptions</code> object and
     *                           to track the number of available lots for the chosen car park
     */
    public NavigationViewModel(@NonNull Application application, DirectionsAndCPInfo initialChosenRoute) {
        this(application);
        Log.d("NavigationViewModel", "constructor with chosen route");
        this.chosenRoute = initialChosenRoute;
        updatingRouteDirections = new MutableLiveData<>();
        updatingRouteDirections.setValue(chosenRoute.getGoogleMapsDirections());
        initializeNavigation();

    }

    /**
     * Exposes the updatingRouteDirections <code>LiveData</code> for observation by
     * <code>NavigationActivity</code>
     * @return
     */
    public LiveData<GoogleMapsDirections> getUpdatingRoute(){
        return updatingRouteDirections;
    }

    /**
     * Uses the <code>MediatorLiveData</code> to observe the <code>MutableLiveData</code> currentLocation
     * from the <code>LocationRepository</code>. Whenever the user changes his current location, the
     * <code>ViewModel</code> will call the <code>Repository</code> to update the route
     *
     */
    private void initializeNavigation(){
        mediatorCurrentLoc.addSource(currentLocation, newCurrentLocation -> {
            Log.d("NavigationViewModel", "mediatorCurrentLoc changed");
            if(navigationStarted){
                Log.d("NavigationViewModel", "current loc changed and  navigation started");
                //if location is changed
                if (!currentLocation.getValue().equals(previousLocation)){
                    Log.d("NavigationViewModel", "current loc: " + currentLocation.getValue().toString() + " prev loc: " + previousLocation.toString());
                    Log.d("NavigationViewModel", "current loc != previous loc, calling update routes");
                    mRepository.updateRoutes((LatLng) newCurrentLocation, chosenRoute.getDestinationLatLng(),
                            new DirectionsCallback() {
                                @Override
                                public void onSuccess(GoogleMapsDirections gMapsDirections) {
                                    updatingRouteDirections.setValue(gMapsDirections);
                                    Log.d("NavigationViewModel", "navigation: updated route directions with new current loc.");
                                }

                                @Override
                                public void onFailure() {
                                    Log.d("NavigationViewModel", "navigation: update route failed");
                                }
                            });
                    previousLocation = currentLocation.getValue();
                }else Log.d("NavigationViewModel", "location not changed, do not need to update route");

            }
        });

        //timer to check availability every 5 minutes
        Timer mTimer = new Timer();
        mTimer.schedule(new TimerTask(){
            @Override
            public void run(){
                String cpNumber = chosenRoute.getCarParkInfo().getCPNumber();
                mRepository.checkAvailability(cpNumber, new AvailabilityCountCallback() {
                    @Override
                    public void onSuccess(int availabilityCount) {
                        Log.d("NavigationViewModel", "in 5 min update, avail onSuccess");
                        if (availabilityCount < carParkAvailabilityThreshold) {
                            onAvailZero();
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d("NavigationViewModel", "in 5 min update, avail onFailure");
                        // do nothing
                    }
                });
            };
            //check every 5 min (300 seconds)
        },5000,300000);
    }

    /**
     * Calls the <code>Repository</code> to get a new list of DirectionsAndCPInfo objects when
     * the current car park has less lots than the threshold
     */
    private void onAvailZero(){
        Log.d("NavigationViewModel", "onAvailZero: in avail zero");
        mRepository.getDirectionsAndCPs(currentLocation.getValue(), chosenRoute.getDestinationLatLng(), new GetRoutesCallback() {
            @Override
            public void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList) {
                String oldCarParkNumber = chosenRoute.getCarParkInfo().getCPNumber();
                if(directionsAndCPInfoList.size() == 1){
                    availabilityStatus.setValue(1);
                    return;
                } else if(directionsAndCPInfoList.get(0).getCarParkInfo().getCPNumber().equals(oldCarParkNumber)){
                    directionsAndCPInfoList.remove(0);
                }
                alternativeRoute = directionsAndCPInfoList.get(0);
                availabilityStatus.setValue(0);

            }
            @Override
            public void onFailure(int errorCode) {
                Log.d("NavigationViewModel", "on avail zero onFailure: Directions and CP API call failed.");
            }
        });
    }
    /**
     * Exposes the user's current location <code>LiveData</code> for observation by <code>NavigationActivity</code>
     * @return the <code>LiveData</code> object of current location
     */
    public LiveData<LatLng> getCurrentLoc(){ return currentLocation; }

    /**
     Exposes the <code>MediatorLiveData</code> for observation by <code>NavigationActivity</code>
     * @return the <code>MediatorLiveData</code> object
     */
    public MediatorLiveData getMediatorCurrentLoc() { return mediatorCurrentLoc; }

    /**
     * Exposes the availability status <code>LiveData</code> for observation by <code>NavigationActivity</code>
     * 0 means below threshold
     * 1 means no alternative car parks to reroute
     * -1 means nothing
     *
     * @return the <code>LiveData</code> object of the availability status of the car park lots
     */
    public MutableLiveData<Integer> getAvailabilityStatus() { return availabilityStatus; }

    public DirectionsAndCPInfo getAlternativeRoute() { return alternativeRoute; }

}