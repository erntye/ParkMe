package com.example.chiilek.parkme.repository.callbacks;

import com.example.chiilek.parkme.entity.DirectionsAndCPInfo;

import java.util.List;

public interface GetRoutesCallback {
    void onSuccess(List<DirectionsAndCPInfo> directionsAndCPInfoList);
    void onFailure(int errorCode);
}
