package com.example.chiilek.parkme.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import com.example.chiilek.parkme.entity.availabilityapi.CarParkDatum;

import java.io.Serializable;

/**
 * Created by QuekYufei on 27/3/18.
 */

@Entity(tableName = "CarParkInfo")
public class CarParkInfo implements Serializable{

    public CarParkInfo(String CPNumber){
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

    @ColumnInfo(name="last_update_datetime")
    private String lastUpdateDatetime;

    @ColumnInfo(name="available_car_lots")
    private String availableCarLots;

    @ColumnInfo(name="total_car_lots")
    private String totalCarLots;

    @ColumnInfo(name="available_motorcycle_lots")
    private String availableMotorcycleLots;

    @ColumnInfo(name="total_motorcycle_lots")
    private String totalMotorcycleLots;

    @ColumnInfo(name="available_heavy_lots")
    private String availableHeavyLots;

    @ColumnInfo(name="total_heavy_lots")
    private String totalHeavyLots;

    public void setAvailInfo(CarParkDatum cpDatum) {
        setLastUpdateDatetime(cpDatum.getUpdateDatetime());
        setTotalCarLots(Integer.toString(cpDatum.getCarParkInfo().get(0).getTotalLots()));
        setAvailableCarLots(Integer.toString(cpDatum.getCarParkInfo().get(0).getLotsAvailable()));
        if (cpDatum.getCarParkInfo().size() > 1) {
            setTotalMotorcycleLots(Integer.toString(cpDatum.getCarParkInfo().get(1).getTotalLots()));
            setAvailableMotorcycleLots(Integer.toString(cpDatum.getCarParkInfo().get(1).getLotsAvailable()));
        }
        if (cpDatum.getCarParkInfo().size() > 2) {
            setTotalHeavyLots(Integer.toString(cpDatum.getCarParkInfo().get(2).getTotalLots()));
            setAvailableHeavyLots(Integer.toString(cpDatum.getCarParkInfo().get(2).getLotsAvailable()));
        }
    }

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

    public LatLng getLatLng(){return new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));}

    public String getAvailableCarLots() { return availableCarLots; }

    public void setAvailableCarLots(String availableCarLots) { this.availableCarLots = availableCarLots; }

    public String getTotalCarLots() { return totalCarLots; }

    public void setTotalCarLots(String totalCarLots) { this.totalCarLots = totalCarLots; }

    public String getLastUpdateDatetime() { return lastUpdateDatetime; }

    public void setLastUpdateDatetime(String lastUpdateDatetime) { this.lastUpdateDatetime = lastUpdateDatetime; }

    public String getAvailableMotorcycleLots() { return availableMotorcycleLots; }

    public void setAvailableMotorcycleLots(String availableMotorcycleLots) { this.availableMotorcycleLots = availableMotorcycleLots; }

    public String getTotalMotorcycleLots() { return totalMotorcycleLots; }

    public void setTotalMotorcycleLots(String totalMotorcycleLots) { this.totalMotorcycleLots = totalMotorcycleLots; }

    public String getAvailableHeavyLots() { return availableHeavyLots; }

    public void setAvailableHeavyLots(String availableHeavyLots) { this.availableHeavyLots = availableHeavyLots; }

    public String getTotalHeavyLots() { return totalHeavyLots; }

    public void setTotalHeavyLots(String totalHeavyLots) { this.totalHeavyLots = totalHeavyLots; }
}
