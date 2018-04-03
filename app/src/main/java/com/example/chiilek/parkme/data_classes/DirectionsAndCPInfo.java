package com.example.chiilek.parkme.data_classes;

import com.example.chiilek.parkme.data_classes.directions_classes.GoogleMapsDirections;

public class DirectionsAndCPInfo {
    private CarParkStaticInfo carParkStaticInfo;
    private GoogleMapsDirections googleMapsDirections;
    private double distance;
    private int duration;
    private double distanceScore = 0;
    private double durationScore = 0;
    private double distanceScoreWeigtage = 0.80;

    public DirectionsAndCPInfo(CarParkStaticInfo cpInfo, GoogleMapsDirections gmapsDir){
        this.carParkStaticInfo = cpInfo;
        this.googleMapsDirections = gmapsDir;
        distance = getDistanceFromLatLngInKm(gmapsDir.getRoutes().get(0).getLegs().get(0).getStartLocation().getLat(),
                gmapsDir.getRoutes().get(0).getLegs().get(0).getStartLocation().getLng(),
                Double.parseDouble(cpInfo.getLatitude()),
                Double.parseDouble(cpInfo.getLongitude()));
        duration = gmapsDir.getRoutes().get(0).getLegs().get(0).getDuration().getValue();
    }

    private double getDistanceFromLatLngInKm(double lat1, double lng1, double lat2, double lng2) {
        double radiusAtEquator = 6378; //in kilometers
        double dLat = degToRad(lat2-lat1);  // deg2rad below
        double dLng = degToRad(lng2-lng1);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(degToRad(lat1)) * Math.cos(degToRad(lat2)) *
                                Math.sin(dLng/2) * Math.sin(dLng/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = radiusAtEquator * c; // Distance in km
        return distance;
    }

    private double degToRad(double deg) {
        return deg * (Math.PI/180);
    }

    public double getScore(){
        return distanceScoreWeigtage*distanceScore + (1-distanceScoreWeigtage)*durationScore;
    }

    public CarParkStaticInfo getCarParkStaticInfo() { return carParkStaticInfo; }

    public GoogleMapsDirections getGoogleMapsDirections() { return googleMapsDirections; }

    public double getDistance(){ return distance; }

    public int getDuration(){ return duration; }

    public void setDistanceScore(double distanceScore) { this.distanceScore = distanceScore; }

    public void setDurationScore(double durationScore) { this.durationScore = durationScore; }
}
