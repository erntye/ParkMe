package com.example.chiilek.parkme.data_classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author chiilek
 * @since 24/3/2018.
 * This class contains this portion of the JSON file:
 *
 *  {
 *      "total_lots": "104",
 *      "lot_type": "C",
 *      "lots_available": "0"
 *  }
 *
 * Contains only get methods.
 */

public class CarParkInfo {

    @SerializedName("total_lots")
    @Expose
    private int totalLots;

    @SerializedName("lot_type")
    @Expose
    private char lotType;

    @SerializedName("lots_available")
    @Expose
    private int lotsAvailable;

    public int getTotalLots() {
        return totalLots;
    }

    public char getLotType() {
        return lotType;
    }

    public int getLotsAvailable() {
        return lotsAvailable;
    }
}
