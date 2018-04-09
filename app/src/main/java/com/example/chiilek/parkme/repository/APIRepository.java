package com.example.chiilek.parkme.repository;


import com.example.chiilek.parkme.api_controllers.availability_api.AvailabilityAPIController;
import com.example.chiilek.parkme.api_controllers.availability_api.CarParkComparator;
import com.example.chiilek.parkme.data_classes.availability_classes.CarParkDatum;
import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;

import java.util.ArrayList;
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
        AvailabilityAPIController controller = new AvailabilityAPIController();
    }

    /**
     * Takes in a List<CarParkStaticInfo> of size
     * @param staticInfos
     * @return List<CarParkDatum>
     */
    public static List<CarParkDatum> getTopFiveRepository (List<CarParkStaticInfo> staticInfos) {

        List<CarParkDatum> top5Avail = new ArrayList<CarParkDatum>();

        for (CarParkStaticInfo si : staticInfos) {
            for(int i = 0; i < CarParkList.size(); i++){
                // Checks for similar carpark number.
                if (si.getCPNumber() == CarParkList.get(i).getCarParkNumber()){
                    top5Avail.add(CarParkList.get(i));
                    break;
                }
            }
        }

        // Sorting implemented at CarParkComparator class.
        // Compares and sorts based on absolute value of availability.
        Collections.sort(top5Avail, new CarParkComparator());

        // Checks that there are at least 5 carparks in the list.
        if(top5Avail.size() >= 5){
            return top5Avail.subList(0, 4);
        } else {
            return null;
        }
    }

}
