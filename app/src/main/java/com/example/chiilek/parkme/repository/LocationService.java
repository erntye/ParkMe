package com.example.chiilek.parkme.repository;


import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationService extends Service {

    FusedLocationProviderClient mFusedClient;
    LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location currentLocation;
    private final IBinder mBinder = new LocationService.LocationBinder();

    public class LocationBinder extends Binder {
        public LocationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocationService.this;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("LocationService", " in onCreate: ");
        startLocationUpdate();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    public void startLocationUpdate(){
        mFusedClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        SettingsClient client = LocationServices.getSettingsClient(this);
        client.checkLocationSettings(locationSettingsRequest);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {return;}
                for (Location location : locationResult.getLocations()) {
                    Log.d("LocationService","location is " + location.getLatitude() + " " + location.getLongitude());
                }
            }
        };
        //check for permission before you can request updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //Location Permission already granted
            mFusedClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            Log.d("LocationService","requested updates");
        } else {
            //do not need because we are requesting for permission in UI
        }
    }

    public Location getLastLocation(){
        Log.d("Location Manager", "In get last location");
        if (mFusedClient == null)
            mFusedClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mFusedClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null){
                                Log.d("LocationService","getlastlocation is " + location.getLongitude() + location.getLatitude());
                                currentLocation = location;
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener(){
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("LocationService","Failed to get last location");
                            e.printStackTrace();
                        }
                    });
        }
        return this.currentLocation;
    }

    public void stopLocationUpdates(){
        mFusedClient.removeLocationUpdates(mLocationCallback);
    }

}

/*
    //to be put inside the activity bound to the service
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


*/
