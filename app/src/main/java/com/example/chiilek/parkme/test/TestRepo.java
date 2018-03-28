package com.example.chiilek.parkme.test;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TKY on 28/3/2018.
 */

public class TestRepo {
    public LiveData<List<String>> foo(String newTerm){
        MutableLiveData<List<String>> data = new MutableLiveData<>();
        List<String> newlist = new ArrayList<String>();
        newlist.add(newTerm);
        newlist.add("hello");
        data.postValue(newlist);
        return data;
    }
}
