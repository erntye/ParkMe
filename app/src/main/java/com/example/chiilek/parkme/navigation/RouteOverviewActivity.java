package com.example.chiilek.parkme.navigation;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.content.Intent;

import com.example.chiilek.parkme.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Expects following data passed in as extras in Intent:
 *  double startPointLat
 *  double startPointLong
 *  double endPointLat
 *  double endPointLong
 */
public class RouteOverviewActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private final int REQUEST_PERMISSION_LOCATION = 1;
    private LatLng[] sampleWayPoints = new LatLng[]{new LatLng(37.4220, -122.0940),
                                                    new LatLng(37.4130, -122.0831),
                                                    new LatLng(37.4000, -122.0762),
                                                    new LatLng(37.3830, -122.0870)};

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
        NavigationViewModel model = ViewModelProviders.of(this).get(NavigationViewModel.class);

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

        // Pass in either list of LatLng or PolylineOptions object
        plotPolyline(sampleWayPoints);

        // MAP CAMERA TO GOOGLEPLEX
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            CameraPosition cp = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(13).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(googleplex));
    }

    /**
     * Plots the Polyline
     * Given an array of waypoints
     */
    public void plotPolyline(LatLng[] waypoints){
        PolylineOptions plo = new PolylineOptions();
        plo.add(waypoints);
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
