package com.example.chiilek.parkme.navigation;

import android.animation.ValueAnimator;
import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.Toast;

import com.example.chiilek.parkme.MultiSearchFragment;
import com.example.chiilek.parkme.R;
import com.example.chiilek.parkme.ViewMap.ViewMapActivity;
import com.example.chiilek.parkme.ViewMap.ViewMapViewModel;
import com.example.chiilek.parkme.repository.LocationService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Expects following data passed in as extras in Intent:
 *  double startPointLat
 *  double startPointLong
 *  double endPointLat
 *  double endPointLong
 */
public class RouteOverviewActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private NavigationViewModel model;
    //needed to bind to service to get location updates
    private LocationService mLocationService;
    private final int REQUEST_PERMISSION_LOCATION = 1;
    private List<LatLng> sampleWayPoints;

    // ---------------------------------------
    //             CHECK PERMISSIONS
    // ---------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_overview);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Create a view model and allow re-created activities to get the same view model instance
        model = ViewModelProviders.of(this).get(NavigationViewModel.class);
//        Bundle extras = getIntent().getExtras();
//        LatLng startPoint = new LatLng(extras.getDouble("startPointLat"), extras.getDouble("startPointLong"));
//        LatLng endPoint = new LatLng(extras.getDouble("endPointLat"), extras.getDouble("endPointLong"));
        ImageView b = findViewById(R.id.startButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RouteOverviewActivity.this, NavigationActivity.class));
            }
        });

    }

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }

        // Pass in either list of LatLng or PolylineOptions object
        //plotPolyline(sampleWayPoints);

        // MAP CAMERA TO GOOGLEPLEX
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            CameraPosition cp = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(13).build();
                            // mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
                        }
                    }
                });

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cap_park_marker);

        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 200, 200, false);

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(37.3830, -122.0870))
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

        //mMap.getMyLocation().getLatitude();

        // Add a marker in Googleplex and move the camera
        LatLng googleplex = new LatLng(37.4220, -122.0940);
        mMap.addMarker(new MarkerOptions().position(googleplex).title("Marker in Googleplex"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(googleplex));

        mMap.setMyLocationEnabled(true);

        // SAMPLE HARDCODED ROUTE
        sampleWayPoints= new ArrayList<>();
        sampleWayPoints.add(new LatLng(37.4220, -122.0940));
        sampleWayPoints.add(new LatLng(37.4130, -122.0831));
        sampleWayPoints.add(new LatLng(37.4000, -122.0762));
        sampleWayPoints.add(new LatLng(37.3830, -122.0870));
//        PolylineOptions route = model.getInitialRoute();
//        plotPolyline(route);
        plotPolyline(sampleWayPoints);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(LatLng latlng:sampleWayPoints)
            builder.include(latlng);
        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,width,(int)(height*0.5),2);
        mMap.animateCamera(mCameraUpdate);
    }

    //establish service connection needed to bind to service
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();
            Log.d("RouteOverviewpActivity", "In Service Connection");
            if (name.endsWith("LocationService")) {
                mLocationService = ((LocationService.LocationBinder) service).getService();
                mLocationService.startLocationUpdate();
                Log.d("RouteOverviewActivity", "Location Update started");
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals("LocationService")) {
                Log.d("RouteOverviewActivity", "Service disconnected");
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
