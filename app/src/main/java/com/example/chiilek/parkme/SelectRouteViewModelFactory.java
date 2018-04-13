package com.example.chiilek.parkme;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
import com.example.chiilek.parkme.test.TestEntity;
import com.example.chiilek.parkme.test.TestViewModel2;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import javax.security.auth.Destroyable;

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
