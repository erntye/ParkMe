package com.example.chiilek.parkme.apirepository;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.chiilek.parkme.data_classes.Envelope;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;

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
 *      AvailabilityAPIController controller = new AvailabilityAPIController();
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
 */

public class AvailabilityAPIController implements Callback<Envelope> {

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
                APIRepository.setCarParkList(envelope.getItem().getCarParkData());
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
}
