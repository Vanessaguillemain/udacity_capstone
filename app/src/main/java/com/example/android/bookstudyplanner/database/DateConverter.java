package com.example.android.bookstudyplanner.database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by vanessa on 10/07/2019.
 */

class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
