package com.example.chiilek.parkme.apirepository;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import static org.junit.Assert.*;

public class APIControllerTest {

    @Test
    public void callDirectionsAPI() {
        LatLng origin = new LatLng(1.3010632720868323, 103.85411269138322);
        LatLng destination = new LatLng(1.3210042901028483, 103.88504719970231);

        System.out.println("test begins");
        APIController controller = new APIController();
        controller.callDirectionsAPI(origin, destination);
    }
}