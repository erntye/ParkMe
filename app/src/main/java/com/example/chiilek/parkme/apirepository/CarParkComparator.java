package com.example.chiilek.parkme.apirepository;


import com.example.chiilek.parkme.data_classes.CarParkDatum;

import java.util.Comparator;

/**
 * @author chiilek
 * @since 27/3/2018.
 */

public class CarParkComparator implements Comparator<CarParkDatum> {
    @Override
    public int compare(CarParkDatum cp1, CarParkDatum cp2) {

        return cp1.getCarParkInfo().get(0).getLotsAvailable() - cp2.getCarParkInfo().get(0).getLotsAvailable();
    }
}
