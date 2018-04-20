package com.example.chiilek.parkme.api.availability_api;

import com.example.chiilek.parkme.entity.availabilityapi.Item;

public interface AvailabilityCallback {
    public void onSuccess(Item cpAPIItem);
    public void onFailure();
}
