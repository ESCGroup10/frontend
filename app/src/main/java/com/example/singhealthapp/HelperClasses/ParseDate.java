package com.example.singhealthapp.HelperClasses;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ParseDate {

    public static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
        return dateFormat.format(new Date()).toLowerCase();
    }

    public static String getDay(String date) {
        return date.substring(0, 2);
    }

    public static String getMonth(String date) {
        return date.substring(3, 5);
    }

    public static String getYear(String date) {
        return date.substring(6, 10);
    }

    public static String getHours(String date) {
        return date.substring(11, 13);
    }

    public static String getMinutes(String date) {
        return date.substring(14, 16);
    }

    public static String getSeconds(String date) {
        return date.substring(17, 19);
    }

    public static String getAmOrPm(String date) {
        return date.substring(20);
    }
}
