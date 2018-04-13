package com.example.chiilek.parkme.test;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ernest on 28/3/2018.
 */

@Entity(tableName = "TestEntity")
public class TestEntity implements Serializable{

    public TestEntity(int id, String name){
        this.id = id;
        this.name = name;
    }
    @ColumnInfo(name = "ID")
    @PrimaryKey
    @NonNull
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
