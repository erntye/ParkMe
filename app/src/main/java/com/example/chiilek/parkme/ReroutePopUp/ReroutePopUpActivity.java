package com.example.chiilek.parkme.ReroutePopUp;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chiilek.parkme.R;
import com.example.chiilek.parkme.data_classes.DirectionsAndCPInfo;
import com.example.chiilek.parkme.navigation.NavigationActivity;
import com.example.chiilek.parkme.navigation.RouteOverviewActivity;

/**
 * Created by DanSeb on 06/04/18.
 */

public class ReroutePopUpActivity extends AppCompatActivity {
    private DirectionsAndCPInfo alternativeRoute;
    private DirectionsAndCPInfo initialRoute;
    private TextView oldCarParkName, newCarParkName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent parentIntent = getIntent();
        alternativeRoute = (DirectionsAndCPInfo) parentIntent.getSerializableExtra("alternativeRoute");
        initialRoute = (DirectionsAndCPInfo) parentIntent.getSerializableExtra("initialRoute");
        setContentView(R.layout.popup_warning);
        getSupportActionBar().hide();
        //TODO Pass the choice over to navigation
        //this makes the SELECT button go to navigation
        ImageView rb = findViewById(R.id.rerouteButton);
        ImageView cancel = findViewById(R.id.cancelButton);
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ReroutePopUpActivity", "onClick: creating new nav Activity with alternative route");
                Intent intent = new Intent(ReroutePopUpActivity.this, NavigationActivity.class);
                intent.putExtra("chosenRoute",alternativeRoute);
                startActivity(intent);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        oldCarParkName = findViewById(R.id.pop_up_title_location);
        newCarParkName = findViewById(R.id.textView9);

        oldCarParkName.setText(initialRoute.getCarParkStaticInfo().getAddress());
        newCarParkName.setText("New CP: " + alternativeRoute.getCarParkStaticInfo().getAddress());

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.9), ActionBar.LayoutParams.WRAP_CONTENT);
    }

}