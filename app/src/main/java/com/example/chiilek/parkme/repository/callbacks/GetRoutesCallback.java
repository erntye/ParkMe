package com.example.chiilek.parkme.repository.callbacks;

import com.example.chiilek.parkme.entity.DirectionsAndCPInfo;

import java.util.List;

public interface GetRoutesCallback {
    public void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList);
    public void onFailure();
}
