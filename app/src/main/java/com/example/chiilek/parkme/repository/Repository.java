package com.example.chiilek.parkme.repository;


import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.example.chiilek.parkme.api.availability_api.AvailabilityAPIController;
import com.example.chiilek.parkme.api.directions_api.DirectionsAPIController;
import com.example.chiilek.parkme.api.directions_api.DirectionsCallback;
import com.example.chiilek.parkme.entity.CarParkInfo;
import com.example.chiilek.parkme.entity.DirectionsAndCPInfo;
import com.example.chiilek.parkme.entity.availabilityapi.CarParkDatum;
import com.example.chiilek.parkme.entity.availabilityapi.Item;
import com.example.chiilek.parkme.entity.directionsapi.GoogleMapsDirections;
import com.example.chiilek.parkme.database.AppDatabase;
import com.example.chiilek.parkme.repository.callbacks.GetRoutesCallback;
import com.example.chiilek.parkme.repository.callbacks.SearchNearbyCallback;
import com.example.chiilek.parkme.repository.callbacks.AvailabilityCountCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Repository {

    private AppDatabase appDatabase;
    private MutableLiveData<List<CarParkInfo>> viewMapCPList = new MutableLiveData<List<CarParkInfo>>();
    //singleton pattern
    private static Repository INSTANCE;

    public Repository(Context context){
        this.appDatabase = AppDatabase.getInstance(context);
    }


    /**
     * Singleton pattern implementation to get the <code>Repository</code> object. Creates the instance if it has not been done before.
     * @param context context of the class that calls for the <code>Repository</code> Instance.
     * @return Singleton instance of the <code>Repository</code>
     */
    public static Repository getInstance(Context context){
        if (INSTANCE == null)
            INSTANCE = new Repository(context);
        Log.d("Repository", "In Singleton Pattern getInstance");
        return INSTANCE;
    }


    /**
     *
     * Called to generate directions to all nearby car parks given a destination.
     * With the destination, the function checks the database for nearby car parks. With this list of nearby car parks,
     * the function generates the directions to navigate each car park.
     * Next, it makes calls to the Availability API, and wraps all these information in <code>DirectionAndCPInfo</code> objects.
     * The asynchronous function uses the callback function to return the <code>List</code> of <code>DirectionsAndCPInfo</code> objects.
     * @param startPoint <code>LatLng</code> object of the User's chosen start point
     * @param destination <code>LatLng</code> object of the User's selected destination
     */
    public void getDirectionsAndCPs(LatLng startPoint, LatLng destination, GetRoutesCallback routesCallback) {
        Log.d("Repository", "Called getDirectionsAndCPs(startPoint: " + startPoint.toString() + ", destination: " + destination.toString() + ")");

        //gets list of all nearby car parks (within a certain range)
        List<CarParkInfo> closestCarParks = appDatabase.CPInfoDao()
                .getNearestCarParks(destination.latitude, destination.longitude);

        if (closestCarParks.size() == 0 ){
            Log.d("Repository", "In getDirectionsAndCPs: There are no closest car parks found.\n*******") ;
            routesCallback.onFailure(1);
        }
        else
            Log.d("Repository", "In getDirectionsAndCPs: closestCarParks size: " + closestCarParks.size()) ;

        //generates directions to each car park, stores in DirectionsAndCPInfo class
        List<DirectionsAndCPInfo> directionsAndCPList = new ArrayList<DirectionsAndCPInfo>();
        AvailabilityAPIController availAPIControl = new AvailabilityAPIController();
        AtomicInteger counter = new AtomicInteger(closestCarParks.size());
        for (CarParkInfo carPark : closestCarParks) {
            DirectionsAPIController.getInstance().callDirectionsAPI(startPoint,
                    new LatLng(Double.parseDouble(carPark.getLatitude()), Double.parseDouble(carPark.getLongitude())),
                    new DirectionsCallback() {
                        public void onSuccess(GoogleMapsDirections googleMapsDirections) {

                            switch(googleMapsDirections.getStatus()){
                                case "NOT_FOUND":
                                case "ZERO_RESULTS":
                                    Log.d("Repository", "getDirectionsAndCPs - Directions onSuccess: not found or zero results; Status code: "+ googleMapsDirections.getStatus());
                                    return;
                            }

                            Log.d("Repository", "onSuccess in DirectionsCallback from CP in closestCarParks");
                            Log.d("Repository","DIRECTIONS ROUTE: " + googleMapsDirections.getRoutes().get(0).getLegs().get(0).getSteps().toString());

                            DirectionsAndCPInfo element = new DirectionsAndCPInfo(carPark, googleMapsDirections, destination);
                            directionsAndCPList.add(element);
                            int i = counter.decrementAndGet();
                            if(i==0){
                                Log.d("Repository", "In getDirectionsAndCPs: atomic counter is 0; GMaps API calls are done");
                                //calling Availability API with callback function.
                                availAPIControl.makeCall(new com.example.chiilek.parkme.api.availability_api.AvailabilityCallback() {
                                    @Override
                                    public void onSuccess(Item cpAPIItem) {
                                        if(cpAPIItem.getCarParkData().size() == 0){
                                            Log.d("Repository", "In availability callback success: Size of cpData list in Item object is 0.");
                                            for(DirectionsAndCPInfo dirAndCP : directionsAndCPList){
                                                //stores CarParkDatum object into each DirectionsAndCP object
                                                dirAndCP.setCarParkDatum(new CarParkDatum());
                                            }
                                            Log.d("Repository", "In availability callback success. Created default CarParkDatum objects and stored in D&CPInfo objects.");
                                        }else {
                                            for (DirectionsAndCPInfo dirAndCP : directionsAndCPList) {
                                                //stores CarParkDatum object into each DirectionsAndCP object
                                                CarParkDatum datumToSet = cpAPIItem.getCarParkDatum(dirAndCP.getCarParkInfo().getCPNumber());
                                                if(datumToSet == null){
                                                    Log.d("Repository", "in availability callback onSuccess: car park number not found. setting default object.");
                                                    dirAndCP.setCarParkDatum(new CarParkDatum());
                                                }else
                                                    dirAndCP.setCarParkDatum(datumToSet);
                                            }
                                            Log.d("Repository", "In availability callback success: CarParkDatum has been saved for all car parks in list.");
                                            Log.d("Repository", "Calling onSuccess from ViewModel's GetRoutesCallback");
                                            routesCallback.onSuccess(scoreAndSort(directionsAndCPList));
                                        }
                                    }

                                    @Override
                                    public void onFailure() {
                                        Log.e("Repository", "Availability Callback onFailure");
                                        Log.d("Repository", "Calling onSuccess from ViewModel's GetRoutesCallback");
                                        for(DirectionsAndCPInfo dirAndCP : directionsAndCPList){
                                            //stores CarParkDatum object into each DirectionsAndCP object
                                            dirAndCP.setCarParkDatum(new CarParkDatum());
                                        }
                                        Log.d("Repository", "In availability callback failure. Created default CarParkDatum objects and stored in D&CPInfo objects.");
                                        routesCallback.onSuccess(directionsAndCPList);
                                    }
                                });
                            }

                        }

                        public void onFailure() {
                            Log.e("Repository", "Directions Callback onFailure.");
                            int i = counter.decrementAndGet();
                            if (i == 0) {
                                Log.d("Repository", "In directions callback failure: atomic counter is 0");
                                if(directionsAndCPList.size() == 0){
                                    Log.d("Repository", "In directions callback failure, could not find routes for any car park.");
                                    routesCallback.onFailure(2);
                                }else {
                                    Log.d("Repository", "In directions callback failure, calling onSuccess from ViewModel's GetRoutesCallback");
                                    routesCallback.onSuccess(directionsAndCPList);
                                }
                            }
                        }
                    });
        }
    }

    /**
     * Sorts the <code>DirectionsAndCPInfo</code> list according to an overall score.
     * Score is assigned to each entry on the list for their availability, trip duration, and distance.
     * Overall score is then assigned based on weights on each attribute listed above
     * @param directionsAndCPList <code>List</code> of <code>DirectionsAndCPInfo</code> to be scored and sorted
     * @return  sorted <code>List</code> of <code>DirectionsAndCPInfo</code>.
     */
    private List<DirectionsAndCPInfo> scoreAndSort(List<DirectionsAndCPInfo> directionsAndCPList){
        int size = directionsAndCPList.size();
        //sorts by distance and scores each element
        directionsAndCPList.sort(Comparator.comparingDouble(DirectionsAndCPInfo::getDistance));
        int score = size;
        for(DirectionsAndCPInfo element : directionsAndCPList){
            element.setDistanceScore(score);
            score--;
        }

        //sorts and scores by duration
        directionsAndCPList.sort(Comparator.comparingDouble(DirectionsAndCPInfo::getDuration));
        score = size;
        for(DirectionsAndCPInfo element : directionsAndCPList){
            element.setDurationScore(score);
            score--;
        }

        //sorts and scores by availability
        directionsAndCPList.sort(Comparator.comparingDouble(DirectionsAndCPInfo::getAvailability));
        score = size;
        for(DirectionsAndCPInfo element : directionsAndCPList){
            element.setAvailabilityScore(score);
            score--;
        }

        //sorts by overall score
        directionsAndCPList.sort(Comparator.comparingDouble(DirectionsAndCPInfo::getOverallScore));

        return directionsAndCPList;
    }

    /**
     * Returns a <code>MutableLiveData<List></code> of <code>CarParkInfo</code> of the nearby car parks around a given search term.
     * Asynchronous call to Availability API will run in a background thread, and will update the <code>LiveData</code> when
     * the API call returns, and will add the availability information to the <code>CarParkInfo</code> objects
     * Before the return of the API call, the availability information will be unavailable.
     * If the API call fails, the values of the availability information in the CarParkInfo objects will be defaulted to indicate unavailability.
     * @param searchTerm User's defined search term, where User wants to find car parks in the vicinity.
     * @return <code>MutableLiveData<List></code> of <code>CarParkInfo</code> objects, corresponding to the car park objects around the vicinity of the search term
     */
    public MutableLiveData<List<CarParkInfo>> searchNearbyCarParks(LatLng searchTerm, SearchNearbyCallback searchNearbyCallback){
        Log.d("Repository", "Called setSearchTerm(" + searchTerm + ")");
        //call database getClosest10()
        List<CarParkInfo> closestCarParks = appDatabase.CPInfoDao()
                .getNearestCarParks(searchTerm.latitude, searchTerm.longitude);

        MutableLiveData<List<CarParkInfo>> liveData = new MutableLiveData<>();

        AvailabilityAPIController availAPIControl = new AvailabilityAPIController();
        availAPIControl.makeCall(new com.example.chiilek.parkme.api.availability_api.AvailabilityCallback() {
            @Override
            public void onSuccess(Item cpAPIItem) {
                if(cpAPIItem.getCarParkData().size() == 0){
                    Log.d("Repository", "searchNearbyCarParks() - In availability callback success: Size of cpData list in Item object is 0.");
                    Log.d("Repository", "searchNearbyCarParks() - Availability information will be null.");
                    for(CarParkInfo carPark : closestCarParks){
                        //stores CarParkDatum object into each DirectionsAndCP object
                        carPark.setAvailInfo(new CarParkDatum());
                    }
                }else {
                    for(CarParkInfo carPark : closestCarParks){
                        CarParkDatum toSetDatum = cpAPIItem.getCarParkDatum(carPark.getCPNumber());
                        if(toSetDatum == null){
                            Log.d("Repository", "searchNearbyCarParks() - availability onSuccess: could not find cp number. creating default object.");
                            carPark.setAvailInfo(new CarParkDatum());
                        } else
                            carPark.setAvailInfo(toSetDatum);
                    }
                    Log.d("Repository", "searchNearbyCarParks() - In availability callback success: CarParkDatum has been saved for all car parks in static info list.");
                    Log.d("Repository", "searchNearbyCarParks() - Calling onSuccess from ViewModel's GetRoutesCallback");
                    viewMapCPList.postValue(closestCarParks);
                    searchNearbyCallback.onSuccess();
                }
            }

            @Override
            public void onFailure() {
                Log.e("Repository", "searchNearbyCarParks() - Availability Callback onFailure");
                Log.d("Repository", "searchNearbyCarParks() - Availability information will be null.");
                for(CarParkInfo carPark : closestCarParks){
                    carPark.setAvailInfo(new CarParkDatum());
                }
            }
        });
        viewMapCPList.setValue(closestCarParks);
        liveData.setValue(viewMapCPList.getValue());
        return liveData;
    }

    /**
     * Used to update the route taken by the User during navigation.
     * Makes an asynchronous call to Directions API to generate routes according to the User's new current location.
     * Method will update the return the <code>GoogleMapsDirections</code> object through the <code>DirectionsCallback</code> which it takes in as an argument.
     * @param startPoint <code>LatLng</code> of new current location of the User, which is updated as the User drives along the roads.
     * @param destination <code>LatLng</code> of Car Park that the User is currently navigating towards.
     * @param directionCallback Callback function for the async API call to return the <code>GoogleMapsDirections</code> object through.
     */
    public void updateRoutes(LatLng startPoint, LatLng destination, DirectionsCallback directionCallback){
        DirectionsAPIController.getInstance().callDirectionsAPI(startPoint, destination, new DirectionsCallback() {
            @Override
            public void onSuccess(GoogleMapsDirections gMapsDirections) {
                Log.d("Repository", "updateRoutes callback success");
                Log.d("Repository","DIRECTIONS ROUTE: " + gMapsDirections.getRoutes().get(0).getLegs().get(0).getSteps().toString());
                directionCallback.onSuccess(gMapsDirections);
            }

            @Override
            public void onFailure() {
                Log.d("Repository", "updateRoutes callback failure");
            }
        });
    }

    /**
     * Checks the car park availability in a chosen car park. Used during the Update Use Case.
     * Makes an asynchronous call to the <code>AvailabilityAPI</code>, checks the availability in the chosen car park,
     * and returns the number of available lots as an <code>int</code> through the <code>AvailabilityCountCallback</code>.
     * @param cpNumber String representing the car park number to check availability for
     * @param availCallback Callback to return the availability through.
     */
    public void checkAvailability(String cpNumber, AvailabilityCountCallback availCallback){
        AvailabilityAPIController availAPIControl = new AvailabilityAPIController();

        availAPIControl.makeCall(new com.example.chiilek.parkme.api.availability_api.AvailabilityCallback() {
            @Override
            public void onSuccess(Item cpAPIItem) {
                if(cpAPIItem.getCarParkData().size() == 0){
                    Log.d("Repository", "checkAvailability onSuccess: size of avail api return is 0");
                    availCallback.onFailure();
                }else {
                    Log.d("Repository", "checkAvailability onSuccess");
                    availCallback.onSuccess(cpAPIItem.getCarParkDatum(cpNumber).getCarParkInfo().get(0).getLotsAvailable());
                }
            }

            @Override
            public void onFailure() {
                Log.d("Repository", "checkAvailability onFailure: Availability API call failed");
                availCallback.onFailure();
            }
        });
    }

    /**
     * Exposes the <code>MutableLiveData<List<CarParkInfo>></code> for observation
     * @return
     */
    public MutableLiveData<List<CarParkInfo>> getViewMapCPList(){
        return viewMapCPList;
    }

}