package com.example.chiilek.parkme;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class ViewMapActivity extends AppCompatActivity {

    private static Button button;
    private static EditText textInput;
    private static LiveData destination;
    com.example.chiilek.parkme.ViewMap.ViewMapViewModel model = ViewModelProviders.of(this).get(com.example.chiilek.parkme.ViewMap.ViewMapViewModel.class);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testlayout);
        textInput = findViewById(R.id.location);
        button = findViewById(R.id.button2);

        button.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        destination = (LiveData)textInput.getText();
                        button.setText(textInput.getText());
            }
        });
        //Create a view model and allow re-created activities to get the same view model instance

        final Observer<String> destinationObserver =
                new Observer<String>() {
                    @Override
                    public void onChanged(String newDestination) {
                        textInput.setText(newDestination);
                    }};
        //model.getDestination().observe(this, destinationObserver);

    }



}
