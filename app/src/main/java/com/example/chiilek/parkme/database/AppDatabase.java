package com.example.chiilek.parkme.database;

import android.arch.persistence.room.Database;
import android.content.Context;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.example.chiilek.parkme.entity.CarParkInfo;
import com.example.chiilek.parkme.database.sqlAsset.AssetSQLiteOpenHelperFactory;

/**
 * Created by QuekYufei on 27/3/18.
 * Abstract class used to implement the <code>Room</code> database.
 * Makes use of the <code>sqlAsset</code> library provided online by Alberto Giunta at https://github.com/albertogiunta/sqliteAsset
 * to preload prepared SQLite database into room.
 */

@Database(entities = {CarParkInfo.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract CarParkStaticInfoDao CPInfoDao();

    private static final Object sLock = new Object();

    /**
     * Singleton Pattern implemented to get instance of <code>AppDatabase</code>.
     * @param context context of class which calls this function.
     * @return Singleton instance of <code>AppDatabase</code>.
     */
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
