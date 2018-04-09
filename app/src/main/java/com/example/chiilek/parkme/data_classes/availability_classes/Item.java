package com.example.chiilek.parkme.data_classes.availability_classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author chiilek
 * @since 24/3/2018.
 *
 * This is the item class that the API uses to define its response body.
 *
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

    public CarParkDatum getCarParkDatum(String cpNumber){
        return carParkData.stream().filter(o -> o.getCarParkNumber().equals(cpNumber)).findFirst().get();
    }

}