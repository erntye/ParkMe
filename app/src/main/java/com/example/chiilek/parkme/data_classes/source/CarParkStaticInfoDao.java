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
    double xCoordOffset = 500;
    double yCoordOffset = 500;


    @Query("SELECT * FROM CarParkInfo")
    List<CarParkStaticInfo> getAll();

    @Query("SELECT * FROM CarParkInfo " +
            "WHERE (CAST (x_coord AS REAL) BETWEEN (:xCoord-:xCoordOffset) AND (:xCoord+:xCoordOffest)) " +
            "AND (CAST (y_coord AS REAL) BETWEEN (:yCoord-:yCoordOffset) AND (:yCoord+:yCoordOffset))")
    List<CarParkStaticInfo> getNearestCarParks(double xCoord, double yCoord);
}
