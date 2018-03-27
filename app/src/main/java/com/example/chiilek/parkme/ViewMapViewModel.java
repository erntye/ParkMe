package com.example.chiilek.parkme;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.example.chiilek.parkme.data_classes.CarParkDatum;
import com.example.chiilek.parkme.repository.Repository;

import java.util.List;

public class ViewMapViewModel extends ViewModel{
    private MutableLiveData<Location> msearchTerm;
    //list of nearest carparks to search term
    private LiveData<List< CarParkDatum>> mcarParkList;
    private Repository mRepository;

    public ViewMapViewModel(){
        msearchTerm = new MutableLiveData<>();
        //searches nearby everytime msearchterm changes, when called by VMMP.searchNearby()
        mcarParkList = Transformations.switchMap(msearchTerm, (Location newDestination)->
                mRepository.searchNearby(newDestination));
    }
    //called by button in ViewMapActivity and triggers transformation
    public void searchNearby(Location searchTerm){
        msearchTerm.setValue(searchTerm);
    }

    /*
        // put this in the ViewMapActivity GMAP fragment with the search button to update searchTerm

        model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        itemSelector.setOnClickListener(item -> {
        model.searchNearby(item);
        //create new SelectRouteActivity
        }
     */

    //displays popup carpark info
    //TODO find out what google maps returns when you press on a pin
    public CarParkDatum getCarParkInfo(String toFind){
        if (mcarParkList != null){
            for ( CarParkDatum carpark: mcarParkList.getValue()){
                if (carpark.getCarParkNumber().equals(toFind))
                    return carpark;
            }
        }
        return null;
    }

    public LiveData<Location> getSearchTerm() {
        return msearchTerm;
    }

    public LiveData<List<CarParkDatum>> getCarParkList() {
        return mcarParkList;
    }

}

