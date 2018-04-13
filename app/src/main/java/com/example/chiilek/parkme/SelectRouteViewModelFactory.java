package com.example.chiilek.parkme;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
import com.example.chiilek.parkme.test.TestEntity;
import com.example.chiilek.parkme.test.TestViewModel2;

import java.util.List;

public class SelectRouteViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private Application mApplication;
    private CarParkStaticInfo mCarParkStaticInfo;


    public SelectRouteViewModelFactory(Application application, CarParkStaticInfo staticInfo) {
        mApplication = application;
        mCarParkStaticInfo = staticInfo;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new SelectRouteViewModel(mApplication, mCarParkStaticInfo);
    }
}
