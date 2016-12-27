package com.example.dariuszn.lab2;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DariuszN on 04.12.2016.
 */

public class PostepInfo implements Parcelable {

    public int mPobranychBajtow;
    public int mRozmiar;
    public int mWynik;

    public int getmPobranychBajtow() {
        return mPobranychBajtow;
    }

    public int getmRozmiar() {
        return mRozmiar;
    }

    public int getmWynik() {
        return mWynik;
    }

    public PostepInfo() {
        mPobranychBajtow = 0;
        mRozmiar = 0;
        mWynik = 0;
    }

    public PostepInfo(Parcel paczka) {
        mPobranychBajtow = paczka.readInt();
        mRozmiar = paczka.readInt();
        mWynik = paczka.readInt();
    }

    public void setInfo(int mPobranychBajtow, int mRozmiar, int mWynik) {
        this.mPobranychBajtow = mPobranychBajtow;
        this.mRozmiar = mRozmiar;
        this.mWynik = mWynik;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mPobranychBajtow);
        parcel.writeInt(mRozmiar);
        parcel.writeInt(mWynik);
    }

    public static final Parcelable.Creator<PostepInfo> CREATOR =
            new Parcelable.Creator<PostepInfo>() {

                @Override
                public PostepInfo createFromParcel(Parcel parcel) {
                    return new PostepInfo(parcel);
                }

                @Override
                public PostepInfo[] newArray(int i) {
                    return new PostepInfo[i];
                }
            };
}
