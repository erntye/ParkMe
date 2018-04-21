package com.example.chiilek.parkme.repository.callbacks;

public interface AvailabilityCountCallback {
    void onSuccess(int availabilityCount);
    void onFailure();
}
