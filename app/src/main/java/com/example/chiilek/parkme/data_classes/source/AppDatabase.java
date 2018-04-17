package com.example.chiilek.parkme.data_classes.source;

import android.arch.persistence.room.Database;
import android.content.Context;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
import com.example.chiilek.parkme.sqlAsset.AssetSQLiteOpenHelperFactory;

/**
 * Created by QuekYufei on 27/3/18.
 */

@Database(entities = {CarParkStaticInfo.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract CarParkStaticInfoDao CPInfoDao();

    private static final Object sLock = new Object();

    public static AppDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "ParkMeWithDiffLots.db")
                        .openHelperFactory(new AssetSQLiteOpenHelperFactory())
                        .allowMainThreadQueries()
                        .build();
            }
            return INSTANCE;
        }
    }
}
