package com.example.chiilek.parkme.api_controllers.roads_api;

import com.example.chiilek.parkme.data_classes.roads_classes.GMapsRoads;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface RoadsAPI {
    @GET("snapToRoads")
    Call<GMapsRoads> getDirections(@QueryMap Map<String, String> params);
}
