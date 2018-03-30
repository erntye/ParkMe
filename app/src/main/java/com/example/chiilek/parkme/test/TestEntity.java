package com.example.chiilek.parkme.test;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

/**
 * Created by Ernest on 28/3/2018.
 */

@Entity(tableName = "TestEntity")
public class TestEntity {

    public TestEntity(int id, String name){
        this.id = id;
        this.name = name;
    }
    @ColumnInfo(name = "ID")
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "name")
    public String name;

}
