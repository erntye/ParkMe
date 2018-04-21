package com.example.chiilek.parkme.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.chiilek.parkme.R;
import com.example.chiilek.parkme.entity.CarParkInfo;
import com.example.chiilek.parkme.viewmodel.NavigationViewModel;
import com.example.chiilek.parkme.viewmodel.NavigationViewModelFactory;
import com.example.chiilek.parkme.entity.DirectionsAndCPInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

/**
 * Expects following data passed in as extras in Intent:
 *  double startPointLat
 *  double startPointLong
 *  double endPointLat
 *  double endPointLong
 */
public class NavigationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PolylineOptions sampleWayPoints;
    private float v;
    int index, next;
    private double lat,lng;
    private Handler handler;
    private LatLng prevLoc, currentLoc, destLoc;
    float bearing;
    private String destination;
    private PolylineOptions blackPolyLineOptions;
    private Polyline blackPolyline;
    private LatLng myPosition;
    private Marker marker;
    private DirectionsAndCPInfo initialChosenRoute;
    private CarParkInfo initialCarPark;
    private NavigationViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        sampleWayPoints= new ArrayList<>();
//        sampleWayPoints.add(new LatLng(37.4220, -122.0940));
//        sampleWayPoints.add(new LatLng(37.4130, -122.0831));
//        sampleWayPoints.add(new LatLng(37.4000, -122.0762));
//        sampleWayPoints.add(new LatLng(37.3830, -122.0870));

        //Create a view model and allow re-created activities to get the same view model instance
        //model = ViewModelProviders.of(this).get(NavigationViewModel.class);
        //TODO update the above with the below once completed
        Intent parentIntent = getIntent();
        initialChosenRoute = (DirectionsAndCPInfo) parentIntent.getSerializableExtra("chosenRoute");
        Log.d("NavigationActivity", "onCreate: initialising initial chosen route: " + initialChosenRoute.getCarParkInfo().getAddress());
        destLoc = initialChosenRoute.getDestinationLatLng();

        model = ViewModelProviders
                .of(this, new NavigationViewModelFactory(this.getApplication(), initialChosenRoute))
                .get(NavigationViewModel.class);

        // Random starting location
        currentLoc = new LatLng(1.346267,103.707881);
    }
//        Bundle extras = getIntent().getExtras();
//        LatLng startPoint = new LatLng(extras.getDouble("startPointLat"), extras.getDouble("startPointLong"));
//        LatLng endPoint = new LatLng(extras.getDouble("endPointLat"), extras.getDouble("endPointLong"));

//        //TODO place the code below to correct place
//        //SHOW MESSAGE WHEN REACHED     /**********************************************************/
//        startActivity(new Intent(NavigationActivity.this, ReachMessageActivity.class));


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setPadding(0, 90, 0, 0);
        MapStyleOptions nightStyle = MapStyleOptions.loadRawResourceStyle(this, R.raw.styles_night);
        googleMap.setMapStyle(nightStyle);

        marker = mMap.addMarker(new MarkerOptions().position(currentLoc)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cursor)));

        mMap.addMarker(new MarkerOptions().position(destLoc).title("Destination Marker"));

        model.getCurrentLoc().observe(this, newCurrentLoc -> {
            prevLoc = currentLoc;
            currentLoc = newCurrentLoc;
            Log.d("currentLoc", prevLoc.toString());
            Log.d("currentLoc", newCurrentLoc.toString());
            PolylineOptions updatedRoute = model.getUpdatingRoute().getValue().getPolylineOptions();
            float bearing = getBearing(prevLoc, newCurrentLoc);
            marker.setPosition(newCurrentLoc);
            marker.setAnchor(0.5f, 0.5f);
            marker.setRotation(bearing);
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(newCurrentLoc)
                    .zoom(18f)
                    .bearing(bearing)
                    .build(
                    )));
            plotPolyline(updatedRoute);
            if(checkReached(currentLoc, destLoc)){
                model.getCurrentLoc().removeObservers(this);
                reached();
            }
        });

        model.getAvailabilityStatus().observe(this, newStatus -> {
            if(newStatus == 0){
                Log.d("NavigationActivity", "availabilityStatus observe, 0 lots left. calling reroute()");
                reroute();
            }else if(newStatus == 1){
                Toast.makeText(this.getApplicationContext(), "Could not find another CarPark to reroute to. Staying on current route.",Toast.LENGTH_LONG).show();
            }
        });
    }

    private float getBearing(LatLng startPosition, LatLng newPos) {
        double lat = Math.abs(startPosition.latitude - newPos.latitude);
        double lng = Math.abs(startPosition.longitude - newPos.longitude);

        if (startPosition.latitude< newPos.latitude && startPosition.longitude < newPos.longitude){
            return (float) Math.toDegrees(Math.atan(lng/lat));
        }else if (startPosition.latitude >= newPos.latitude && startPosition.longitude < newPos.longitude){
            return (float) ((90-Math.toDegrees(Math.atan(lng/lat)))+90);
        }else if (startPosition.latitude >= newPos.latitude && startPosition.longitude >= newPos.longitude){
            return (float) (Math.toDegrees(Math.atan(lng/lat))+180);
        }else if (startPosition.latitude< newPos.latitude && startPosition.longitude >= newPos.longitude){
            return (float) ((90-Math.toDegrees(Math.atan(lng/lat)))+270);
        }
        else return -1;
    }

    public void plotPolyline(List<LatLng> waypoints){
        PolylineOptions plo = new PolylineOptions();
        plo.addAll(waypoints);
        plo.color(Color.LTGRAY);
        plo.width(30);
        mMap.addPolyline(plo);
    }

    public void plotPolyline(PolylineOptions plo){
        plo.color(Color.LTGRAY);
        plo.width(30);
        mMap.addPolyline(plo);
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
                                ActivityCompat.requestPermissions(NavigationActivity.this,
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

    public boolean checkReached(LatLng prevLoc, LatLng currentLoc){
        double threshold = 0.0001;
        double longDist = prevLoc.longitude - currentLoc.longitude;
        double latDist = prevLoc.latitude - currentLoc.latitude;
        if(longDist<0) longDist *= -1;
        if(latDist<0) latDist *= -1;
        if (longDist<threshold && latDist < threshold){
            return true;
        } else return false;
    }

    public void reached(){
        Intent intent = new Intent(NavigationActivity.this, ReachMessageActivity.class);
        Log.d("ReroutePopup","Displaying Reroute Popup Msg");
        startActivity(intent);
    }

    private void reroute(){
        Intent intent = new Intent(NavigationActivity.this, ReroutePopUpActivity.class);
        Log.d("NavigationActivity", "reroute: initial chosen route: " + initialChosenRoute.getCarParkInfo().getAddress());
        intent.putExtra("initialRoute", initialChosenRoute);
        intent.putExtra("alternativeRoute", model.getAlternativeRoute());
        Log.d("NavigationActivity", "reroute: calling re route pop up activity");
        startActivity(intent);
    }
}