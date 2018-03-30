package com.example.chiilek.parkme.test;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Ernest on 28/3/2018.
 */
@Dao
public interface ModelDAO {

    @Query("SELECT * FROM TestEntity")
    LiveData<List<TestEntity>> getAllModel();

    @Query("SELECT * FROM TestEntity WHERE id = :id")
    LiveData<List<TestEntity>> getModelById(int id);

    @Insert(onConflict = REPLACE)
    void save(TestEntity entity);
}
