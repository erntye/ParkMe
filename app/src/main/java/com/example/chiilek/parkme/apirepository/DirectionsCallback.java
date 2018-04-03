package com.example.chiilek.parkme.apirepository;

import com.example.chiilek.parkme.data_classes.directions_classes.GoogleMapsDirections;

public interface DirectionsCallback {
    public void onSuccess(GoogleMapsDirections gMapsDirections);
    public void onFailure();
}
