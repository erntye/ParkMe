package com.example.chiilek.parkme.api.availability_api;

import com.example.chiilek.parkme.entity.availabilityapi.Item;

/**
 * Interface for callbacks related to Availability API calls
 */
public interface AvailabilityCallback {
    void onSuccess(Item cpAPIItem);
    void onFailure();
}
