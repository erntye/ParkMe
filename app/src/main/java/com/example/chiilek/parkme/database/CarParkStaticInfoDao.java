package com.example.chiilek.parkme.database;

import java.util.List;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.chiilek.parkme.entity.CarParkInfo;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Data Access Object which defines SQL Queries used to access and update objects in the db file.
 */

@Dao
public interface CarParkStaticInfoDao {

    @Query("SELECT * FROM CarParkInfo")
    List<CarParkInfo> getAll();

    @Query("SELECT * FROM CarParkInfo " +
            "WHERE (CAST (latitude AS REAL) BETWEEN (:latitude-0.004) AND (:latitude+0.004)) " +
            "AND (CAST (longitude AS REAL) BETWEEN (:longitude-0.004) AND (:longitude+0.004))")
    List<CarParkInfo> getNearestCarParks(double latitude, double longitude);

    @Query("SELECT * FROM CarParkInfo WHERE car_park_no = :carParkName")
    CarParkInfo getCarParkByID(String carParkName);

    @Update(onConflict = REPLACE)
    void updateAll(List<CarParkInfo> cpStaticInfo);
}
