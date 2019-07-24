package com.example.android.bookstudyplanner.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.services.books.model.Volume;

/**
 * Created by vanessa on 23/07/2019.
 */

public class MyVolume implements Parcelable {
    private String id;
    private String volumeInfoTitle;
    private String volumeInfoSubtitle;
    private String volumeInfoImageLink;
    private Integer volumeInfoPageCount;

    // Constructor
    public MyVolume(Volume volume){
        Volume.VolumeInfo info = volume.getVolumeInfo();
        id = volume.getId();

        if(info != null) {
            this.volumeInfoTitle = info.getTitle();
            this.volumeInfoSubtitle = info.getSubtitle();
            this.volumeInfoPageCount = info.getPageCount();

            Volume.VolumeInfo.ImageLinks imageLinks = info.getImageLinks();
            if (imageLinks != null) {
                String medium = imageLinks.getMedium();
                String large = imageLinks.getLarge();
                String small = imageLinks.getSmall();
                String thumbnail = imageLinks.getThumbnail();
                String smallThumbnail = imageLinks.getSmallThumbnail();

                String imageLink = "";
                if (large != null) {
                    imageLink = large;
                } else if (medium != null) {
                    imageLink = medium;
                } else if (small != null) {
                    imageLink = small;
                } else if (thumbnail != null) {
                    imageLink = thumbnail;
                } else if (smallThumbnail != null) {
                    imageLink = smallThumbnail;
                }

                imageLink = imageLink.replace("edge=curl", "");
                this.volumeInfoImageLink = imageLink;
            }
        }
    }

    private MyVolume(Parcel in) {
        id = in.readString();
        volumeInfoTitle = in.readString();
        volumeInfoSubtitle = in.readString();
        volumeInfoImageLink = in.readString();
        volumeInfoPageCount = in.readInt();
    }

    // Getter and setter methods

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(volumeInfoTitle);
        dest.writeString(volumeInfoSubtitle);
        dest.writeString(volumeInfoImageLink);
        dest.writeInt(volumeInfoPageCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MyVolume createFromParcel(Parcel in) {
            return new MyVolume(in);
        }

        public MyVolume[] newArray(int size) {
            return new MyVolume[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVolumeInfoTitle() {
        return volumeInfoTitle;
    }

    public void setVolumeInfoTitle(String volumeInfoTitle) {
        this.volumeInfoTitle = volumeInfoTitle;
    }

    public String getVolumeInfoSubtitle() {
        return volumeInfoSubtitle;
    }

    public void setVolumeInfoSubtitle(String volumeInfoSubtitle) {
        this.volumeInfoSubtitle = volumeInfoSubtitle;
    }

    public String getVolumeInfoImageLink() {
        return volumeInfoImageLink;
    }

    public void setVolumeInfoImageLink(String volumeInfoImageLink) {
        this.volumeInfoImageLink = volumeInfoImageLink;
    }

    public Integer getVolumeInfoPageCount() {
        return volumeInfoPageCount;
    }

    public void setVolumeInfoPageCount(Integer volumeInfoPageCount) {
        this.volumeInfoPageCount = volumeInfoPageCount;
    }
}
