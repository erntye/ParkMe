package com.example.chiilek.parkme.test;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.chiilek.parkme.R;

import java.util.ArrayList;
import java.util.List;

public class TestActivity2 extends AppCompatActivity {

    private static TextView text1;
    private static TextView text2;
    private static TextView text3;
    private static TestViewModel2 model;
    private static List<TestEntity> testList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        text1 = findViewById(R.id.textView1);
        text2 = findViewById(R.id.textView2);
        text3 = findViewById(R.id.textView3);

        Intent originalIntent = getIntent();
        testList.add((TestEntity) originalIntent.getSerializableExtra("TestEntity1"));
        testList.add((TestEntity) originalIntent.getSerializableExtra("TestEntity2"));
        testList.add((TestEntity) originalIntent.getSerializableExtra("TestEntity3"));

        model = ViewModelProviders
                .of(this, new TestViewModel2Factory(this.getApplication(),testList))
                .get( TestViewModel2.class);

        text1.setText(model.getList().get(0).getName());


    }
}
