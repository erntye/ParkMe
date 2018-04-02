package com.example.chiilek.parkme;


/**
 * Created by Quek Yufei on 25/3/18.
 */


/**
 * Class is deprecated and will be removed in version 3.8.2
 */
public class Location {
    private double longitude;
    private double latitude;

    /**
     * Coordinates given in data.gov api is in the format (XY):
     *      EPSG:3414 (SVY21 / Singapore TM)
     * Lat/Long coordinates used by google maps (lon-lat):
     *      EPSG:4326 (WGS 84)
     * Coordinates should be converted before constructing Location objects
     * @param latitude Latitude Coordinate
     * @param longitude Longitude Coordinate
     */
    public Location(double latitude, double longitude){
            this.longitude = longitude;
            this.latitude = latitude;
    }

    public double getLongitude(){ return longitude; }
    public double getLatitude(){ return latitude; }
}
