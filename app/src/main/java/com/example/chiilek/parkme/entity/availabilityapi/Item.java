package com.example.chiilek.parkme.entity.availabilityapi;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author chiilek
 * @since 24/3/2018.
 *
 * Part of the response from the GET request from the Availability API call.
 * Contains list of <code>CarParkDatum</code> objects where information about availability can be found.
 * Houses a function to return a <code>CarParkDatum</code> object with a particular Car Park Number.
 * Contains only get methods.
 */

public class Item {
    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    @SerializedName("carpark_data")
    @Expose
    private List<CarParkDatum> carParkData = null;


    public String getTimestamp() {
        return timestamp;
    }

    public List<CarParkDatum> getCarParkData() {

        return carParkData;
    }

    /**
     * Iterates through the <code>List</code> of <code>CarParkDatam</code> objects to find the one
     * with a matching Car Park Number, which is a unique identifier of each car park.
     * @param cpNumber Unique Identifier of the car park.
     * @return <code>CarParkDatum</code> object which corresponds to Car Park Number requested. Returns null if the Car Park Number is not found.
     */
    public CarParkDatum getCarParkDatum(String cpNumber){
        Log.d("Item", "Size of response from availability API: " + carParkData.size());
        Log.d("Item", "Searching availability API response for this car park: " + cpNumber);
        return carParkData.stream().filter(o -> o.getCarParkNumber().equals(cpNumber)).findFirst().orElse(null);
    }

}