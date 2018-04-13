package com.example.chiilek.parkme.data_classes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by QuekYufei on 27/3/18.
 */

@Entity(tableName = "CarParkInfo")
public class CarParkStaticInfo implements Serializable{

    public CarParkStaticInfo(String CPNumber){
        this.CPNumber = CPNumber;
    }
    //declaring variables (columns in table)
    @PrimaryKey
    @NonNull
    @ColumnInfo(name="car_park_no")
    private String CPNumber;

    private String address;

    @ColumnInfo(name="x_coord")
    private String xCoord;

    @ColumnInfo(name="y_coord")
    private String yCoord;

    @ColumnInfo(name="car_park_type")
    private String carParkType;

    @ColumnInfo(name="type_of_parking_system")
    private String typeOfParkingSystem;

    @ColumnInfo(name="short_term_parking")
    private String shortTermParking;

    @ColumnInfo(name="free_parking")
    private String freeParking;

    @ColumnInfo(name="night_parking")
    private String nightParking;

    private String latitude;

    private String longitude;

    @ColumnInfo(name="available_car_lots")
    private String availableCarLots;

    @ColumnInfo(name="total_car_lots")
    private String totalCarLots;

    @ColumnInfo(name="last_update_datetime")
    private String lastUpdateDatetime;


    //Getters and Setters
    @NonNull
    public String getCPNumber() {
        return CPNumber;
    }

    public void setCPNumber(@NonNull String CPNumber) {
        this.CPNumber = CPNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getXCoord() {
        return xCoord;
    }

    public void setXCoord(String xCoord) {
        this.xCoord = xCoord;
    }

    public String getYCoord() {
        return yCoord;
    }

    public void setYCoord(String yCoord) {
        this.yCoord = yCoord;
    }

    public String getCarParkType() {
        return carParkType;
    }

    public void setCarParkType(String carParkType) {
        this.carParkType = carParkType;
    }

    public String getTypeOfParkingSystem() {
        return typeOfParkingSystem;
    }

    public void setTypeOfParkingSystem(String typeOfParkingSystem) { this.typeOfParkingSystem = typeOfParkingSystem; }

    public String getShortTermParking() {
        return shortTermParking;
    }

    public void setShortTermParking(String shortTermParking) { this.shortTermParking = shortTermParking; }

    public String getFreeParking() {
        return freeParking;
    }

    public void setFreeParking(String freeParking) {
        this.freeParking = freeParking;
    }

    public String getNightParking() {
        return nightParking;
    }

    public void setNightParking(String nightParking) {
        this.nightParking = nightParking;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAvailableCarLots() { return availableCarLots; }

    public void setAvailableCarLots(String availableCarLots) { this.availableCarLots = availableCarLots; }

    public String getTotalCarLots() { return totalCarLots; }

    public void setTotalCarLots(String totalCarLots) { this.totalCarLots = totalCarLots; }

    public String getLastUpdateDatetime() { return lastUpdateDatetime; }

    public void setLastUpdateDatetime(String lastUpdateDatetime) { this.lastUpdateDatetime = lastUpdateDatetime; }
}
