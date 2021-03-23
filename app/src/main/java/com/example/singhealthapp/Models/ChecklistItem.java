package com.example.singhealthapp.Models;

public class ChecklistItem {

    private String statement;
    private String remarks;

    public ChecklistItem(String statement, String remarks) {
        this.statement = statement;
        this.remarks = remarks;
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
}
