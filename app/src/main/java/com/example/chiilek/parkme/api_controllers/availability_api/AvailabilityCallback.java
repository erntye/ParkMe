package com.example.chiilek.parkme.api_controllers.availability_api;

import com.example.chiilek.parkme.data_classes.availability_classes.Item;

import java.util.List;

public interface AvailabilityCallback {
    public void onSuccess(int index, Item cpAPIItem);
    public void onFailure();
}
