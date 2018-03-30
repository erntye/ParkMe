package com.example.chiilek.parkme.test;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.chiilek.parkme.data_classes.source.AppDatabase;

/**
 * Created by Ernest on 28/3/2018.
 */

@Database(entities = {TestEntity.class}, version = 1,exportSchema = false)
public abstract class TestDatabase extends RoomDatabase {

    public abstract ModelDAO getModelDAO();
    private static TestDatabase INSTANCE;

    public static TestDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), TestDatabase.class, "test_db")
                            .build();
        }
        return INSTANCE;
    }



}
