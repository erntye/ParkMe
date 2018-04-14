package com.example.chiilek.parkme.CarParkPopUp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chiilek.parkme.Suggestion.SuggestionsActivity;
import com.example.chiilek.parkme.R;
import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
import com.example.chiilek.parkme.navigation.RouteOverviewActivity;

import org.w3c.dom.Text;

/**
 * Created by DanSeb on 06/04/18.
 */

public class CarParkPopUpActivity extends AppCompatActivity {

    TextView titleText;

    TextView motorCarValue;
    TextView motorCycleValue;
    TextView heavyVehicleValue;

    TextView carParkTypeValue;
    TextView parkingSystemValue;
    TextView freeParkingValue;

    TextView shortTermParkingValue;
    TextView nightParkingValue;

    CarParkStaticInfo carParkStaticInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pop_info);
        getSupportActionBar().hide();
        //TODO Pass the choice over to navigation
        //this makes the SELECT button go to navigation
        ImageView b = findViewById(R.id.pop_up_select_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarParkPopUpActivity.this, RouteOverviewActivity.class);
                intent.putExtra("chosenCarPark",carParkStaticInfo);
                startActivity(intent);
            }
        });


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout(1000, 1500);
        Log.d("dimension", Integer.toString(dm.widthPixels) + Integer.toString(dm.heightPixels));

        carParkStaticInfo = (CarParkStaticInfo) getIntent().getSerializableExtra("CarParkStaticInfo");
        Log.d("PopUpActivity", "pop up carpark is "+ carParkStaticInfo.getCPNumber());


        titleText = findViewById(R.id.pop_up_title_location);

        motorCarValue = findViewById(R.id.pop_up_motor_car_value);
        motorCycleValue = findViewById(R.id.pop_up_motor_cycle_value);
        heavyVehicleValue = findViewById(R.id.pop_up_heavy_vehicle_value);

        carParkTypeValue = findViewById(R.id.pop_up_car_park_type_value);
        parkingSystemValue = findViewById(R.id.pop_up_parking_system_value);
        freeParkingValue = findViewById(R.id.pop_up_free_parking_value);

        shortTermParkingValue = findViewById(R.id.pop_up_short_term_parking_value);
        nightParkingValue = findViewById(R.id.pop_up_night_parking_value);

        if (carParkStaticInfo == null) {
            Log.d("CarParkPopUpActivity", "cpsi = null");
        } else {
            titleText.setText(carParkStaticInfo.getAddress());

            motorCarValue.setText(carParkStaticInfo.getAvailableCarLots());
            if(carParkStaticInfo.getAvailableMotorcycleLots() != null) {
                motorCycleValue.setText(carParkStaticInfo.getAvailableMotorcycleLots());
            }else motorCycleValue.setText("Not Available");

            if(carParkStaticInfo.getAvailableHeavyLots() != null) {
                heavyVehicleValue.setText(carParkStaticInfo.getAvailableHeavyLots());
            }else heavyVehicleValue.setText("Not Available");

            carParkTypeValue.setText(carParkStaticInfo.getCarParkType());
            parkingSystemValue.setText(carParkStaticInfo.getTypeOfParkingSystem());
            freeParkingValue.setText(carParkStaticInfo.getFreeParking());

            shortTermParkingValue.setText(carParkStaticInfo.getShortTermParking());
            nightParkingValue.setText(carParkStaticInfo.getNightParking());
            Log.d("CarParkPopUpActivity", "cpsi = not null");
        }
    }

}
