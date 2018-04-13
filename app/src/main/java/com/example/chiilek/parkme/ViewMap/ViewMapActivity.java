package com.example.chiilek.parkme.ViewMap;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.lifecycle.Observer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.chiilek.parkme.CarParkPopUp.CarParkPopUpActivity;
import com.example.chiilek.parkme.R;
import com.example.chiilek.parkme.Suggestion.SuggestionsActivity;
import com.example.chiilek.parkme.api_controllers.availability_api.AvailabilityAPIController;
import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
import com.example.chiilek.parkme.repository.LocationService;
import com.example.chiilek.parkme.repository.Repository;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class ViewMapActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    Bitmap bitmap;
    Bitmap smallMarker;

    List<Marker> MarkerList;

    ViewMapViewModel model;
    //needed to bind to service to get location updates
    private LocationService mLocationService;
    private final int REQUEST_PERMISSION_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        checkLocationPermission();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        MarkerList = new ArrayList<>();

        model = ViewModelProviders.of(this).get(ViewMapViewModel.class);
        //TODO pass current location to Viewmodel
        model.getCarParkList().observe(this, new Observer<List<CarParkStaticInfo>>() {
            @Override
            public void onChanged(@Nullable List<CarParkStaticInfo> newCarParkList) {
            }
        });

        AvailabilityAPIController controller = new AvailabilityAPIController();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setBoundsBias(new LatLngBounds(new LatLng(1.227925, 103.604971), new LatLng(1.456672, 104.003780)));

        Repository repository = Repository.getInstance(this); // TODO remove this shit bruh

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cap_park_marker);
        smallMarker = Bitmap.createScaledBitmap(bitmap, 200, 200, false);



        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mMap.clear();

                Log.d("Maps", "Place selected: " + place.getName());
                CameraPosition cp = new CameraPosition.Builder().target(place.getLatLng()).zoom(14).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
                List<CarParkStaticInfo> list = repository.searchNearbyCarParks(place.getLatLng()).getValue();

                Log.d("Marker", Integer.toString(list.size()));

                for (CarParkStaticInfo cpsi : list){
                    Log.d("Marker", cpsi.getAddress());
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(cpsi.getLatitude()), Double.parseDouble(cpsi.getLongitude())))
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)))
                            .setTag(cpsi);
                }

                mMap.addMarker(new MarkerOptions().position(place.getLatLng()));

                //this makes the RECOMMENDATIONS button go to suggestions
                ImageView buttonRec = findViewById(R.id.viewmap_recommendations_button);
                buttonRec.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ViewMapActivity.this, SuggestionsActivity.class));
                    }
                });
            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //bind to service whenever start
        Intent serviceIntent = new Intent(this, LocationService.class);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        Repository repository = Repository.getInstance(this); // TODO remove this shit bruh
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            CameraPosition cp = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(14).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
                            List<CarParkStaticInfo> list = repository.searchNearbyCarParks(new LatLng(location.getLatitude(), location.getLongitude())).getValue();

                            Log.d("Marker", Integer.toString(list.size()));

                            for (CarParkStaticInfo cpsi : list){
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(cpsi.getLatitude()), Double.parseDouble(cpsi.getLongitude())))
                                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)))
                                        .setTag(cpsi);
                            }

                        }
                    }
                });

        mMap.setOnMarkerClickListener(this);

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        //mMap.getMyLocation().getLatitude();
    }


    //establish service connection needed to bind to service
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();
            Log.d("ViewMapActivity", "In Service Connection");
            if (name.endsWith("LocationService")) {
                mLocationService = ((LocationService.LocationBinder) service).getService();
                mLocationService.startLocationUpdate();
                Log.d("ViewMapActivity", "Location Update started");
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals("LocationService")) {
                Log.d("ViewMapActivity", "Service disconnected");
                mLocationService = null;
            }
        }
    };
    //ask permission to turn on GPS
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

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
                                ActivityCompat.requestPermissions(ViewMapActivity.this,
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

    //handle result of the request

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
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        CarParkStaticInfo cpsi = (CarParkStaticInfo) marker.getTag();
        if (cpsi == null){
            Log.d("Marker", "FUCKFUCKFUCK");
        } else {
            Log.d("Marker", cpsi.getCPNumber());
        }
        Intent intent = new Intent(ViewMapActivity.this,  CarParkPopUpActivity.class);

        intent.putExtra("CarParkStaticInfo", cpsi);
        startActivity(intent);
        return false;
    }
}