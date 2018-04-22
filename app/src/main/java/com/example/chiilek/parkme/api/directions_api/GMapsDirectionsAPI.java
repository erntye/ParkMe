package com.example.chiilek.parkme.api.directions_api;

import com.example.chiilek.parkme.entity.directionsapi.GoogleMapsDirections;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Interface to define GET call to the Directions API. Used by <code>Retrofit</code>.
 */
public interface GMapsDirectionsAPI {
    @GET("json")
    Call<GoogleMapsDirections> getDirections(@QueryMap Map<String, String> params);
}
