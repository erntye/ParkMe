package com.example.chiilek.parkme.test;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import junit.framework.Test;

import java.util.List;

public class TestViewModel2 extends AndroidViewModel {
    private static List<TestEntity> mTestEntityList;

    public TestViewModel2(Application application, List<TestEntity> TestEntityList){
        super(application);
        mTestEntityList = TestEntityList;
    }

    public List<TestEntity>getList(){
        return mTestEntityList;
    }



}
