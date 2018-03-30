package com.example.chiilek.parkme.test;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by TKY on 28/3/2018.
 */

public class TestRepo {
    private final TestDatabase testDatabase;

    //implement singleton structure
    private static TestRepo INSTANCE;

    public static TestRepo getInstance(Context context){
        if (INSTANCE == null)
            INSTANCE = new TestRepo(context);
        return INSTANCE;
    }
    //pri
    private TestRepo(Context context){
        this.testDatabase = TestDatabase.getDatabase(context);
    }

    public LiveData<List<TestEntity>> getAllEntity(){
        return testDatabase.userDAO().getAllModel();
    }

    public LiveData<List<TestEntity>> getEntityById(int id){
        return testDatabase.userDAO().getModelById(id);
    }

    public void initialize(){
        TestEntity entity1 = new TestEntity(1,"1");
        TestEntity entity2 = new TestEntity(2,"2");
        TestEntity entity3 = new TestEntity(3,"3");
        testDatabase.userDAO().save(entity1);
        testDatabase.userDAO().save(entity2);
        testDatabase.userDAO().save(entity3);
    }
}
