package com.example.chiilek.parkme.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.Toast;

import com.example.chiilek.parkme.R;
import com.example.chiilek.parkme.viewmodel.RouteOverviewViewModel;
import com.example.chiilek.parkme.viewmodel.RouteOverviewViewModelCarParkFactory;
import com.example.chiilek.parkme.viewmodel.RouteOverviewViewModelRouteFactory;
import com.example.chiilek.parkme.entity.CarParkInfo;
import com.example.chiilek.parkme.entity.DirectionsAndCPInfo;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

/**
 * This <code>Activity</code> is responsible for displaying an overview of the selected route before beginning
 * navigation. This screen can be reached from the <code>SelectRouteActivity</code> or directly from
 * <code>CarParkPopUpActivity</code>.
 * <p>
 * At this screen, users can still have the option to change their intended destination, which will take them back
 * to the map screen again.
 * @see CarParkInfo
 * @see DirectionsAndCPInfo
 * @see NavigationActivity
 * @see SelectRouteActivity
 * @see SelectRouteAdapter
 * @see CarParkPopUpActivity
 * @see RouteOverviewViewModel
 * @see RouteOverviewViewModelCarParkFactory
 * @see RouteOverviewViewModelRouteFactory
 */
public class RouteOverviewActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private RouteOverviewViewModel model;
    //needed to bind to service to get location updates
    private final int REQUEST_PERMISSION_LOCATION = 1;
    private List<LatLng> sampleWayPoints;
    private DirectionsAndCPInfo mChosenRoute;
    private CarParkInfo mChosenCarPark;

    //ask permission to turn on GPS
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    /**
     * This method is responsible for initialising the <code>RouteOverviewViewModel</code> upon creation. It also
     * initialises the relevant <code>PlaceAutocompleteFragment</code> to show the start and end locations of the
     * current chosen navigation.
     * @param savedInstanceState a <code>Bundle</code> object containing the saved instance state of
     *                           the previous activity. Expected to include either a <code>DirectionsAndCPInfo</code>
     *                           object containing important information such as the route to the destination, or a
     *                           <code>CarParkInfo</code> object.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_overview);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent parentIntent = getIntent();
        if (parentIntent.getSerializableExtra("chosenRoute") != null) {
            mChosenRoute = (DirectionsAndCPInfo) parentIntent.getSerializableExtra("chosenRoute");
            Log.d("NavigationActivity", "InitialChosenRoute passed from intent is  " + mChosenRoute.getCarParkInfo().getCPNumber());
            model = ViewModelProviders
                    .of(this, new RouteOverviewViewModelRouteFactory(this.getApplication(), mChosenRoute))
                    .get(RouteOverviewViewModel.class);
        } else {
            mChosenCarPark = (CarParkInfo) parentIntent.getSerializableExtra("chosenCarPark");
            Log.d("NavigationActivity", "ChosenCarPark passed from intent is  " + mChosenCarPark.getCPNumber());
            model = ViewModelProviders
                    .of(this, new RouteOverviewViewModelCarParkFactory(this.getApplication(), mChosenCarPark))
                    .get(RouteOverviewViewModel.class);
        }
//        Bundle extras = getIntent().getExtras();
//        LatLng startPoint = new LatLng(extras.getDouble("startPointLat"), extras.getDouble("startPointLong"));
//        LatLng endPoint = new LatLng(extras.getDouble("endPointLat"), extras.getDouble("endPointLong"));
        ImageView startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getChosenRoute().getValue() == null){
                    Toast.makeText(RouteOverviewActivity.this, "No Route Found", Toast.LENGTH_LONG).show();
                }else {
                    Log.d("RouteOverviewActivity", "Start Button pressed");
                    Intent intent = new Intent(RouteOverviewActivity.this, NavigationActivity.class);
                    intent.putExtra("chosenRoute", model.getChosenRoute().getValue());
                    Log.d("RouteOverviewActivity", "starting intent for Navigation Activity");
                    startActivity(intent);
                }
            }
        });

        PlaceAutocompleteFragment autocompleteFragmentSource = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_source);
        PlaceAutocompleteFragment autocompleteFragmentDestination = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_destination);

        autocompleteFragmentSource.setBoundsBias(new LatLngBounds(new LatLng(1.227925, 103.604971), new LatLng(1.456672, 104.003780)));
        autocompleteFragmentDestination.setBoundsBias(new LatLngBounds(new LatLng(1.227925, 103.604971), new LatLng(1.456672, 104.003780)));

        //Puts text in the search bars
        ((EditText)autocompleteFragmentSource.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(18.5f);
        ((EditText)autocompleteFragmentDestination.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(18.5f);

        ((EditText)autocompleteFragmentSource.getView().findViewById(R.id.place_autocomplete_search_input)).setTextColor(getResources().getColor(R.color.colorMain));
        ((EditText)autocompleteFragmentDestination.getView().findViewById(R.id.place_autocomplete_search_input)).setTextColor(getResources().getColor(R.color.colorMain));

        (autocompleteFragmentSource.getView().findViewById(R.id.place_autocomplete_search_input)).setPadding(0,0,0,4);
        (autocompleteFragmentDestination.getView().findViewById(R.id.place_autocomplete_search_input)).setPadding(0,0,0,4);

        autocompleteFragmentSource.setText("Current Location");
        autocompleteFragmentDestination.setText(getIntent().getExtras().getString("destinationAddress"));

        autocompleteFragmentDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Intent intent = new Intent(RouteOverviewActivity.this, ViewMapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("Place", (Parcelable) place);
                intent.putExtras(bundle);
                Log.d("RouteOverviewActivityChange","Changing chosen destination on ViewMapActivity");
                startActivity(intent);
            }
            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where the <code>Marker</code>s images are added onto the map to indicate the start and end points
     * visually.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cap_park_marker);

        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 200, 200, false);

        mMap.setMyLocationEnabled(true);

        //if viewmodel is created from CPSI, it will need to call API hence map cannot be initialized immediately
        //if viewmodel is created from D&CPI, it can be initialized immediately
        if (model.getChosenRoute().getValue() == null){
            model.getChosenRoute().observe(this,newRoute-> {
                initializeMap(newRoute);
                model.getChosenRoute().removeObservers(this);
        });
        } else {
            initializeMap(model.getChosenRoute().getValue());
        }
    }

    /**
     * This method is responsible to adjusting the map bounds such that it does not cut off any part of the
     * drawn route.
     * @param newRoute A <code>DirectionsAndCPInfo</code> containing the route information.
     */
    private void initializeMap(DirectionsAndCPInfo newRoute) {
        if (newRoute != null){
            PolylineOptions polylineToAdd = newRoute.getGoogleMapsDirections().getPolylineOptions();
            polylineToAdd.width(10).color(R.color.colorMain);
            mMap.addPolyline(newRoute.getGoogleMapsDirections().getPolylineOptions());

            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = dm.heightPixels;

            List<LatLng> waypoints = polylineToAdd.getPoints();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for(LatLng latlng:waypoints)
                builder.include(latlng);
            LatLngBounds bounds = builder.build();
            CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,(int)(width*0.9),(int)(height*0.5),2);
            mMap.animateCamera(mCameraUpdate);
            mMap.addMarker(new MarkerOptions().position(newRoute.getDestinationLatLng()).title("Marker in Destination"));
        }
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(RouteOverviewActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "permission granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(googleplex));


    }

    /**
     * Plots the Polyline
     * Given an array of waypoints
     */
    public void plotPolyline(List<LatLng> waypoints){
        PolylineOptions plo = new PolylineOptions();
        plo.addAll(waypoints);
        plo.color(R.color.colorMain);
        plo.width(20);
        mMap.addPolyline(plo);
    }

    /**
     * Plots the Polyline
     * Given a PolylineOptions Object
     */
    public void plotPolyline(PolylineOptions plo){
        mMap.addPolyline(plo);
    }
}
