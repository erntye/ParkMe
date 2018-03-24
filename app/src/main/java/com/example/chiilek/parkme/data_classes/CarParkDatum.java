package com.example.chiilek.parkme.data_classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by chiilek on 24/3/2018.
 */

public class CarParkDatum {

    @SerializedName("car_park_info")
    @Expose
    private List<CarParkInfo> carParkInfo = null;

    @SerializedName("car_park_number")
    @Expose
    private String carParkNumber;

    @SerializedName("update_datetime")
    @Expose
    private String updateDatetime;

    public List<CarParkInfo> getCarParkInfo() {
        return carParkInfo;
    }

    public String getCarParkNumber() {
        return carParkNumber;
    }

    public String getUpdateDatetime() {
        return updateDatetime;
    }
}
