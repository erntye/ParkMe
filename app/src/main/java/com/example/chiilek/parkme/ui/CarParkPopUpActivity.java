package com.example.chiilek.parkme.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chiilek.parkme.R;
import com.example.chiilek.parkme.entity.CarParkInfo;

/**
 * Created by DanSeb on 06/04/18.
 */

public class CarParkPopUpActivity extends AppCompatActivity {


    // These are the fields that will be initialized in onCreate
    TextView titleText;

    TextView motorCarValue;
    TextView motorCycleValue;
    TextView heavyVehicleValue;

    TextView carParkTypeValue;
    TextView parkingSystemValue;
    TextView freeParkingValue;

    TextView shortTermParkingValue;
    TextView nightParkingValue;

    CarParkInfo carParkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pop_info);
        //noinspection ConstantConditions
        getSupportActionBar().hide();

        // Set fragment size.
        getWindow().setLayout(1200, ActionBar.LayoutParams.WRAP_CONTENT);

        // Retrieve the car park object from intent.
        carParkInfo = (CarParkInfo) getIntent().getSerializableExtra("CarParkInfo");
        Log.d("CarParkPopUpActivity", "pop up carpark is "+ carParkInfo.getCPNumber());

        // Assign the fields.
        assignFields();

        // Populate the text views.
        if (carParkInfo == null) {
            Log.d("CarParkPopUpActivity", "cpsi = null");
        } else {
            titleText.setText(carParkInfo.getAddress());

            motorCarValue.setText(carParkInfo.getAvailableCarLots());
            if(carParkInfo.getAvailableMotorcycleLots() != null) {
                motorCycleValue.setText(carParkInfo.getAvailableMotorcycleLots());
            }else motorCycleValue.setText("Not Available");

            if(carParkInfo.getAvailableHeavyLots() != null) {
                heavyVehicleValue.setText(carParkInfo.getAvailableHeavyLots());
            }else heavyVehicleValue.setText("Not Available");

            carParkTypeValue.setText(carParkInfo.getCarParkType());
            parkingSystemValue.setText(carParkInfo.getTypeOfParkingSystem());
            freeParkingValue.setText(carParkInfo.getFreeParking());

            shortTermParkingValue.setText(carParkInfo.getShortTermParking());
            nightParkingValue.setText(carParkInfo.getNightParking());
            Log.d("CarParkPopUpActivity", "cpsi = not null");
        }

        //this makes the SELECT button go to navigation
        ImageView b = findViewById(R.id.pop_up_select_button);
        b.setOnClickListener(v -> {
            Intent intent = new Intent(CarParkPopUpActivity.this, RouteOverviewActivity.class);
            intent.putExtra("chosenCarPark", carParkInfo);
            intent.putExtra("destinationAddress", carParkInfo.getAddress());
            startActivity(intent);
        });
    }

    private void assignFields(){
        titleText = findViewById(R.id.pop_up_title_location);

        motorCarValue = findViewById(R.id.pop_up_motor_car_value);
        motorCycleValue = findViewById(R.id.pop_up_motor_cycle_value);
        heavyVehicleValue = findViewById(R.id.pop_up_heavy_vehicle_value);

        carParkTypeValue = findViewById(R.id.pop_up_car_park_type_value);
        parkingSystemValue = findViewById(R.id.pop_up_parking_system_value);
        freeParkingValue = findViewById(R.id.pop_up_free_parking_value);

        shortTermParkingValue = findViewById(R.id.pop_up_short_term_parking_value);
        nightParkingValue = findViewById(R.id.pop_up_night_parking_value);
    }

}
