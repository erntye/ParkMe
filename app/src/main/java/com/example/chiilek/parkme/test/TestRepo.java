package com.example.chiilek.parkme.test;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ernest on 28/3/2018.
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
        Log.d("TestRepo","Called getAllEntity()");
        return testDatabase.getModelDAO().getAllModel();
    }

    public LiveData<List<TestEntity>> getEntityById(int id){
        Log.d("Repo","Called getEntityById(" + id+ ")");
        return testDatabase.getModelDAO().getModelById(id);
    }
    public void deleteEntityById(int id){
        Log.d("Repo","Called getEntityById(" + id+ ")");
        testDatabase.getModelDAO().delete(id);
    }

    public void initialize(){
        Log.d("repo","Initialize Entered");
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                TestEntity entity1 = new TestEntity(1,"name1");
                TestEntity entity2 = new TestEntity(2,"name2");
                TestEntity entity3 = new TestEntity(3,"name3");
                testDatabase.getModelDAO().save(entity1);
                testDatabase.getModelDAO().save(entity2);
                testDatabase.getModelDAO().save(entity3);
                Log.d("repo","Initialize Saved");
                return null;
            }
        }.execute();
    }

    public void testMediatorFunc(){
        Log.d("repo","Testing Mediator function");
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                TestEntity entity4 = new TestEntity(4,"testing4");
                testDatabase.getModelDAO().save(entity4);
                Log.d("repo","Initialize Saved");
                return null;
            }
        }.execute();
    }
}
