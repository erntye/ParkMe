
package com.example.chiilek.parkme.entity.directionsapi;

import java.io.Serializable;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.PolyUtil;

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

    public PolylineOptions getPolylineOptions(){
        List<LatLng> decodedPath = PolyUtil.decode(getRoutes().get(0).getOverviewPolyline().getPoints());
        PolylineOptions polylineOptions = new PolylineOptions().clickable(true);
        polylineOptions.addAll(decodedPath);
//        if(gMapsRoads.getSnappedPoints().size() != 0){
//            Log.d("GoogleMapDirections", "Polyline Options, snapped points size != 0");
//            for(SnappedPoint point : gMapsRoads.getSnappedPoints()){
//                polylineOptions.add(new LatLng(point.getLocation().getLatitude(), point.getLocation().getLongitude()));
//                Log.d("GoogleMapDirections", "Polyline Options, adding one lat long pair now.");
//            }
//        }else{
//            Log.d("GoogleMapDirections", "Polyline Options, snapped points size = 0");
//        }
        return polylineOptions;
    }
}