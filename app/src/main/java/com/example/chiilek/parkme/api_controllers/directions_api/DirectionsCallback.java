package com.example.chiilek.parkme.api_controllers.directions_api;

import com.example.chiilek.parkme.data_classes.directions_classes.GoogleMapsDirections;

public interface DirectionsCallback {
    public void onSuccess(GoogleMapsDirections gMapsDirections);
    public void onFailure();
}
