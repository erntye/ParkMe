package com.example.chiilek.parkme.api.directions_api;

import com.example.chiilek.parkme.entity.directionsapi.GoogleMapsDirections;

/**
 * Interface for callbacks related to Directions API calls.
 */
public interface DirectionsCallback {
    void onSuccess(GoogleMapsDirections gMapsDirections);
    void onFailure();
}
