package com.example.android.bookstudyplanner.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

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
    private Integer fromPageNb;
    private Integer toPageNb;
    private Integer nbPagesToRead;
    private Date beginDate;
    private Date endDate;
    private String weekPlanning;
    private Integer nbPagesRead;
    private Integer readTimeInSeconds;
    private Integer nbSecondsByPage;

    @Ignore
    private float percentRead;


    public BookEntity(int id, int isbn, String title, int pageCount, Integer fromPageNb, Integer toPageNb, Integer nbPagesToRead, Date beginDate, Date endDate, String weekPlanning, Integer nbPagesRead, Integer readTimeInSeconds, Integer nbSecondsByPage) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.pageCount = pageCount;
        this.fromPageNb = fromPageNb;
        this.toPageNb = toPageNb;
        this.nbPagesToRead = nbPagesToRead;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.weekPlanning = weekPlanning;
        this.nbPagesRead = nbPagesRead;
        this.readTimeInSeconds = readTimeInSeconds;
        this.nbSecondsByPage = nbSecondsByPage;
    }
    @Ignore
    public BookEntity(int isbn, String title, int pageCount, Integer fromPageNb, Integer toPageNb, Integer nbPagesToRead, Date beginDate, Date endDate, String weekPlanning, Integer nbPagesRead, Integer readTimeInSeconds, Integer nbSecondsByPage) {
        this.isbn = isbn;
        this.title = title;
        this.pageCount = pageCount;
        this.fromPageNb = fromPageNb;
        this.toPageNb = toPageNb;
        this.nbPagesToRead = nbPagesToRead;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.weekPlanning = weekPlanning;
        this.nbPagesRead = nbPagesRead;
        this.readTimeInSeconds = readTimeInSeconds;
        this.nbSecondsByPage = nbSecondsByPage;
    }

    @Ignore
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
        this.fromPageNb = (Integer)parcel.readSerializable();
        this.toPageNb = (Integer)parcel.readSerializable();
        this.nbPagesToRead = (Integer)parcel.readSerializable();
        Long begin = (Long) parcel.readSerializable();
        if (begin == null) {
            this.beginDate = null;
        } else {
            this.beginDate = new Date(begin);
        }
        Long end = (Long) parcel.readSerializable();
        if (end == null) {
            this.endDate = null;
        } else {
            this.endDate = new Date(end);
        }
        this.weekPlanning = (String)parcel.readSerializable();
        this.nbPagesRead = (Integer)parcel.readSerializable();
        this.readTimeInSeconds = (Integer)parcel.readSerializable();
        this.nbSecondsByPage = (Integer)parcel.readSerializable();
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
        dest.writeSerializable(fromPageNb);
        dest.writeSerializable(toPageNb);
        dest.writeSerializable(nbPagesToRead);
        if(beginDate != null) {
            dest.writeSerializable(beginDate.getTime());
        } else {
            dest.writeSerializable(null);
        }
        if(endDate != null) {
            dest.writeSerializable(endDate.getTime());
        } else {
            dest.writeSerializable(null);
        }
        dest.writeSerializable(weekPlanning);
        dest.writeSerializable(nbPagesRead);
        dest.writeSerializable(readTimeInSeconds);
        dest.writeSerializable(nbSecondsByPage);
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

    public Integer getFromPageNb() {
        return fromPageNb;
    }

    public void setFromPageNb(Integer fromPageNb) {
        this.fromPageNb = fromPageNb;
    }

    public Integer getToPageNb() {
        return toPageNb;
    }

    public void setToPageNb(Integer toPageNb) {
        this.toPageNb = toPageNb;
    }

    public Integer getNbPagesToRead() {
        return nbPagesToRead;
    }

    public void setNbPagesToRead(Integer nbPagesToRead) {
        this.nbPagesToRead = nbPagesToRead;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getWeekPlanning() {
        return weekPlanning;
    }

    public void setWeekPlanning(String weekPlanning) {
        this.weekPlanning = weekPlanning;
    }

    public Integer getNbPagesRead() {
        return nbPagesRead;
    }

    public void setNbPagesRead(Integer nbPagesRead) {
        this.nbPagesRead = nbPagesRead;
    }

    public Integer getReadTimeInSeconds() {
        return readTimeInSeconds;
    }

    public void setReadTimeInSeconds(Integer readTimeInSeconds) {
        this.readTimeInSeconds = readTimeInSeconds;
    }

    public Integer getNbSecondsByPage() {
        return nbSecondsByPage;
    }

    public void setNbSecondsByPage(Integer nbSecondsByPage) {
        this.nbSecondsByPage = nbSecondsByPage;
    }

    public float getPercentRead() {
        if(nbPagesToRead>0) {
            return nbPagesRead / nbPagesToRead * 100;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "BookEntity{" +
                "title='" + title + '\'' +
                ", pageCount=" + pageCount +
                ", fromPageNb=" + fromPageNb +
                ", toPageNb=" + toPageNb +
                ", nbPagesToRead=" + nbPagesToRead +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                ", weekPlanning='" + weekPlanning + '\'' +
                '}';
    }
}
