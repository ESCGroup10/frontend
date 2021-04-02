package com.example.singhealthapp.Models;

import android.graphics.Bitmap;

public class ChecklistItem {

    private String statement;
    private String remarks;
    private Bitmap imageBitmap;
    private boolean photoTaken;

    private boolean isCase = false;

    public ChecklistItem(String statement, String remarks) {
        this.statement = statement;
        this.remarks = remarks;
        this.imageBitmap = null;
        this.photoTaken = false;
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

    public void setCase(boolean aCase) {
        isCase = aCase;
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
