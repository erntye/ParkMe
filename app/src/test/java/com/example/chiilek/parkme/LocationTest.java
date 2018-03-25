package com.example.chiilek.parkme;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by QuekYufei on 25/3/18.
 */
public class LocationTest {


    @Test
    private void testConvertAndSet(){
        double xCoord = 41494.2122;
        double yCoord = 37985.4918;
        double delta = 0.00001;
        Location location = new Location(xCoord, yCoord, true);
        assertEquals("Testing XY to lon-lat", 1.4282188, location.getLongitude(), delta);
    }
}