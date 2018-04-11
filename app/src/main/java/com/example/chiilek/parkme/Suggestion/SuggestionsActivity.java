package com.example.chiilek.parkme.Suggestion;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.chiilek.parkme.R;
import com.example.chiilek.parkme.SelectRouteViewModel;
import com.example.chiilek.parkme.ViewMap.ViewMapViewModel;
import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
import com.example.chiilek.parkme.data_classes.DirectionsAndCPInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class SuggestionsActivity extends AppCompatActivity{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<DirectionsAndCPInfo> test = new ArrayList<DirectionsAndCPInfo>();
    private SelectRouteViewModel model;
    private List<DirectionsAndCPInfo> mCarparkList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        model = ViewModelProviders.of(this).get(SelectRouteViewModel.class);

        if (mCarparkList != null)
            Log.d("SuggestionsActivity","not null");
        else
            Log.d("SuggestionsActivity","null");
        model.getDirectionsAndCarParks().observe(this, newDirections ->
            {
                mCarparkList = model.getDirectionsAndCarParks().getValue();
                for (int i = 0; i < mCarparkList.size();i++){
                    Log.d("SuggestionsActivity","i is " + i);
                    test.add(mCarparkList.get(i));
                    mAdapter.notifyDataSetChanged();
                }
            });
        if (mCarparkList != null)
            Log.d("SelectRouteVM","Start point is "+ mCarparkList.size());
        //TODO LOAD REAL DATA BELOW HERE
        //--------------

        //---------------
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

