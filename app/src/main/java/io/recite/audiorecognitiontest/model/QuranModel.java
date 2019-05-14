package io.recite.audiorecognitiontest.model;

/**
 * Created by johan on 27/7/2017.
 */

public class QuranModel {
//    private int databaseID;
    private String suraID;
//    private int verseID;
    private String ayah;

//    public int getdatabaseID() {
//        return databaseID;
//    }
//
//    public void setdatabaseID(int databaseID) {
//        databaseID = databaseID;
//    }

    public String getsuraID() {
        return suraID;
    }

    public void setsuraID(int suraID) {
        suraID = suraID;
    }
//
//    public int getverseID() {
//        return verseID;
//    }

//    public void setverseID(int verseID) {
//        verseID = verseID;
//    }

    public String getayah() {
        return ayah;
    }

    public void setayah(String ayahText) {
        ayah = ayahText;
    }
}
