package com.example.chiilek.parkme.ViewMap;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.MutableLiveData;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.Button;
import android.widget.Toast;


import com.example.chiilek.parkme.CarParkPopUp.CarParkPopUpActivity;
import com.example.chiilek.parkme.R;
import com.example.chiilek.parkme.Suggestion.SuggestionsActivity;
import com.example.chiilek.parkme.api_controllers.availability_api.AvailabilityAPIController;
import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
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
import java.util.Locale;

public class ViewMapActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;

    // To create the bitmap for parking lots
    Bitmap parking_lots_bitmap;
    Bitmap parking_lots_smallMarker;

    LatLng destination;

    String name;

    Button suggestCarParks;
    Place placeUpdate;
    PlaceAutocompleteFragment autocompleteFragment;

    Bundle b;

    ViewMapViewModel model;

    private final int REQUEST_PERMISSION_LOCATION = 1;


    //private MutableLiveData<List<CarParkStaticInfo>> cpList = new MutableLiveData<>();
    private List<CarParkStaticInfo> carparkList = new ArrayList<>();
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

        suggestCarParks = findViewById(R.id.view_map_recommendations_button);

        model = ViewModelProviders.of(this).get(ViewMapViewModel.class);
        //TODO pass current location to Viewmodel
        model.getCarParkInfo().observe(this, new Observer<List<CarParkStaticInfo>>() {
            @Override
            public void onChanged(@Nullable List<CarParkStaticInfo> newCarParkList) {
            }
        });
        model.getCurrentLocation().observe(this, newLocation ->
            {


            });

        AvailabilityAPIController controller = new AvailabilityAPIController();

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setBoundsBias(new LatLngBounds(new LatLng(1.227925, 103.604971), new LatLng(1.456672, 104.003780)));

        // Create customised markers.
        // Parking lots markers
        parking_lots_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.carpark_sign_shadow);
        parking_lots_smallMarker = Bitmap.createScaledBitmap(parking_lots_bitmap, 88, 89, false);

        b = this.getIntent().getExtras();

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener()
        {@Override
        public void onPlaceSelected(Place place) {
            mMap.clear();

            Log.d("Maps", "Place selected: " + place.getName());
            CameraPosition cp = new CameraPosition.Builder().target(place.getLatLng()).zoom(16).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));

            carparkList = model.getCarParkInfo(place.getLatLng()).getValue();
            if(carparkList != null) {
                for (CarParkStaticInfo cpsi : carparkList) {
                    Log.d("Marker", cpsi.getAddress());
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(cpsi.getLatitude()), Double.parseDouble(cpsi.getLongitude())))
                            .icon(BitmapDescriptorFactory.fromBitmap(parking_lots_smallMarker)))
                            .setTag(cpsi);
                }
                ;
            }

            // Create red marker to mark searched location
            mMap.addMarker(new MarkerOptions()
                    .position(place.getLatLng()));

            //Sets the fields to pass into suggest_car_parks
            name = place.getName().toString();
            destination = place.getLatLng();
            suggestCarParks.setVisibility(View.VISIBLE);
            Log.d("Visibility", Integer.toString(suggestCarParks.getVisibility()));
        }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setPadding(0, 300, 0, 0);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }

        model.getCurrentLocation().observe(this, newLatLng ->{
            if (newLatLng != null) {
                CameraPosition cp = new CameraPosition.Builder().target(newLatLng).zoom(16).build();
                if(b==null) mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
                model.getMcpListMediator().observe(ViewMapActivity.this, newData ->{
                    Log.d("ViewMapActivity", "On map ready, in mediator observe");
                });
                model.getCarParkInfo().observe(ViewMapActivity.this, carParkStaticInfos -> {
                    if (carParkStaticInfos.size() > 0) {
                        carparkList = carParkStaticInfos;
                    }
                });

                // To ensure that the button is only enabled when model is ready.
                Button button = findViewById(R.id.parking_button);
                button.setEnabled(true);

                carparkList = model.getCarParkInfo(newLatLng).getValue();
                Log.d("ViewMapActivity", "onMapReady, Marker count: " + Integer.toString(carparkList.size()));
                for (CarParkStaticInfo cpsi : carparkList){
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(cpsi.getLatitude()), Double.parseDouble(cpsi.getLongitude())))
                            .icon(BitmapDescriptorFactory.fromBitmap(parking_lots_smallMarker)))
                            .setTag(cpsi);
                }

            }
        });

        /*mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            CameraPosition cp = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(16).build();
                            if(b==null) mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
                            model.getMcpListMediator().observe(ViewMapActivity.this, newData ->{
                                Log.d("ViewMapActivity", "On map ready, in mediator observe");
                            });
                            model.getCarParkInfo().observe(ViewMapActivity.this, new Observer<List<CarParkStaticInfo>>(){
                                @Override
                                public void onChanged(@Nullable List<CarParkStaticInfo> carParkStaticInfos) {
                                    if (carParkStaticInfos.size() > 0) {
                                        Log.d("ViewMapActivity", "getCarParkInfo onChanged, availability info: " + carParkStaticInfos.get(0).getAvailableCarLots());
                                        cpList.setValue(carParkStaticInfos);
                                    }
                                }
                            });

                            // To ensure that the button is only enabled when model is ready.
                            Button button = findViewById(R.id.parking_button);
                            button.setEnabled(true);

                            cpList.setValue(model.getCarParkInfo(new LatLng(location.getLatitude(), location.getLongitude())).getValue());
                            Log.d("ViewMapActivity", "onMapReady, Marker count: " + Integer.toString(cpList.getValue().size()));
                            for (CarParkStaticInfo cpsi : cpList.getValue()){
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(cpsi.getLatitude()), Double.parseDouble(cpsi.getLongitude())))
                                        .icon(BitmapDescriptorFactory.fromBitmap(parking_lots_smallMarker)))
                                        .setTag(cpsi);
                            }

                        }
                    }
                });*/

        mMap.setOnMarkerClickListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        //mMap.getMyLocation().getLatitude();

        PlaceSelectionListener listener = new PlaceSelectionListener()
        {@Override
        public void onPlaceSelected(Place place) {
            autocompleteFragment.setText(place.getName());
            mMap.clear();

            Log.d("Maps", "Place selected: " + place.getName());
            CameraPosition cp = new CameraPosition.Builder().target(place.getLatLng()).zoom(16).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));

            //cpList.setValue(model.getCarParkInfo(place.getLatLng()).getValue());
            carparkList = model.getCarParkInfo(place.getLatLng()).getValue();

            for (CarParkStaticInfo cpsi : carparkList){
                Log.d("Marker", cpsi.getAddress());
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(cpsi.getLatitude()), Double.parseDouble(cpsi.getLongitude())))
                        .icon(BitmapDescriptorFactory.fromBitmap(parking_lots_smallMarker)))
                        .setTag(cpsi);
            }

            // Create red marker to mark searched location
            mMap.addMarker(new MarkerOptions()
                    .position(place.getLatLng()));

            //Sets the fields to pass into suggest_car_parks
            name = place.getName().toString();
            destination = place.getLatLng();
            suggestCarParks.setVisibility(View.VISIBLE);
            Log.d("Visibility", Integer.toString(suggestCarParks.getVisibility()));
        }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        };

        if (b != null){
            placeUpdate = b.getParcelable("Place");
            if (placeUpdate != null)
                listener.onPlaceSelected(placeUpdate);
        }
    }


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
        LatLng currentLoc = null;
        if (cpsi == null){
            Log.d("ViewMapActivity", "cpsi = null; unable to pass cpsi");
        } else {
            for (CarParkStaticInfo carpark: carparkList){
                if (cpsi.getCPNumber().equals(carpark.getCPNumber())) {
                    Intent intent = new Intent(ViewMapActivity.this, CarParkPopUpActivity.class);
                    intent.putExtra("CarParkStaticInfo", carpark);
                    startActivity(intent);
                }
            }
        }

        return true;
    }

    public void suggestCarParks(View view) {

        if(destination != null && name != null) {
            Intent intent = new Intent(ViewMapActivity.this, SuggestionsActivity.class);
            intent.putExtra("Destination", destination);
            intent.putExtra("Name", name);
            startActivity(intent);

        } else {
            Log.d("ViewMapActivity", "destination = null || name = null");
        }

    }

    public void showCarParks(View view) {
        mMap.clear();

        LatLng latLng = mMap.getCameraPosition().target;

        CameraPosition cp = new CameraPosition.Builder().target(latLng).zoom(16).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
        //cpList.setValue(model.getCarParkInfo(latLng).getValue());
        carparkList = model.getCarParkInfo(latLng).getValue();

        for (CarParkStaticInfo cpsi : carparkList){
            Log.d("Marker", cpsi.getAddress());
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(cpsi.getLatitude()), Double.parseDouble(cpsi.getLongitude())))
                    .icon(BitmapDescriptorFactory.fromBitmap(parking_lots_smallMarker)))
                    .setTag(cpsi);
        }

        // Create red marker to mark searched location
        mMap.addMarker(new MarkerOptions()
                .position(latLng));

        //Sets the fields to pass into suggest_car_parks
        name = getCompleteAddressString(latLng.latitude, latLng.longitude);
        destination = latLng;
        suggestCarParks.setVisibility(View.VISIBLE);
        PlaceAutocompleteFragment placeAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        placeAutocompleteFragment.setText(name);
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {

        String strAdd = "";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE,
                    LONGITUDE, 1);

            if (addresses.get(0) != null) {

                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                String s = returnedAddress.toString();
                boolean copy = false;

                for (int i = 0; i < s.length(); i++) {
                    if(s.charAt(i) == ','){
                        break;
                    }
                    if(copy){
                        strReturnedAddress.append(s.charAt(i));
                    }
                    if(s.charAt(i) == '"'){
                        copy = true;
                    }
                }

                strAdd = strReturnedAddress.toString();

                Log.d("getCompleteAddressString", strAdd);
            } else {
                Log.d("getCompleteAddressString", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("getCompleteAddressString", "Canont get Address!");
        }
        return strAdd;
    }
}