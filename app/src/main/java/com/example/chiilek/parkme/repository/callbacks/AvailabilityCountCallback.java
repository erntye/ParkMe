package com.example.chiilek.parkme.repository.callbacks;

/**
 * Interface for callbacks when App needs to check for availability count of a car park.
 */
public interface AvailabilityCountCallback {
    void onSuccess(int availabilityCount);
    void onFailure();
}
