package com.example.chiilek.parkme.viewmodel;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.chiilek.parkme.entity.DirectionsAndCPInfo;
/**
 * This class is required by Android in order to pass in arguments to the <code>RouteOverviewViewModel</code>
 * constructor. This particular Factory takes in a <code>DirectionsAndCPInfo</code> object.
 *
 * @see RouteOverviewViewModel
 * @see DirectionsAndCPInfo
 */
public class RouteOverviewViewModelRouteFactory extends ViewModelProvider.NewInstanceFactory {
    private Application mApplication;
    private DirectionsAndCPInfo mInitialChosenRoute;


    public RouteOverviewViewModelRouteFactory(Application application, DirectionsAndCPInfo initialChosenRoute) {
        mApplication = application;
        mInitialChosenRoute = initialChosenRoute;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new RouteOverviewViewModel(mApplication, mInitialChosenRoute);
    }
}
