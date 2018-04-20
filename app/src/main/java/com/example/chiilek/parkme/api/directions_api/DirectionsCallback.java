package com.example.chiilek.parkme.api.directions_api;

import com.example.chiilek.parkme.entity.directionsapi.GoogleMapsDirections;

public interface DirectionsCallback {
    public void onSuccess(GoogleMapsDirections gMapsDirections);
    public void onFailure();
}
