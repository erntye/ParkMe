package com.example.chiilek.parkme.viewmodel;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.chiilek.parkme.entity.DirectionsAndCPInfo;

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
