package com.example.chiilek.parkme.api.directions_api;

import android.util.Log;

import com.example.chiilek.parkme.entity.directionsapi.GoogleMapsDirections;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Controller class that makes use of <code>Retrofit</code> to make GET requests to the Directions API calls.
 * Fits the JSON response into a <code>GoogleMapsDirections</code> object which is sent through the <code>DirectionsCallback</code> callback.
 * To make a call to the API, use callDirectionsAPI method.
 */
public class DirectionsAPIController {

    static final private String GMAPS_DIRECTION_API_BASE_URL = "https://maps.googleapis.com/maps/api/directions/";
    static final private String GMAPS_API_KEY = "AIzaSyCMcA56knRPYgayHU95ceIL2nNyLkpIeUo";
    private static DirectionsAPIController INSTANCE;

    /**
     * Singleton Pattern implemented to get instance of <code>DirectionsAPIController</code>
     * @return Singleton instance of <code>DirectionsAPIController</code>.
     */
    public static DirectionsAPIController getInstance(){
        if (INSTANCE == null)
            INSTANCE = new DirectionsAPIController();
        return INSTANCE;
    }


    /**
     * Makes a call to the Directions API to calculate a route from a given origin and destination.
     * Passes the response object as a <code>GoogleMapsDirections</code> through the <code>DirectionsCallback</code> object taken in this method.
     * @param origin <code>LatLng</code> object defining the origin.
     * @param destination <code>LatLng</code> object defining the destination.
     * @param repoCallback <code>DirectionsCallback</code> to pass the response object through.
     */
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
                        Log.d("DirectionsAPIController","On Response; code: " + response.code());
                        Log.d("DirectionsAPIController","On Response; directions status: " + gMapsDirections.getStatus());
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

    }
}
