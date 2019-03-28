package com.pole.krono.model;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

@Database(entities = {Sport.class, Profile.class, ActivityType.class, TrackingSession.class, Lap.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class DB extends RoomDatabase {

    private static volatile DB INSTANCE;

    static DB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DB.class, "krono_database")
                            .addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.d("Pole", "Db.onCreate");
            new PopulateDbAsync(INSTANCE).execute();
        }

        @Override
        public void onOpen (@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            Log.d("Pole", "DB.onOpen");
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final Dao mDao;

        PopulateDbAsync(DB db) {
            mDao = db.dao();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            mDao.insertSports(Sport.populate());
            mDao.insertActivities(ActivityType.populate());
            Log.v("Pole", "DB populated");

            return null;
        }
    }

    public abstract Dao dao();

}
