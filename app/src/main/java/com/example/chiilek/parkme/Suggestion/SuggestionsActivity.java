package com.example.chiilek.parkme.Suggestion;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.chiilek.parkme.MultiSearchFragment;
import com.example.chiilek.parkme.R;
import com.example.chiilek.parkme.SelectRouteViewModel;
import com.example.chiilek.parkme.SelectRouteViewModelFactory;
import com.example.chiilek.parkme.ViewMap.ViewMapActivity;
import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
import com.example.chiilek.parkme.data_classes.DirectionsAndCPInfo;
import com.example.chiilek.parkme.navigation.RouteOverviewActivity;
import com.example.chiilek.parkme.test.TestEntity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
        PlaceAutocompleteFragment autocompleteFragmentDestination = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_destination);


        autocompleteFragmentSource.setBoundsBias(new LatLngBounds(new LatLng(1.227925, 103.604971), new LatLng(1.456672, 104.003780)));
        autocompleteFragmentDestination.setBoundsBias(new LatLngBounds(new LatLng(1.227925, 103.604971), new LatLng(1.456672, 104.003780)));


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
        String destinationName = parentIntent.getExtras().getParcelable("Name");
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
                }
        );

        //Puts text in the search bars
        ((EditText)autocompleteFragmentSource.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(18.5f);
        ((EditText)autocompleteFragmentDestination.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(18.5f);

        ((EditText)autocompleteFragmentSource.getView().findViewById(R.id.place_autocomplete_search_input)).setTextColor(getResources().getColor(R.color.colorMain));
        ((EditText)autocompleteFragmentDestination.getView().findViewById(R.id.place_autocomplete_search_input)).setTextColor(getResources().getColor(R.color.colorMain));

        (autocompleteFragmentSource.getView().findViewById(R.id.place_autocomplete_search_input)).setPadding(0,0,0,4);
        (autocompleteFragmentDestination.getView().findViewById(R.id.place_autocomplete_search_input)).setPadding(0,0,0,4);


        autocompleteFragmentSource.setText("Current Location");
        autocompleteFragmentDestination.setText(parentIntent.getExtras().getString("Name"));

        autocompleteFragmentDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Intent intent = new Intent(SuggestionsActivity.this, ViewMapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("Place", (Parcelable) place);
                intent.putExtras(bundle);
                Log.d("SuggestionsActivityChange","Changing chosen destination on ViewMapActivity");
                startActivity(intent);
            }
            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });
    }
}

