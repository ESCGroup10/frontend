package com.example.singhealthapp.HelperClasses;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateOperations {

    public static String getCurrentDatabaseDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(new Date());
    }

    public static String convertDatabaseDateToReadableDate(String date) {
        return date.substring(0, 10) + " " + date.substring(11, 19);
    }

}
