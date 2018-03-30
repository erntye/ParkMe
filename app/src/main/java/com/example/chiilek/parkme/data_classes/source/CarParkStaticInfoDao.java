package com.example.chiilek.parkme.data_classes.source;

import java.util.List;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;

/**
 * Created by QuekYufei on 27/3/18.
 */

@Dao
public interface CarParkStaticInfoDao {
    double latitudeOffset = 0.005;
    double longitudeOffset = 0.005;


    @Query("SELECT * FROM CarParkInfo")
    List<CarParkStaticInfo> getAll();

    @Query("SELECT * FROM CarParkInfo " +
            "WHERE (CAST (latitude AS REAL) BETWEEN (:latitude-:latitudeOffset) AND (:latitude+:latitudeOffset)) " +
            "AND (CAST (longitude AS REAL) BETWEEN (:longitude-:longitudeOffset) AND (:longitude+:longitudeOffset))")
    List<CarParkStaticInfo> getNearestCarParks(double latitude, double longitude);

    @Query("SELECT * FROM CarParkInfo WHERE car_park_no = :carParkName")
    CarParkStaticInfo getCarParkByID(String carParkName);
}
