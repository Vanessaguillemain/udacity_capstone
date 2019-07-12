package com.example.android.bookstudyplanner.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.util.Log;

/**
 * Created by vanessa on 10/07/2019.
 */

@Database(entities = {BookEntity.class}, version = 2, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "books";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .addMigrations(MIGRATION_1_2)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract BookDao bookDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE book ADD COLUMN fromPageNb INTEGER");
            database.execSQL("ALTER TABLE book ADD COLUMN toPageNb INTEGER");
            database.execSQL("ALTER TABLE book ADD COLUMN nbPagesToRead INTEGER");
            database.execSQL("ALTER TABLE book ADD COLUMN beginDate INTEGER");
            database.execSQL("ALTER TABLE book ADD COLUMN endDate INTEGER");
            database.execSQL("ALTER TABLE book ADD COLUMN weekPlanning TEXT");
            database.execSQL("ALTER TABLE book ADD COLUMN nbPagesRead INTEGER");
            database.execSQL("ALTER TABLE book ADD COLUMN readTimeInSeconds INTEGER");
            database.execSQL("ALTER TABLE book ADD COLUMN nbSecondsByPage INTEGER");
        }
    };

}
