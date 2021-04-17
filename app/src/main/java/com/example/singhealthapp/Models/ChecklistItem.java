package com.example.singhealthapp.Models;

import android.graphics.Bitmap;

public class ChecklistItem {

    private String statement, remarks;
    private Bitmap imageBitmap;
    private boolean photoTaken;
    private boolean isStatusSet;

    public boolean isNA() {
        return isNA;
    }

    public void setNA(boolean NA) {
        isNA = NA;
    }

    private boolean isNA;

    private boolean isCase = false;

    public ChecklistItem(String statement, String remarks) {
        this.statement = statement;
        this.remarks = remarks;
        this.imageBitmap = null;
        this.photoTaken = this.isStatusSet = false;
    }

    public boolean isStatusSet() {
        return isStatusSet;
    }

    public void setStatusSet(boolean statusSet) {
        isStatusSet = statusSet;
    }

    public boolean isPhotoTaken() {
        return photoTaken;
    }

    public void setPhotoTaken(boolean photoTaken) {
        this.photoTaken = photoTaken;
    }

    public boolean isCase() {
        return isCase;
    }

    public void setCase(boolean set) {
        isCase = set;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }
}
