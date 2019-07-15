package com.example.android.bookstudyplanner.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by vanessa on 15/07/2019.
 */
@Entity(tableName = "planning", primaryKeys = {"date", "bookId"})
public class PlanningEntity {
    @NonNull
    private Date date;
    @NonNull
    private int bookId;
    @NonNull
    private boolean done;

    public PlanningEntity(Date date, int bookId, boolean done) {
        this.date = date;
        this.bookId = bookId;
        this.done = done;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
