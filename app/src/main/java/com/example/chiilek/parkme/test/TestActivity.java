package com.example.chiilek.parkme.test;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.chiilek.parkme.R;

import java.util.List;


public class TestActivity extends AppCompatActivity {

    private static Button button;
    private static Button initbutton;
    private static EditText textInput;
    private static TestViewModel model;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testlayout);
        textInput = findViewById(R.id.location);
        button = findViewById(R.id.button2);
        initbutton = findViewById(R.id.button3);

        button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        //model.setData(Integer.getInteger(textInput.getText().toString()));
                        button.setText(textInput.getText());
                    }
                });
        //Create a view model and allow re-created activities to get the same view model instance
        model = ViewModelProviders.of(this).get(TestViewModel.class);

        initbutton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        model.initialize();
                        initbutton.setText("Done!");
                    }
                });
        model.getList().observe(this, new Observer<List<TestEntity>>() {
            @Override
            public void onChanged(List<TestEntity> newTerm) {
                textInput.setText(newTerm.get(1).name);
            }
        });

    }
}
