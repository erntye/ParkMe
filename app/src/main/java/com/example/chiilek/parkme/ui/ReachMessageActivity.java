package com.example.chiilek.parkme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.example.chiilek.parkme.R;

public class ReachMessageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup_reach_destination);
        getSupportActionBar().hide();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .85), (int) (height * .35));

        ImageView b = findViewById(R.id.pop_up_ok_button);
        b.setOnClickListener(v -> {
            Intent intent = new Intent(ReachMessageActivity.this, ViewMapActivity.class);
            Log.d("Reached", "Returning to ViewMapActivity");
            startActivity(intent);
        });
    }
}