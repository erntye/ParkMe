package com.example.chiilek.parkme.viewmodel;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.chiilek.parkme.viewmodel.SelectRouteViewModel;
import com.google.android.gms.maps.model.LatLng;
/**
 * This class is required by Android in order to pass in arguments to the <code>SelectRouteViewModel</code>
 * constructor. This particular Factory takes in a <code>LatLng</code> object.
 *
 * @see RouteOverviewViewModel
 * @see LatLng
 */
public class SelectRouteViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private Application mApplication;
    private LatLng mDestination;


    public SelectRouteViewModelFactory(Application application, LatLng destination) {
        mApplication = application;
        mDestination = destination;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new SelectRouteViewModel(mApplication,mDestination);
    }
}
