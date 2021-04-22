package com.example.singhealthapp.HelperClasses;

import android.util.Log;

import com.example.singhealthapp.Models.Report;
import com.example.singhealthapp.Models.ReportPreview;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DateOperations {

    private static final String TAG = "DateOperations";

    public static String getCurrentDatabaseDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(new Date());
    }

    public static String convertDatabaseDateToReadableDate(String date) {
        return date.substring(0, 10) + " " + date.substring(11, 19);
    }

    public static String convertDatabaseDateToReadableDateNoTime(String date) {
        return date.substring(0, 10);
    }

    /**
     * returns the more recent date based on the date format: yyyy-mm-dd hh:MM:ss
     * */
    public static String getMoreRecentDate(String date1, String date2) {
        // yyyy-mm-dd hh:MM:ss
//        Log.d(TAG, "getMoreRecentDate: year: "+date1.substring(0, 4)+" vs "+date2.substring(0, 4));
        if (Integer.parseInt(date1.substring(0, 4)) == Integer.parseInt(date2.substring(0, 4))) {
//            Log.d(TAG, "getMoreRecentDate: month: "+date1.substring(5, 7)+" vs "+date2.substring(5, 7));
            if (Integer.parseInt(date1.substring(5, 7)) == Integer.parseInt(date2.substring(5, 7))) {
//                Log.d(TAG, "getMoreRecentDate: day: "+date1.substring(8, 10)+" vs "+date2.substring(8, 10));
                if (Integer.parseInt(date1.substring(8, 10)) == Integer.parseInt(date2.substring(8, 10))) {
//                    Log.d(TAG, "getMoreRecentDate: hour: "+date1.substring(11, 13)+" vs "+date2.substring(11, 13));
                    if (Integer.parseInt(date1.substring(11, 13)) == Integer.parseInt(date2.substring(11, 13))) {
//                        Log.d(TAG, "getMoreRecentDate: min: "+date1.substring(14, 16)+" vs "+date2.substring(14, 16));
                        if (Integer.parseInt(date1.substring(14, 16)) == Integer.parseInt(date2.substring(14, 16))) {
//                            Log.d(TAG, "getMoreRecentDate: sec: "+date1.substring(17, 19)+" vs "+date2.substring(17, 19));
                            if (Integer.parseInt(date1.substring(17, 19)) == Integer.parseInt(date2.substring(17, 19))) {
                                return date1;

                            } else if (Integer.parseInt(date1.substring(17, 19)) > Integer.parseInt(date2.substring(17, 19))) {
//                                Log.d(TAG, "getMoreRecentDate: picking date1");
                                return date1;
                            } else {
//                                Log.d(TAG, "getMoreRecentDate: picking date2");
                                return date2;
                            }

                        } else if (Integer.parseInt(date1.substring(14, 16)) > Integer.parseInt(date2.substring(14, 16))) {
//                            Log.d(TAG, "getMoreRecentDate: picking date1");
                            return date1;
                        } else {
//                            Log.d(TAG, "getMoreRecentDate: picking date2");
                            return date2;
                        }

                    } else if (Integer.parseInt(date1.substring(11, 13)) > Integer.parseInt(date2.substring(11, 13))) {
//                        Log.d(TAG, "getMoreRecentDate: picking date1");
                        return date1;
                    } else {
//                        Log.d(TAG, "getMoreRecentDate: picking date2");
                        return date2;
                    }

                } else if (Integer.parseInt(date1.substring(8, 10)) > Integer.parseInt(date2.substring(8, 10))) {
//                    Log.d(TAG, "getMoreRecentDate: picking date1");
                    return date1;
                } else {
//                    Log.d(TAG, "getMoreRecentDate: picking date2");
                    return date2;
                }

            } else if (Integer.parseInt(date1.substring(5, 7)) > Integer.parseInt(date2.substring(5, 7))) {
//                Log.d(TAG, "getMoreRecentDate: picking date1");
                return date1;
            } else {
//                Log.d(TAG, "getMoreRecentDate: picking date2");
                return date2;
            }

        } else if (Integer.parseInt(date1.substring(0, 4)) > Integer.parseInt(date2.substring(0, 4))) {
//            Log.d(TAG, "getMoreRecentDate: picking date1");
            return date1;
        } else {
//            Log.d(TAG, "getMoreRecentDate: picking date2");
            return date2;
        }
    }

//    public static List<Report> organiseReportByDate(List<Report> reportList) {
//        // first is more recent
////        Report[] organisedReportList = new Report[reportList.size()];
//        /**
//         * Does not work, need to make deepcopy of list and remove the most recent report/make its date large after every loop
//         * */
//        Log.d(TAG, "organiseReportByDate: size: "+reportList.size());
//        List<Report> organisedReportList = new ArrayList<Report>();
//        for (int j=0;j<reportList.size();j++) {
//            String mostRecentDate = null;
//            int reportIdx = 0;
//            for (int i = 0; (i + 1) < reportList.size(); i++) {
//                if (i == 0) {
//                    String date1 = convertDatabaseDateToReadableDate(reportList.get(i).getReport_date());
//                    String date2 = convertDatabaseDateToReadableDate(reportList.get(i + 1).getReport_date());
//                    mostRecentDate = getMoreRecentDate(date1, date2);
//                    if ((mostRecentDate.equals(date1))) {
//                        reportIdx = i;
//                    } else {
//                        reportIdx = (i + 1);
//                    }
//                } else {
//                    String date1 = convertDatabaseDateToReadableDate(reportList.get(i + 1).getReport_date());
//                    mostRecentDate = getMoreRecentDate(date1, mostRecentDate);
//                    if ((mostRecentDate.equals(date1))) {
//                        reportIdx = i + 1;
//                    }
//                }
//            }
//            organisedReportList.add(reportList.get(reportIdx));
//
//        }
//        Log.d(TAG, "organiseReportByDate: final size: "+organisedReportList.size());
//        return organisedReportList;
//    }
//
//    public static List<ReportPreview> organiseReportPreviewByDate(List<ReportPreview> reportList) {
//        // first is more recent
////        Report[] organisedReportList = new Report[reportList.size()];
//        Log.d(TAG, "organiseReportByDate: size: "+reportList.size());
//        List<ReportPreview> organisedReportPreviewList = new ArrayList<ReportPreview>();
//        for (int j=0;j<reportList.size();j++) {
//            String mostRecentDate = null;
//            int reportIdx = 0;
//            for (int i = 0; (i + 1) < reportList.size(); i++) {
//                if (i == 0) {
//                    String date1 = convertDatabaseDateToReadableDate(reportList.get(i).getReport_date());
//                    String date2 = convertDatabaseDateToReadableDate(reportList.get(i + 1).getReport_date());
//                    mostRecentDate = getMoreRecentDate(date1, date2);
//                    if ((mostRecentDate.equals(date1))) {
//                        reportIdx = i;
//                    } else {
//                        reportIdx = (i + 1);
//                    }
//                } else {
//                    String date1 = convertDatabaseDateToReadableDate(reportList.get(i + 1).getReport_date());
//                    mostRecentDate = getMoreRecentDate(date1, mostRecentDate);
//                    if ((mostRecentDate.equals(date1))) {
//                        reportIdx = i + 1;
//                    }
//                }
//            }
//            organisedReportPreviewList.add(reportList.get(reportIdx));
//        }
//        Log.d(TAG, "organiseReportByDate: final size: "+organisedReportPreviewList.size());
//        return organisedReportPreviewList;
//    }

}
