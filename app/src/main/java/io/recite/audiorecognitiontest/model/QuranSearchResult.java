package io.recite.audiorecognitiontest.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by johan on 27/10/2017.
 */

public class QuranSearchResult implements Parcelable {

    private String verseName;
    private String ayah;


    protected QuranSearchResult(Parcel in) {
        verseName = in.readString();
        ayah = in.readString();
    }

    public static final Creator<QuranSearchResult> CREATOR = new Creator<QuranSearchResult>() {
        @Override
        public QuranSearchResult createFromParcel(Parcel in) {
            return new QuranSearchResult(in);
        }

        @Override
        public QuranSearchResult[] newArray(int size) {
            return new QuranSearchResult[size];
        }
    };

    public String getVerseName() {
        return verseName;
    }

    public void setVerseName(String verseName) {
        this.verseName = verseName;
    }

    public String getAyah() {
        return ayah;
    }

    public void setAyah(String ayah) {
        this.ayah = ayah;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(verseName);
        dest.writeString(ayah);
    }
}
