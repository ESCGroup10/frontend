//package com.example.singhealthapp.HelperClasses;
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.Service;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Binder;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.widget.Toast;
//
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//
//import com.example.singhealthapp.Models.DatabaseApiCaller;
//import com.example.singhealthapp.Models.ReportPreview;
//import com.example.singhealthapp.Models.ReportedCases;
//import com.example.singhealthapp.R;
//
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class NotificationFromBackground extends Service {
//    private final LocalBinder mBinder = new LocalBinder();
//    protected NotificationCompat.Builder builder;
//    private boolean newReport;
//    private boolean apiCallDone = false;
//    private int savedSize = -1;
//
//    public class LocalBinder extends Binder {
//        public NotificationFromBackground getService() {
//            return NotificationFromBackground.this;
//        }
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return mBinder;
//    }
//
//    private void createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String name = getString(R.string.channel_name);
//            String description = getString(R.string.channel_description);
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }
//
//    @Override
//    public void onCreate() {
//        createNotificationChannel();
//        builder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
//                .setSmallIcon(R.drawable.ic_baseline_checklist_24)
//                .setContentTitle("My notification")
//                .setContentText("Hello World!")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setAutoCancel(true);
//        super.onCreate();
//
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Intent broadcastIntent = new Intent("com.example.singhealthapp.HelperClasses.SensorRestarterBroadcastReceiver");
//        sendBroadcast(broadcastIntent);
////        stopTask();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        System.out.println("there1");
//        Bundle bundle = intent.getExtras();
//        int tenantID = bundle.getInt("TENANT_ID_KEY");
//        String token = bundle.getString("TOKEN_KEY");
//
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                hasNewReport(tenantID, token);
//
//                System.out.println("there2");
//                AsyncTask.execute(() -> {
//                    if (!newReport) {
//                        return;
//                    }
//                    synchronized (this) {
//                        while (!apiCallDone) {
//                            try {
//                                System.out.println("there3");
//                                wait(2000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                    System.out.println("there4");
//                    System.out.println("I'm here!");
//                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(NotificationFromBackground.this);
//
//                    // notificationId is a unique int for each notification that you must define
//                    notificationManager.notify(1, builder.build());
//                    System.out.println("background new unresolved report checker stopped");
//                });
//            }
//        };
//
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(timerTask, 0, 5000);
//
//        return android.app.Service.START_STICKY;
//    }
//
//    public synchronized void hasNewReport(int tenantID, String token) {
//        System.out.println("here1");
//
//        AsyncTask.execute(() -> {
//            DatabaseApiCaller apiCaller = new Retrofit.Builder()
//                    .baseUrl("https://esc10-303807.et.r.appspot.com/")
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build()
//                    .create(DatabaseApiCaller.class);
//            Call<List<ReportedCases>> getReportedCases = apiCaller.getReportedCase("Token " + token, tenantID);
//
//            System.out.println("here3");
//            getReportedCases.enqueue(new Callback<List<ReportedCases>>() {
//                @Override
//                public void onResponse(Call<List<ReportedCases>> call, Response<List<ReportedCases>> response) {
//                    System.out.println("NotificationFromBackground response code: " + response.code());
//                    if (response.isSuccessful()) {
//                        System.out.println("here4");
//                        List<ReportedCases> reportedCasesList = response.body();
//                        System.out.println("NotificationFromBackground number of ReportedCases in list: " + reportedCasesList.size());
//                        if (savedSize != -1 && savedSize < reportedCasesList.size()) {
//                            System.out.println("NotificationFromBackground savedSize has been set more than once");
//                            newReport = true;
//                        }
//                        savedSize = reportedCasesList.size();
//                        apiCallDone = true;
//                    } else {
//                        System.out.println("NotificationFromBackground response code: " + response.code());
//                        // something wrong, act as if there are no new reports
//                        apiCallDone = true;
//                        newReport = false;
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<List<ReportedCases>> call, Throwable t) {
//                    t.printStackTrace();
//                    // something wrong, act as if there are no new reports
//                    apiCallDone = true;
//                    newReport = false;
//                }
//            });
//        });
//        System.out.println("here5");
//        notifyAll();
//    }
//
//}