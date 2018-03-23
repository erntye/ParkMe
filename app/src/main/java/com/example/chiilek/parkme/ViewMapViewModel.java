package com.example.chiilek.parkme;


public class ViewMapViewModel extends ViewModel{
    private LiveData<Destination> destination;

    public LiveData<Destination> getDestination() {
        if (destination == null) {
            destination = new LiveData<Destination>();
            loadDestination();
        }
        return destination;
    }

    private void loadDestination() {
        //get data from persistence
    }

}

