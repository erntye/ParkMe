package com.example.chiilek.parkme.repository;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.chiilek.parkme.data_classes.Item;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chiilek on 23/3/2018.
 */

public class APIController implements Callback<Item> {

    static final private String BASE_URL = "https://api.data.gov.sg/v1/transport/";


    public void makeCall(){

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        CarParkAvailabilityAPI carParkAvailabilityAPI = retrofit.create(CarParkAvailabilityAPI.class);
        Log.d("Check", BASE_URL + "carpark-availability?date_time=" + formatDateTime());
        Call<Item> call = carParkAvailabilityAPI.getAvailability(formatDateTime());
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
    public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
        Item item = response.body();

        Log.d("asdasd is not null", "asd");
        if (item != null){

            Log.d("SUCCESS", "*****************************************************");
            if (item.getTimestamp() != null){
                Log.d("UpdateDateTime", item.getTimestamp());
            }
            if (item.getCarParkData() != null){
                Log.d("CarParkDate", item.getCarParkData().toString());
            }
        }
        else {
            String responseee = response.toString();
            Log.d("CarPark is Null", "No object pulled");
            Log.d("CarPark is Null2", responseee);
        }
    }

    @Override
    public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
        Log.d("YOU FAILEDDDDDDDDDDDD********@#$%^&*", t.getMessage());
        t.getStackTrace();
    }
}
