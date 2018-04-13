package com.example.chiilek.parkme.test;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import java.util.List;

public class TestViewModel2Factory extends ViewModelProvider.NewInstanceFactory {
    private Application mApplication;
    private List<TestEntity> mTestEntityList;


    public TestViewModel2Factory(Application application, List<TestEntity> TestEntityList) {
        mApplication = application;
        mTestEntityList = TestEntityList;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new TestViewModel2(mApplication, mTestEntityList);
    }
}
