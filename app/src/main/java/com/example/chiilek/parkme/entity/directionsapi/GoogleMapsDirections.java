
package com.example.chiilek.parkme.entity.directionsapi;

import java.io.Serializable;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.PolyUtil;

/**
 * Main class created from response to GET request the Directions API call.
 * Contains information about the way points as <code>LatLng</code> objects which defines the route to navigate between
 * the start point and end point.
 * Contains function to decode the <code>Polyline</code> provided by the API call into the <code>LatLng</code> objects, used to
 * generate <code>Polyline</code> objects.
 */
public class GoogleMapsDirections implements Serializable{

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

    /**
     * Decodes the <code>Polyline</code> provided by the Directions API into <code>LatLng</code> objects.
     * Generates and returns a <code>PolylineOptions</code> object with the <code>LatLng</code> objects.
     * @return <code>PolylineOptions</code> object with the <code>LatLng</code> objects.
     */
    public PolylineOptions getPolylineOptions(){
        List<LatLng> decodedPath = PolyUtil.decode(getRoutes().get(0).getOverviewPolyline().getPoints());
        PolylineOptions polylineOptions = new PolylineOptions().clickable(true);
        polylineOptions.addAll(decodedPath);
        return polylineOptions;
    }

    /**
     * Decodes the <code>Polyline</code> provided by the Directions API into <code>LatLng</code> objects.
     * @return The decoded <code>LatLng</code> objects.
     */
    public List<LatLng> getPolyLinePoints(){
        List<LatLng> decodedPath = PolyUtil.decode(getRoutes().get(0).getOverviewPolyline().getPoints());
        return decodedPath;
    }
}