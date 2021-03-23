package com.example.singhealthapp.Models;

import android.graphics.Bitmap;

public class Checklist_item {

    private String statement;
    private String remarks;
    private Bitmap imageBitmap;

    public Checklist_item(String statement, String remarks) {
        this.statement = statement;
        this.remarks = remarks;
        this.imageBitmap = null;
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
