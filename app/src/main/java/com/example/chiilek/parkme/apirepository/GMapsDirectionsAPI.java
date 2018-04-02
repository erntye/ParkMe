package com.example.chiilek.parkme.apirepository;

import com.example.chiilek.parkme.data_classes.Envelope;
import com.example.chiilek.parkme.data_classes.directions_classes.GoogleMapsDirections;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface GMapsDirectionsAPI {
    @GET("json")
    Call<GoogleMapsDirections> getDirections(@QueryMap Map<String, String> params);
}
