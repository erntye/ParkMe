package com.example.chiilek.parkme.apirepository;


import com.example.chiilek.parkme.data_classes.CarParkDatum;

import java.util.Collections;
import java.util.List;

/**
 * @author chiilek
 * @since 27/3/2018.
 *
 * This is the main class for API call.
 * There are several methods implemented here for main repository to call.
 */

public class APIRepository {

    private static List<CarParkDatum> CarParkList = null;

    public static List<CarParkDatum> getCarParkList() {
        return CarParkList;
    }

    public static void setCarParkList(List<CarParkDatum> carParkList) {
        CarParkList = carParkList;
    }

    public static void refreshCarParks () {
        APIController controller = new APIController();
        controller.makeCall();
    }

    public static List<CarParkDatum> getTopFiveRepository (List carParkList) {
        Collections.sort(carParkList, new CarParkComparator());
        return carParkList.subList(0, 4);
    }

}
