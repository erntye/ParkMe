package com.example.chiilek.parkme.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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


import com.example.chiilek.parkme.R;
import com.example.chiilek.parkme.entity.CarParkInfo;
import com.example.chiilek.parkme.viewmodel.ViewMapViewModel;
import com.example.chiilek.parkme.api.availability_api.AvailabilityAPIController;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This <code>Activity</code> is responsible for displaying the map view of at the beginning of the application.
 * It also allows users to interact with the map view by panning, zooming, and rotating, or center on his location
 * by pressing the location button.
 * <p>
 * This <code>Activity</code> also displays the relevant car parks either within the boundaries of the map view, or
 * those surrounding a destination, depending on user interaction.
 * <p>
 * When the user clicks on specific car park marker, this activity launches the <code>CarParkPopUpActivity</code> to
 * display the relevant information.
 * @see CarParkInfo
 * @see CarParkPopUpActivity
 * @see SelectRouteActivity
 */
public class ViewMapActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private int locationCounter = 0;

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

    //private MutableLiveData<List<CarParkInfo>> cpList = new MutableLiveData<>();
    private List<CarParkInfo> carparkList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        checkLocationPermission();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        suggestCarParks = findViewById(R.id.view_map_recommendations_button);

        model = ViewModelProviders.of(this).get(ViewMapViewModel.class);
        //TODO pass current location to Viewmodel


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
                for (CarParkInfo cpsi : carparkList) {
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

                if (b == null) {
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
                    Log.d("ViewMapActivity", "centering map on current location");
                } else {
                    Log.d("ViewMapActivity", "bundle is not null");
                }
                model.getMcpListMediator().observe(ViewMapActivity.this, newData -> {
                    Log.d("ViewMapActivity", "On map ready, in mediator observe");
                });
                model.getCarParkInfo().observe(ViewMapActivity.this, carParkStaticInfos -> {
                    if (carParkStaticInfos.size() > 0) {
                        Log.d("ViewMapActivity", "Updating CarParkList");
                        carparkList = carParkStaticInfos;
                    }
                });

                // To ensure that the button is only enabled when model is ready.
                Button button = findViewById(R.id.parking_button);
                button.setEnabled(true);

                carparkList = model.getCarParkInfo(newLatLng).getValue();
                Log.d("ViewMapActivity", "onMapReady, Marker count: " + Integer.toString(carparkList.size()));
                for (CarParkInfo cpsi : carparkList) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(cpsi.getLatitude()), Double.parseDouble(cpsi.getLongitude())))
                            .icon(BitmapDescriptorFactory.fromBitmap(parking_lots_smallMarker)))
                            .setTag(cpsi);
                }

            }
            locationCounter++;
            if (locationCounter == 2)
                model.getCurrentLocation().removeObservers(this);
        });


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

            for (CarParkInfo cpsi : carparkList){
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
        CarParkInfo cpsi = (CarParkInfo) marker.getTag();
        LatLng currentLoc = null;
        if (cpsi == null){
            Log.d("ViewMapActivity", "cpsi = null; unable to pass cpsi");
        } else {
            for (CarParkInfo carpark: carparkList){
                if (cpsi.getCPNumber().equals(carpark.getCPNumber())) {
                    Intent intent = new Intent(ViewMapActivity.this, CarParkPopUpActivity.class);
                    intent.putExtra("CarParkInfo", carpark);
                    startActivity(intent);
                }
            }
        }

        return true;
    }

    public void suggestCarParks(View view) {

        if(destination != null && name != null) {
            Intent intent = new Intent(ViewMapActivity.this, SelectRouteActivity.class);
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

        if (carparkList.size() == 0 ){
            Toast.makeText(this, "No Nearby Car Parks", Toast.LENGTH_LONG).show();
        }
        for (CarParkInfo cpsi : carparkList){
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