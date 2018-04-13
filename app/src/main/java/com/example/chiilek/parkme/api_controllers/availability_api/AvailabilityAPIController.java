package com.example.chiilek.parkme.api_controllers.availability_api;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.chiilek.parkme.data_classes.availability_classes.Envelope;

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
    AvailabilityCallback mavailabilityCallback;

    public void makeCall(AvailabilityCallback availCallback){
        mavailabilityCallback = availCallback;
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        CarParkAvailabilityAPI carParkAvailabilityAPI = retrofit.create(CarParkAvailabilityAPI.class);
        Log.d("AvailabilityAPIController", "GET call URL: " + BASE_URL + "carpark-availability?date_time=" + formatDateTime());
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
        Log.d("AvailabilityAPIController","On Response from call, response code: " + Integer.toString(response.code()));
        Envelope envelope = response.body();

        if (envelope != null){
            Log.d("AvailabilityAPIController", "Envelope is not null");

            if (envelope.getItem() != null){
                mavailabilityCallback.onSuccess(envelope.getItem());
                //APIRepository.setCarParkList(envelope.getItem().getCarParkData());
                Log.d("AvailabilityAPIController","Printing out details of first Item in Envelope from availability API call.\n" +
                        "UpdateDateTime: " + envelope.getItem().getTimestamp() + "\n" +
                        "CarParkNumber:  " + envelope.getItem().getCarParkData().get(0).getCarParkNumber()+ "\n" +
                        "Available Lots: " + Integer.toString(envelope.getItem().getCarParkData().get(0).getCarParkInfo().get(0).getLotsAvailable()) + "\n" +
                        "Total Lots:     " + Integer.toString(envelope.getItem().getCarParkData().get(0).getCarParkInfo().get(0).getTotalLots())+ "\n" +
                        "Lots Type:      " + Character.toString(envelope.getItem().getCarParkData().get(0).getCarParkInfo().get(0).getLotType()));
            } else {
                Log.d("AvailabilityAPIController", "No Item in Envelope; No object pulled: " + response.body().toString());
                mavailabilityCallback.onFailure();
            }
        }
        else {
            Log.d("AvailabilityAPIController", " Envelope is Null; No object pulled: " + response.toString());
            mavailabilityCallback.onFailure();
        }
    }

    @Override
    public void onFailure(@NonNull Call<Envelope> call, @NonNull Throwable t) {
        Log.d("AvailabilityAPIController", "onFailure, error message: " + t.getMessage());
        mavailabilityCallback.onFailure();
        t.getStackTrace();
    }
}
