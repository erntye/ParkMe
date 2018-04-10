package com.example.chiilek.parkme.repository;

import com.example.chiilek.parkme.data_classes.DirectionsAndCPInfo;

import java.util.List;

public interface GetRoutesCallback {
    public void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList);
    public void onFailure();
}
