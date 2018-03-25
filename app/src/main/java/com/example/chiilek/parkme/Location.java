package com.example.chiilek.parkme;


/**
 * Created by Quek Yufei on 25/3/18.
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
     * @param longitude Longitude Coordinate
     * @param latitude Latitude Coordinate
     */
    public Location(double longitude, double latitude){
            this.longitude = longitude;
            this.latitude = latitude;
    }

    public double getLongitude(){ return longitude; }
    public double getLatitude(){ return latitude; }
}
