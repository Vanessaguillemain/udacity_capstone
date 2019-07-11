package com.example.android.bookstudyplanner.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vanessa on 10/07/2019.
 */
@Entity (tableName = "book")
public class BookEntity implements Parcelable   {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int isbn;
    private String title;
    private int pageCount;

    public BookEntity(int isbn, String title, int pageCount) {
        this.isbn = isbn;
        this.title = title;
        this.pageCount = pageCount;
    }

    //Parcelable Part
    public BookEntity(Parcel parcel) {
        this.id = parcel.readInt();
        this.isbn = parcel.readInt();
        this.title = parcel.readString();
        this.pageCount = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(isbn);
        dest.writeString(title);
        dest.writeInt(pageCount);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<BookEntity>() {
        @Override
        public BookEntity createFromParcel(Parcel parcel) {
            return new BookEntity(parcel);
        }

        @Override
        public BookEntity[] newArray(int i) {
            return new BookEntity[i];
        }
    };

    //Getter and setter Part
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsbn() {
        return isbn;
    }

    public void setIsbn(int isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    @Override
    public String toString() {
        return "BookEntity{" +
                "id=" + id +
                ", isbn=" + isbn +
                ", title='" + title + '\'' +
                ", pageCount=" + pageCount +
                '}';
    }
}
