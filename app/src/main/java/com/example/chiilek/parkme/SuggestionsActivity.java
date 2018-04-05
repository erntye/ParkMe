package com.example.chiilek.parkme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;

import java.util.ArrayList;
import java.util.List;


public class SuggestionsActivity extends AppCompatActivity{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<CarParkStaticInfo> test = new ArrayList<CarParkStaticInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //creating dummy data
        for (int i = 0; i<10;i++){
            CarParkStaticInfo CP = new CarParkStaticInfo("abow"+i,"wohoo"+i+4);
            test.add(CP);
        }
        setContentView(R.layout.activity_suggestions);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //get animations
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //divide with a line between each suggestion
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // specify an adapter
        mAdapter = new SuggestionAdapter(test, this);
        mRecyclerView.setAdapter(mAdapter);

    }

    }

