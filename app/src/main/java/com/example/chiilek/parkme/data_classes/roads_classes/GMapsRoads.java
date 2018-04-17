
package com.example.chiilek.parkme.data_classes.roads_classes;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GMapsRoads implements Serializable{

    @SerializedName("snappedPoints")
    @Expose
    private List<SnappedPoint> snappedPoints = null;

    public List<SnappedPoint> getSnappedPoints() {
        return snappedPoints;
    }

    public void setSnappedPoints(List<SnappedPoint> snappedPoints) {
        this.snappedPoints = snappedPoints;
    }

}
