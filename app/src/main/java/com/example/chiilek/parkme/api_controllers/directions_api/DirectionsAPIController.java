package com.example.chiilek.parkme.api_controllers.directions_api;

import android.util.Log;

import com.example.chiilek.parkme.api_controllers.roads_api.RoadsAPIController;
import com.example.chiilek.parkme.data_classes.directions_classes.GoogleMapsDirections;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DirectionsAPIController {

    static final private String GMAPS_DIRECTION_API_BASE_URL = "https://maps.googleapis.com/maps/api/directions/";
    //TODO NOT HARDCODE THE STRING
    static final private String GMAPS_API_KEY = "AIzaSyCl6vY67OT8SbBtih9kD1zttnk9lOUxbT4";

    private static DirectionsAPIController INSTANCE;

    public static DirectionsAPIController getInstance(){
        if (INSTANCE == null)
            INSTANCE = new DirectionsAPIController();
        return INSTANCE;
    }


    public void callDirectionsAPI(LatLng origin, LatLng destination, DirectionsCallback repoCallback){
        Map<String, String> params = new HashMap<String,String>();
        params.put("origin", Double.toString(origin.latitude) + "," + Double.toString(origin.longitude));
        params.put("destination", Double.toString(destination.latitude) + "," + Double.toString(destination.longitude));
        params.put("mode", "driving");
        params.put("key", GMAPS_API_KEY);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GMAPS_DIRECTION_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GMapsDirectionsAPI directionsAPI = retrofit.create(GMapsDirectionsAPI.class);

        Call<GoogleMapsDirections> call = directionsAPI.getDirections(params);

        Log.d("DirectionsAPIController","before enqueue, calling for: " + destination.toString());

        call.enqueue(new Callback<GoogleMapsDirections>(){
            @Override
            public void onResponse(Call<GoogleMapsDirections> call, Response<GoogleMapsDirections> response){
                Log.d("DirectionsAPIController","in response");
                GoogleMapsDirections gMapsDirections = response.body();
                switch(gMapsDirections.getStatus()){
                    //either origin or destination is a invalid LatLng pair.
                    case "NOT_FOUND":
                        Log.d("DirectionsAPIController", "onResponse but gMaps status NOT_FOUND");
                        repoCallback.onFailure();
                        break;
                    case "ZERO_RESULTS":
                        Log.d("DirectionsAPIController", "onResponse but gMaps status ZERO_RESULTS");
                        repoCallback.onFailure();
                        break;
                    default:
                        gMapsDirections.setgMapsRoads(RoadsAPIController.getInstance().getRoads(gMapsDirections));
                        repoCallback.onSuccess(gMapsDirections);
                }
            }

            @Override
            public void onFailure(Call<GoogleMapsDirections> call, Throwable t){
                Log.d("DirectionsAPIController", "in failure");
                repoCallback.onFailure();
                t.printStackTrace();
            }
        });
        Log.d("DirectionsAPIController", "enqueued");


        //for testing the api call
//        try {
//            Response<GoogleMapsDirections> response = call.execute();
//            System.out.println("in response");
//            GoogleMapsDirections gMapsDirections = response.body();
//            Log.d("test", "called .body method");
//            Log.d("test", call.request().url().toString());
//            Log.d("test", "response code: " + gMapsDirections.getStatus());
//            Log.d("test", gMapsDirections.getRoutes().get(0).
//                    getOverviewPolyline().getPoints());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
