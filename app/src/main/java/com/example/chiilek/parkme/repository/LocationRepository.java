package com.example.chiilek.parkme.repository;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationRepository  {

    private FusedLocationProviderClient mFusedClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private MutableLiveData<LatLng> currentLocation;
    private static LocationRepository INSTANCE;
    private Context mContext;

    private LocationRepository(Context context){
        mContext = context;
        mFusedClient = LocationServices.getFusedLocationProviderClient(mContext);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {return;}
                Location location = locationResult.getLastLocation();
                currentLocation.setValue(new LatLng(location.getLatitude(), location.getLongitude()));
                Log.d("Location Repo","Location Update is " + location.getLatitude() + " "
                        + location.getLongitude());
            }
        };
        startLocationUpdate();
        currentLocation = new MutableLiveData<>();
        currentLocation.setValue(new LatLng(1.321,103.850));
        Log.d("Location Repo", "created");
    }

    public static LocationRepository getLocationRepository(Context context){
        if (INSTANCE == null)
            INSTANCE = new LocationRepository(context.getApplicationContext());
        Log.d("Location Repo", "called get singleton");
        return INSTANCE;
    }

    private void startLocationUpdate(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        SettingsClient client = LocationServices.getSettingsClient(mContext);
        client.checkLocationSettings(locationSettingsRequest);

        //check for permission before you can request updates
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //Location Permission already granted
            mFusedClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            Log.d("Location Repo","requested updates");
        } else {
            //do not need because we are requesting for permission in UI
            Log.d("Location Repo","update permission not granted");
        }
    }
    public LiveData<LatLng> getLocation(){
        Log.d("Location Repo", "return current location " + currentLocation.getValue().toString());
        return this.currentLocation;
    }

    public MutableLiveData<LatLng> getLastLocation(){
        Log.d("Location Repo", "Getting Last Location");
        if (mFusedClient == null)
            mFusedClient = LocationServices.getFusedLocationProviderClient(mContext);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mFusedClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null){
                                Log.d("Location Repo","getlastlocation is " + location.getLongitude() + location.getLatitude());
                                currentLocation.setValue(new LatLng(location.getLatitude(),location.getLongitude()));
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener(){
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Location Repo","Failed to get last location");
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