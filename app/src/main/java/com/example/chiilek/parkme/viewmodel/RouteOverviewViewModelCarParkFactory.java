package com.example.chiilek.parkme.viewmodel;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.chiilek.parkme.entity.CarParkInfo;

public class RouteOverviewViewModelCarParkFactory extends ViewModelProvider.NewInstanceFactory {
    private Application mApplication;
    private CarParkInfo mInitialCarPark;


    public RouteOverviewViewModelCarParkFactory(Application application, CarParkInfo initialCarPark) {
        mApplication = application;
        mInitialCarPark = initialCarPark;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new RouteOverviewViewModel(mApplication, mInitialCarPark);
    }
}
