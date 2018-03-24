package com.example.chiilek.parkme.data_classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author chiilek
 * @since 24/3/2018.
 *
 * Upon unwrapping the main envelope. This is the item class that the API uses to define its response body.
 */

public class Item {
    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    @SerializedName("carpark_data")
    @Expose
    private List<CarParkDatum> carParkData = null;

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setCarParkData(List<CarParkDatum> carParkData) {
        this.carParkData = carParkData;
    }

    public List<CarParkDatum> getCarParkData() {

        return carParkData;
    }

}
