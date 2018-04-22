package com.example.chiilek.parkme.repository.callbacks;

/**
 * Interface for callbacks to get back list of car parks near the vicinity of a location,
 * and when background asynchronous calls are required.
 */
public interface SearchNearbyCallback {
    void onSuccess();
    void onFailure();
}
