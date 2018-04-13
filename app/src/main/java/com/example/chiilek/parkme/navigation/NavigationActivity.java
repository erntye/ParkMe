package com.example.chiilek.parkme.navigation;

import android.Manifest;
import android.app.AlertDialog;
import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;

import com.example.chiilek.parkme.MultiSearchFragment;
import com.example.chiilek.parkme.R;
import com.example.chiilek.parkme.ViewMap.ViewMapActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.maps.model.JointType.ROUND;

/**
 * Expects following data passed in as extras in Intent:
 *  double startPointLat
 *  double startPointLong
 *  double endPointLat
 *  double endPointLong
 */
public class NavigationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private List<LatLng> sampleWayPoints;
    private float v;
    int index, next;
    private double lat,lng;
    private Handler handler;
    private LatLng startPosition, endPosition;
    private String destination;
    private MultiSearchFragment searchFragment;
    private PolylineOptions blackPolyLineOptions;
    private Polyline blackPolyline;
    private LatLng myPosition;
    private Marker marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sampleWayPoints= new ArrayList<>();
        sampleWayPoints.add(new LatLng(37.4220, -122.0940));
        sampleWayPoints.add(new LatLng(37.4130, -122.0831));
        sampleWayPoints.add(new LatLng(37.4000, -122.0762));
        sampleWayPoints.add(new LatLng(37.3830, -122.0870));

        //Create a view model and allow re-created activities to get the same view model instance
        NavigationViewModel model = ViewModelProviders.of(this).get(NavigationViewModel.class);

//        Bundle extras = getIntent().getExtras();
//        LatLng startPoint = new LatLng(extras.getDouble("startPointLat"), extras.getDouble("startPointLong"));
//        LatLng endPoint = new LatLng(extras.getDouble("endPointLat"), extras.getDouble("endPointLong"));


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

        // ---------------------------------------
        //             CHECK PERMISSIONS
        // ---------------------------------------

        // MAP CAMERA TO GOOGLEPLEX
        // REPLACE WITH WHEREVER YOU ARE
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermission();
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            CameraPosition cp = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(14).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
                        }
                    }
                });

        // Add a marker in Googleplex and move the camera
        LatLng googleplex = new LatLng(37.4220, -122.0940);
        mMap.addMarker(new MarkerOptions().position(googleplex).title("Marker in Googleplex"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(googleplex));

        blackPolyLineOptions = new PolylineOptions();
        blackPolyLineOptions.color(Color.BLACK);
        blackPolyLineOptions.width(5);
        blackPolyLineOptions.startCap(new SquareCap());
        blackPolyLineOptions.endCap(new SquareCap());
        blackPolyLineOptions.jointType(JointType.ROUND);
        blackPolyline = mMap.addPolyline(blackPolyLineOptions);


        ValueAnimator polylineAnimator = ValueAnimator.ofInt(0,100);
        polylineAnimator.setDuration(3000);
        polylineAnimator.setInterpolator(new LinearInterpolator());
        polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                List<LatLng> points = blackPolyline.getPoints();
                int percentValue = (int)animation.getAnimatedValue();
                int size = points.size();
                int newPoints = (int) (size*(percentValue / 100.0f));
                List<LatLng> p = points.subList(0, newPoints);
                blackPolyline.setPoints(p);
            }
        });
        // polylineAnimator.start();

        LatLng test = new LatLng(37.3830, -122.0870);

        //car marker goes here
        marker = mMap.addMarker(new MarkerOptions().position(test)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));

        handler = new Handler();
        index = -1;
        next = 1;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(index < sampleWayPoints.size()-1){
                    index++;
                    next = index+1;
                    startPosition = sampleWayPoints.get(index);
                    endPosition = sampleWayPoints.get(next);
                }

                ValueAnimator valueAnimator = ValueAnimator.ofInt(0,1);
                valueAnimator.setDuration(3000);
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        v = valueAnimator.getAnimatedFraction();
                        lng = v*endPosition.longitude+(1-v)*startPosition.longitude;
                        lat = v*endPosition.latitude+(1-v)*startPosition.latitude;
                        LatLng newPos = new LatLng(lat,lng);
                        marker.setPosition(newPos);
                        marker.setAnchor(0.5f,0.5f);
                        marker.setRotation(getBearing(startPosition,newPos));
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(newPos)
                                .zoom(15.5f)
                                .build(
                                )));

                        plotPolyline(sampleWayPoints);

                    }
                });
                valueAnimator.start();
                handler.postDelayed(this,3000);
            }
        }, 3000);
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
        plo.color(R.color.colorMain);
        plo.width(20);
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
}

//model.getGoogleMapsDirections().observe(this, newDirections ->{
//        polopt = newDirections.getPolylineOptions();
//        plotPolyline(polopt);
//        });
