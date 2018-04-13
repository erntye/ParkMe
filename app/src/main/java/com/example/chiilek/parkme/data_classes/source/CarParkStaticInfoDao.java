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

    @Query("SELECT * FROM CarParkInfo")
    List<CarParkStaticInfo> getAll();

    @Query("SELECT * FROM CarParkInfo " +
            "WHERE (CAST (latitude AS REAL) BETWEEN (:latitude-0.004) AND (:latitude+0.004)) " +
            "AND (CAST (longitude AS REAL) BETWEEN (:longitude-0.004) AND (:longitude+0.004))")
    List<CarParkStaticInfo> getNearestCarParks(double latitude, double longitude);

    @Query("SELECT * FROM CarParkInfo WHERE car_park_no = :carParkName")
    CarParkStaticInfo getCarParkByID(String carParkName);
}
