package com.example.chiilek.parkme.data_classes.availability_classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chiilek
 * @since 24/3/2018.
 * This class contains this portion of the JSON file:
 *
 *  {
 *      "carpark_info": [
 *          {
 *              "total_lots": "104",
 *              "lot_type": "C",
 *              "lots_available": "0"
 *          }
 *      ],
 *      "carpark_number": "HE12",
 *      "update_datetime": "2018-02-12T08:57:48"
 *  }
 *
 * Contains only get methods.
 *
 */

public class CarParkDatum {

    //for generating default objects
    public CarParkDatum(){
        carParkInfo = new ArrayList<CarParkInfo>();
        carParkInfo.add(new CarParkInfo());
        carParkNumber = "Unavailable";
        updateDatetime = "Unavailable";
    }

    @SerializedName("carpark_info")
    @Expose
    private List<CarParkInfo> carParkInfo = null;

    @SerializedName("carpark_number")
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
