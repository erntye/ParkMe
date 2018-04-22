package com.example.chiilek.parkme.entity;

import android.util.Log;

import com.example.chiilek.parkme.entity.availabilityapi.CarParkDatum;
import com.example.chiilek.parkme.entity.directionsapi.GoogleMapsDirections;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.io.Serializable;

/**
 * Wrapper class which combines responses from Directions API, Availability API, and HDB Car Park Information Data:
 * <code>GoogleMapsDirections</code>, <code>CarParkDatum</code>, and <code>CarParkInfo</code>
 * Also contains scores assigned to each object for distance, trip duration, and availability, as well as the overall score.
 */
public class DirectionsAndCPInfo implements Serializable{
    private CarParkInfo carParkInfo;
    private GoogleMapsDirections googleMapsDirections;
    private CarParkDatum carParkDatum;
    private double distance;
    private int duration;
    private int availability = -1;
    private double distanceScore = 0;
    private double durationScore = 0;
    private double availabilityScore = 0;
    private double distanceScoreWeight = 0.25;
    private double durationScoreWeight = 0.25;
    private double availabilityScoreWeight = 0.50;

    /**
     * Constructor which takes in the responses from:
     * Directions API, and HDB Car Park Information Data.
     * @param cpInfo <code>CarParkInfo</code> object created from HDB Car Park Information Data
     * @param gmapsDir <code>GoogleMapsDirections</code> object created from the Directions API call.
     * @param userChosenDestination
     */
    public DirectionsAndCPInfo(CarParkInfo cpInfo, GoogleMapsDirections gmapsDir, LatLng userChosenDestination){
        this.carParkInfo = cpInfo;
        this.googleMapsDirections = gmapsDir;
        Log.d("DirectionsAndCPInfo", "gmapsDir status "+ gmapsDir.getStatus() + ", gmapsDir routes size " + gmapsDir.getRoutes().size());
        distance = SphericalUtil.computeDistanceBetween(userChosenDestination,cpInfo.getLatLng());
        duration = gmapsDir.getRoutes().get(0).getLegs().get(0).getDuration().getValue();
    }


    /**
     * Calculates and returns the overall score based on the individual scores of distance, travel duration, and availability.
     * @return overall score as a <code>double</code>.
     */
    public double getOverallScore(){
        return distanceScoreWeight * distanceScore +
                durationScoreWeight * durationScore +
                availabilityScoreWeight * availabilityScore;
    }

    /**
     * Gets the <code>LatLng</code> of the car park this object contains.
     * @return
     */
    public LatLng getDestinationLatLng(){
        return carParkInfo.getLatLng();
    }

    /**
     * This method can optionally be used to set the <code>CarParkDatum</code> object which is created
     * from the Availability API call's response.
     * @param carParkDatum <code>CarParkDatum</code> object to be set.
     */
    public void setCarParkDatum(CarParkDatum carParkDatum) {
        this.carParkDatum = carParkDatum;
        this.availability = carParkDatum.getCarParkInfo().get(0).getLotsAvailable();
    }

    /**
     * Getter for <code>CarParkInfo</code>
     * @return <code>CarParkInfo</code> object store in this wrapper class
     */
    public CarParkInfo getCarParkInfo() { return carParkInfo; }

    /**
     * Getter for <code>GoogleMapsDirections</code>
     * @return <code>GoogleMapsDirections</code> object store in this wrapper class
     */
    public GoogleMapsDirections getGoogleMapsDirections() { return googleMapsDirections; }

    /**
     * Getter for <code>CarParkDatum</code>
     * @return <code>CarParkDatum</code> object store in this wrapper class
     */
    public CarParkDatum getCarParkDatum() { return carParkDatum; }

    /**
     * Gets the flying distance between the car park and the original chosen destination.
     * @return flying distance as a <code>double</code>.
     */
    public double getDistance(){ return distance; }

    /**
     * Gets the trip duration as calculated by Directions API from the <code>GoogleMapsDirections</code> object.
     * @return duration as a <code>double</code>.
     */
    public int getDuration(){ return duration; }

    /**
     * Gets the car park availability pulled from Availability API from the <code>CarParkDatum</code> object.
     * @return availability as a <code>double</code>.
     */
    public int getAvailability() { return availability; }

    /**
     * To assign a distance score for this <code>DirectionsAndCPInfo</code> object.
     * @param distanceScore <code>double</code> score for distance.
     */
    public void setDistanceScore(double distanceScore) { this.distanceScore = distanceScore; }

    /**
     * To assign a duration score for this <code>DirectionsAndCPInfo</code> object.
     * @param durationScore <code>double</code> score for trip duration.
     */
    public void setDurationScore(double durationScore) { this.durationScore = durationScore; }

    /**
     * To assign a availability score for this <code>DirectionsAndCPInfo</code> object.
     * @param availabilityScore <code>double</code> score for availability.
     */
    public void setAvailabilityScore(double availabilityScore) { this.availabilityScore = availabilityScore; }
}
