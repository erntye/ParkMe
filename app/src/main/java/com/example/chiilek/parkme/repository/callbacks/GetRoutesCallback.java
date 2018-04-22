package com.example.chiilek.parkme.repository.callbacks;

import com.example.chiilek.parkme.entity.DirectionsAndCPInfo;

import java.util.List;

/**
 * Interface for callbacks for calls to both Directions API and Availability API,
 * where <code>DirectionsAndCPInfo</code> objects are desired.
 */
public interface GetRoutesCallback {
    void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList);
    void onFailure(int errorCode);
}
