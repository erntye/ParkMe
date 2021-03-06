package com.example.chiilek.parkme.viewmodel;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.chiilek.parkme.entity.DirectionsAndCPInfo;
/**
 * This class is required by Android in order to pass in arguments to the <code>NavigationViewModel</code>
 * constructor. This particular Factory takes in a <code>DirectionsAndCPInfo</code> object.
 *
 * @see RouteOverviewViewModel
 * @see DirectionsAndCPInfo
 */
public class NavigationViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private Application mApplication;
    private DirectionsAndCPInfo mInitialRoute;


    public NavigationViewModelFactory(Application application, DirectionsAndCPInfo initialRoute) {
        mApplication = application;
        mInitialRoute = initialRoute;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new NavigationViewModel(mApplication, mInitialRoute);
    }
}
