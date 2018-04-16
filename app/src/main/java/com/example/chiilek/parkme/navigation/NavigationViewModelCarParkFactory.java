package com.example.chiilek.parkme.navigation;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
import com.example.chiilek.parkme.data_classes.DirectionsAndCPInfo;

public class NavigationViewModelCarParkFactory extends ViewModelProvider.NewInstanceFactory {
    private Application mApplication;
    private CarParkStaticInfo mInitialCarPark;


    public NavigationViewModelCarParkFactory(Application application, CarParkStaticInfo initialCarPark) {
        mApplication = application;
        mInitialCarPark = initialCarPark;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new NavigationViewModel(mApplication, mInitialCarPark);
    }
}
