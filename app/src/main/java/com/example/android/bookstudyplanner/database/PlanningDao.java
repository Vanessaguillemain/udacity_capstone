package com.example.android.bookstudyplanner.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

/**
 * Created by vanessa on 10/07/2019.
 */

@Dao
public interface PlanningDao {

    @Query("SELECT * FROM planning WHERE date = :date")
    LiveData<List<PlanningEntity>> loadAllBooksForDate(Date date);

    @Query("SELECT * FROM planning WHERE bookId = :bookId")
    LiveData<List<PlanningEntity>> loadAllDatesForBook(int bookId);

    @Insert
    void insertPlanning(PlanningEntity planningEntity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updatePlanning(PlanningEntity planningEntity);

    @Delete
    void deletePlanning(PlanningEntity planningEntity);

    @Query("DELETE FROM planning WHERE bookId = :bookId AND date >= :date")
    void deletePlanningByBookIdAfterIncludeDate(int bookId, Date date);

    @Query("DELETE FROM planning WHERE bookId = :bookId")
    void deletePlanningByBookId(int bookId);

    @Query("DELETE FROM planning WHERE date = :date")
    void deletePlanningByDate(Date date);

}