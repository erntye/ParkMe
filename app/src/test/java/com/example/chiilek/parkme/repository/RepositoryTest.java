package com.example.chiilek.parkme.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.test.mock.MockContext;

import com.example.chiilek.parkme.data_classes.DirectionsAndCPInfo;
import com.example.chiilek.parkme.test.TestActivity;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RepositoryTest {

    @Test
    public void getDirectionsAndCPs() {
        Context context = mock(Context.class);
        Context context1 = new MockContext();

        when(context.getApplicationContext()).thenReturn(context);

        when(context.getApplicationInfo()).thenReturn(new ApplicationInfo(){
            public String dataDir = "/ParkMe/app/src/main/assets";
        });

        Repository repo = Repository.getInstance(context);

        LatLng startPoint = new LatLng(1.3010632720868323,103.85411269138322);
        LatLng destination = new LatLng(1.3690913121113275, 103.8340744788229);

        LiveData<List<DirectionsAndCPInfo>> liveList = repo.getDirectionsAndCPs(startPoint, destination);
        List<DirectionsAndCPInfo> nonLiveList = liveList.getValue();
        for(DirectionsAndCPInfo item : nonLiveList){
            System.out.println(item.getCarParkStaticInfo().getCPNumber() + " : " + Double.toString(item.getOverallScore()));
        }
    }
}