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
     * @param xCoord X Coordinate or Longitude Coordinate
     * @param yCoord Y Coordinate or Latitude Coordinate
     * @param isXY Boolean. True if coordinates are in XY, false if in lon-lat
     */
    public Location(double xCoord, double yCoord, boolean isXY){
        if(isXY){
            convertAndSetCoords(xCoord, yCoord);
        }else{
            this.longitude = xCoord;
            this.latitude = yCoord;
        }
    }

    private void convertAndSetCoords(double xCoord, double yCoord) {
        //convert xy coords to lon-lat

    }

    public double getLongitude(){ return longitude; }
    public double getLatitude(){ return latitude; }
}
