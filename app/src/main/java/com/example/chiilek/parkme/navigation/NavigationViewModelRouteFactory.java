package com.example.chiilek.parkme.navigation;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
import com.example.chiilek.parkme.data_classes.DirectionsAndCPInfo;
import com.example.chiilek.parkme.navigation.NavigationViewModel;

public class NavigationViewModelRouteFactory extends ViewModelProvider.NewInstanceFactory {
    private Application mApplication;
    private DirectionsAndCPInfo mInitialChosenRoute;


    public NavigationViewModelRouteFactory(Application application, DirectionsAndCPInfo initialChosenRoute) {
        mApplication = application;
        mInitialChosenRoute = initialChosenRoute;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new NavigationViewModel(mApplication, mInitialChosenRoute);
    }
}
