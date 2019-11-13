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

@Database(entities = {BookEntity.class, PlanningEntity.class}, version = 10, exportSchema = false)
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
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5,
                                MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract BookDao bookDao();
    public abstract PlanningDao planningDao();

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
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

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE planning (date INTEGER NOT NULL, bookId INTEGER NOT NULL, done INTEGER NOT NULL, PRIMARY KEY(date, bookId))");
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE planning ADD COLUMN nbPagesToRead INTEGER DEFAULT -1 NOT NULL");
            database.execSQL("ALTER TABLE planning ADD COLUMN nbMinutesReading INTEGER");
        }
    };

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE planning ADD COLUMN title TEXT");
        }
    };

    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE book ADD COLUMN imageLink TEXT");
        }
    };

    private static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE planning ADD COLUMN imageLink TEXT");
        }
    };

    private static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE book ADD COLUMN percentRead REAL DEFAULT 0 NOT NULL");
        }
    };

    private static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE planning ADD COLUMN firstPage INTEGER DEFAULT -1 NOT NULL");
            database.execSQL("ALTER TABLE planning ADD COLUMN lastPage INTEGER DEFAULT -1 NOT NULL");
        }
    };

    private static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE book ADD COLUMN lastPageRead INTEGER DEFAULT -1 NOT NULL");
        }
    };

}
