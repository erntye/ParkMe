package com.example.chiilek.parkme.test;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chiilek.parkme.CarParkPopUp.CarParkPopUpActivity;
import com.example.chiilek.parkme.ReroutePopUp.ReroutePopUpActivity ;
import com.example.chiilek.parkme.R;
import com.example.chiilek.parkme.repository.LocationRepository;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    private static Button button;
    private static Button initbutton;
    private static Button locationbutton;
    private static EditText textInput;
    private static TestViewModel model;
    private TestLocationManager testLocationManager;

    //testing to get location service
    LocationRepository mService;
    boolean mBound = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testlayout);
        textInput = findViewById(R.id.location);
        button = findViewById(R.id.button2);
        initbutton = findViewById(R.id.button3);
        locationbutton = findViewById(R.id.button4);
        checkLocationPermission();

        //Create a view model and allow re-created activities to get the same view model instance
        model = ViewModelProviders.of(this).get(TestViewModel.class);

        button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("Activity","Pressed Set Button");
                        model.setData(Integer.parseInt(textInput.getText().toString()));
                        Log.d("Activity","Data set to " + Integer.parseInt(textInput.getText().toString()));
                        button.setText(textInput.getText());
                        //startActivity(new Intent(TestActivity.this, CarParkPopUpActivity.class));
                    }
                });

        initbutton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                      //to initialize database if needed
                        model.initialize();
                        initbutton.setText("Done!");
                        Intent intent = new Intent(TestActivity.this,TestActivity2.class);
                        if (model.getList().getValue()!= null) {
                            Log.d("Activity","list size is " + model.getList().getValue().size());
                            intent.putExtra("TestEntity1", model.getList().getValue().get(0));
                            Log.d("Activity", "Pressed Init Button");
                            startActivity(intent);

                        }

/*                       //to test for mediator live data
                        Log.d("TestActivity", "clicked set location");
                        model.setCurrentLocation(new LatLng(1.3209983, 104.888));
                        */
                    }
                });

        model.getList().observe(this, new Observer<List<TestEntity>>() {
            @Override
            public void onChanged(List<TestEntity> newTerm) {
                Log.d("Activity","On Change Triggered");
                Log.d("Activity","newTerm is " + newTerm);
                if (newTerm.isEmpty())
                    textInput.setText("Database Empty!");
                else
                    textInput.setText(newTerm.get(0).name);
            }
        });

/*        model.getCurrentLocation().observe(this, newLocation ->
            {
                Log.d("Activity", "observed newlocation changed");
            });*/

        model.getMediator().observe(this,  newMediator ->
                Log.d("Activity","mediatorlivedata changed")
        );

        locationbutton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("Activity", "Pressed Location Button");
                        Location location = testLocationManager.getLastLocation();
                        if (location != null) {
                            Toast.makeText(getApplicationContext(), "latitude: " + location.getLatitude(), Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), "longitude: " + location.getLongitude(), Toast.LENGTH_LONG).show();

                        }
                    }
                });



        //DIRECTIONS API TEST START
//        Thread thread = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try  {
//                    LatLng origin = new LatLng(1.3010632720868323, 103.85411269138322);
//                    LatLng destination = new LatLng(1.3210042901028483, 103.88504719970231);
//
//                    System.out.println("test begins");
//                    AvailabilityAPIController controller = new AvailabilityAPIController();
//                    controller.callDirectionsAPI(origin, destination);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        thread.start();
        //DIRECTIONS API TEST END
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();
            Log.d("Activity","In Service Connection");
            if (name.endsWith("TestLocationManager")) {
                testLocationManager = ((TestLocationManager.LocationBinder) service).getService();
                testLocationManager.startLocationUpdate();
                mBound = true;
                Log.d("Activity","Location Update started");
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals("TestLocationManager")) {
                Log.d("Activity","Service disconnected");
                testLocationManager = null;
                mBound = false;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
//        Intent intent = new Intent(this,TestLocationManager.class);
//        //startService(intent);
//        bindService(intent, serviceConnection,Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unbindService(serviceConnection);
//        testLocationManager.stopLocationUpdates();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(TestActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "permission granted", Toast.LENGTH_LONG).show();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }
}


