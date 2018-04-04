
package com.example.chiilek.parkme.data_classes.directions_classes;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GoogleMapsDirections {

    @SerializedName("geocoded_waypoints")
    @Expose
    private List<GeocodedWaypoint> geocodedWaypoints = null;
    @SerializedName("routes")
    @Expose
    private List<Route> routes = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<GeocodedWaypoint> getGeocodedWaypoints() {
        return geocodedWaypoints;
    }

    public void setGeocodedWaypoints(List<GeocodedWaypoint> geocodedWaypoints) {
        this.geocodedWaypoints = geocodedWaypoints;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PolylineOptions getPolylineOptions(){
        PolylineOptions polylineOptions = new PolylineOptions().clickable(true);
        List<Step> steps = routes.get(0).getLegs().get(0).getSteps();
        for(Step step : steps){
            polylineOptions.add(new LatLng(step.getStartLocation().getLat(), step.getStartLocation().getLng()));
        }
        return polylineOptions;
    }

}
