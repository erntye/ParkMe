package com.example.chiilek.parkme.test;

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

public class TestLocationManager extends Service {

    private static final String TAG = TestLocationManager.class.getSimpleName();
    FusedLocationProviderClient mFusedClient;
    LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location currentLocation;
    private final IBinder mBinder = new LocationBinder();

    public class LocationBinder extends Binder {
        TestLocationManager getService() {
            // Return this instance of LocalService so clients can call public methods
            return TestLocationManager.this;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("LocationManager", " in onCreate: ");
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
                    Log.d("LocationManager","location is " + location.getLatitude() + " " + location.getLongitude());
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //Location Permission already granted
            mFusedClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            Log.d("LocationManager","requested updates");
        } else {
            //Request Location Permission
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
                                Log.d("LocationManger","getlastlocation is " + location.getLongitude() + location.getLatitude());
                                currentLocation = location;
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener(){
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("LocationManager","Failed to get last location");
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

