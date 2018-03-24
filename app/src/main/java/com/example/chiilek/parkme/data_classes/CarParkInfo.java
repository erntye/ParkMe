package com.example.chiilek.parkme.data_classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by chiilek on 24/3/2018.
 */

public class CarParkInfo {

    @SerializedName("total_lots")
    @Expose
    private String totalLots;

    @SerializedName("lot_type")
    @Expose
    private String lotType;

    @SerializedName("lots_available")
    @Expose
    private String lotsAvailable;

    public String getTotalLots() {
        return totalLots;
    }

    public String getLotType() {
        return lotType;
    }

    public String getLotsAvailable() {
        return lotsAvailable;
    }
}
