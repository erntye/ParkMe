package com.example.chiilek.parkme.apirepository;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.chiilek.parkme.data_classes.Envelope;
import com.example.chiilek.parkme.data_classes.directions_classes.GoogleMapsDirections;
import com.example.chiilek.parkme.data_classes.directions_classes.Step;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author chiilek
 * @since 24/3/2018.
 *
 * This is the main API controller class.
 * The only class that needs to be instantiated is makeCall().
 *
 *  Example:
 *      APIController controller = new APIController();
 *      controller.makeCall();
 *
 *      controller.onResponse(Call<Envelope> call, Response<Envelope> response){
 *          // Whatever you want to do with the data from the API can be done here.
 *          // Examples are below.
 *      }
 *
 *      controller.onFailure(Call<Envelope> call, Throwable t){
 *          // Whatever you want to do when the call fails can be implemented here.
 *          // Examples are below.
 *      }
 *
 *
 *  use callDirectionsAPI(LatLng origin, LatLng destination) for directions api calling
 *
 */

public class APIController implements Callback<Envelope> {

    static final private String BASE_URL = "https://api.data.gov.sg/v1/transport/";
    static final private String GMAPS_DIRECTION_API_BASE_URL = "https://maps.googleapis.com/maps/api/directions/";
    static final private String GMAPS_API_KEY = "AIzaSyB0R4S6MUMQ_tuEfT29tr4RQnZmcGqo1Qo";


    public void makeCall(){

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        CarParkAvailabilityAPI carParkAvailabilityAPI = retrofit.create(CarParkAvailabilityAPI.class);
        Log.d("Check", BASE_URL + "carpark-availability?date_time=" + formatDateTime());
        Call<Envelope> call = carParkAvailabilityAPI.getAvailability(formatDateTime());
        call.enqueue(this);
    }

    // Gets current date and time and formats it for API calling.
    private String formatDateTime(){
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));

        String month = Integer.toString(Calendar.getInstance().get(Calendar.MONTH));
        if (month.length() == 1)
            month = "0" + month;

        String dayOfMonth = Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        if (dayOfMonth.length() == 1)
            dayOfMonth = "0" + dayOfMonth;

        String hour = Integer.toString(Calendar.getInstance().get(Calendar.HOUR));
        if (hour.length() == 1)
            hour = "0" + hour;
        String minute = Integer.toString(Calendar.getInstance().get(Calendar.MINUTE));
        if (minute.length() == 1)
            minute = "0" + minute;
        String second = Integer.toString(Calendar.getInstance().get(Calendar.SECOND));
        if (second.length() == 1)
            second = "0" + second;

        return year + "-" + month + "-" + dayOfMonth + "T" + hour + ":" + minute + ":" + second;
    }

    @Override
    public void onResponse(@NonNull Call<Envelope> call, @NonNull Response<Envelope> response) {
        Envelope envelope = response.body();

        if (envelope != null){

            Log.d("SUCCESS", "*****************************************************");
            if (envelope.getItem() != null){
                Log.d("Repo_UpdateDateTime", envelope.getItem().getTimestamp());
                Log.d("Repo_CarParkNumber", envelope.getItem().getCarParkData().get(0).getUpdateDatetime());
                Log.d("Repo_Avail", Integer.toString(envelope.getItem().getCarParkData().get(0).getCarParkInfo().get(0).getLotsAvailable()));
                Log.d("Repo_Total", Integer.toString(envelope.getItem().getCarParkData().get(0).getCarParkInfo().get(0).getTotalLots()));
                Log.d("Repo_Type", Character.toString(envelope.getItem().getCarParkData().get(0).getCarParkInfo().get(0).getLotType()));
            } else {
                Log.d("Repo_Update time is null", "No object pulled: " + response.body().toString());
            }
            if (envelope.getItem() != null){
                Log.d("Repo_CarParkDate", envelope.getItem().toString());
            }
        }
        else {
            Log.d("Repo_CarPark is Null", "No object pulled: " + response.toString());
        }
    }

    @Override
    public void onFailure(@NonNull Call<Envelope> call, @NonNull Throwable t) {
        Log.d("Repo_YOU FAILEDDDDDDDDDDDD********@#$%^&*", t.getMessage());
        t.getStackTrace();
    }

    public void callDirectionsAPI(LatLng origin, LatLng destination){
        Map<String, String> params = new HashMap<String,String>();
        params.put("origin", "75 9th Ave New York, NY");
        params.put("destination", "MetLife Stadium 1 MetLife Stadium Dr East Rutherford, NJ 07073");
        params.put("mode", "driving");
        params.put("key", GMAPS_API_KEY);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GMAPS_DIRECTION_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GMapsDirectionsAPI directionsAPI = retrofit.create(GMapsDirectionsAPI.class);

        Call<GoogleMapsDirections> call = directionsAPI.getDirections(params);
        System.out.println("before enqueue");

        GoogleMapsDirections returnMapsDirections;
        call.enqueue(new Callback<GoogleMapsDirections>(){
            @Override
            public void onResponse(Call<GoogleMapsDirections> call, Response<GoogleMapsDirections> response){
                Log.d("API Controller  (G Maps)","in response");
                GoogleMapsDirections gMapsDirections = response.body();
//              String overviewPolyline = gMapsDirections.getRoutes().get(0).getOverviewPolyline().getPoints();
                List<Step> steps = gMapsDirections.getRoutes().get(0).getLegs().get(0).getSteps();
                returnMapsDirections = gMapsDirections;
                //TODO: get library to decode string polyline into latlng (?) or use latlng to plot
            }

            @Override
            public void onFailure(Call<GoogleMapsDirections> call, Throwable t){
                t.printStackTrace();
            }
        });

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
