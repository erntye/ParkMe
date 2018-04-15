package com.example.chiilek.parkme.Suggestion;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.chiilek.parkme.MultiSearchFragment;
import com.example.chiilek.parkme.R;
import com.example.chiilek.parkme.SelectRouteViewModel;
import com.example.chiilek.parkme.SelectRouteViewModelFactory;
import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
import com.example.chiilek.parkme.data_classes.DirectionsAndCPInfo;
import com.example.chiilek.parkme.test.TestEntity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class SuggestionsActivity extends AppCompatActivity{

    private RecyclerView mRecyclerView;
    private SuggestionAdapter mAdapter;
    private List<DirectionsAndCPInfo> mCarparkList = new ArrayList<DirectionsAndCPInfo>();
    private SelectRouteViewModel model;
    //TODO refer to the multisearch fragment to get the startpoint and destination search terms
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_suggestions);
        // MultiSearchFragment searchFragment = (MultiSearchFragment) getFragmentManager().findFragmentById(R.id.multi_search_fragment);


        PlaceAutocompleteFragment autocompleteFragmentSource = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_source);
        PlaceAutocompleteFragment autocompleteFragmentDsetination = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_destination);





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
        mAdapter = new SuggestionAdapter(mCarparkList, this);
        mRecyclerView.setAdapter(mAdapter);

        //code to observe viewmodel
        //model = ViewModelProviders.of(this).get(SelectRouteViewModel.class);
      //TODO to replace above when intent from popup works
        Intent parentIntent = getIntent();
        LatLng destination = parentIntent.getExtras().getParcelable("Destination");
        String destinationName= parentIntent.getExtras().getParcelable("Name");
        model = ViewModelProviders
                .of(this, new SelectRouteViewModelFactory(this.getApplication(),destination))
                .get(SelectRouteViewModel.class);

//        searchFragment.setTexts(destinationName);

        model.getMediatorCurrentLoc().observe(this, newData->
                Log.d("SuggestionsActivity", "observing mediator current location")
        );
        model.getMediatorDirAndCPList().observe(this,newData->
                Log.d("SuggestionsActivity", "observing mediator dir and CP list")
        );
        model.getDirectionsAndCarParks().observe(this, newRoutes ->
                {
                    Log.d("SuggestionsActivity", "observer activated directionsandcarparklist changed");
                    mAdapter.addItems(newRoutes);

                    // for testing, remove soon
                    if (!model.getNavigationStarted()) {
                        model.setEndPoint(mAdapter.getCarParkInfo(0).getDestinationLatLng());
                        model.setChosenRoute(mAdapter.getCarParkInfo(0));
                        model.setNavigationStarted();
                    }
                }
        );

        //Puts text in the search bars
        autocompleteFragmentSource.setText("Current Location");
        autocompleteFragmentDsetination.setText(parentIntent.getExtras().getString("Name"));

    }

}

