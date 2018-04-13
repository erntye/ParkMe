package com.example.chiilek.parkme.CarParkPopUp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chiilek.parkme.Suggestion.SuggestionsActivity;
import com.example.chiilek.parkme.R;
import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;

import org.w3c.dom.Text;

/**
 * Created by DanSeb on 06/04/18.
 */

public class CarParkPopUpActivity extends AppCompatActivity {

    TextView titleText;
    TextView addressText;

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
                startActivity(new Intent(CarParkPopUpActivity.this, SuggestionsActivity.class));
            }
        });


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.9), (int)(height*.75));

        carParkStaticInfo = (CarParkStaticInfo) getIntent().getSerializableExtra("CarParkStaticInfo");

        titleText = findViewById(R.id.pop_up_title_location);
        addressText = findViewById(R.id.pop_up_park_address);

        motorCarValue = findViewById(R.id.pop_up_motor_car_value);
        motorCycleValue = findViewById(R.id.pop_up_motor_cycle_value);
        heavyVehicleValue = findViewById(R.id.pop_up_heavy_vehicle_value);

        carParkTypeValue = findViewById(R.id.pop_up_car_park_type_value);
        parkingSystemValue = findViewById(R.id.pop_up_parking_system_value);
        freeParkingValue = findViewById(R.id.pop_up_free_parking_value);

        shortTermParkingValue = findViewById(R.id.pop_up_short_term_parking_value);
        nightParkingValue = findViewById(R.id.pop_up_night_parking_value);

        titleText.setText(carParkStaticInfo.getCPNumber());
        addressText.setText(carParkStaticInfo.getAddress());

        carParkTypeValue.setText(carParkStaticInfo.getCarParkType());
        parkingSystemValue.setText(carParkStaticInfo.getTypeOfParkingSystem());
        freeParkingValue.setText(carParkStaticInfo.getFreeParking());

        shortTermParkingValue.setText(carParkStaticInfo.getShortTermParking());
        nightParkingValue.setText(carParkStaticInfo.getNightParking());
    }

}
