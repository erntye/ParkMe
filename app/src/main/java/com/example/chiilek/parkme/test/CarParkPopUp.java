package com.example.chiilek.parkme.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.example.chiilek.parkme.R;

/**
 * Created by DanSeb on 06/04/18.
 */

class CarParkPopUp extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pop_info);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.8));

    }
}
