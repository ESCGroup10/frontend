package com.example.singhealthapp.HelperClasses;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.singhealthapp.Containers.TenantFragmentContainer;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.ReportedCases;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Tenant.LatestReportFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Service extends android.app.Service {
    protected static final int NOTIFICATION_ID = 1337;
    private static String TAG = "Service";
    private static Service mCurrentService;
    private static Timer timer;
    private static TimerTask timerTask;
    private static DatabaseApiCaller apiCaller;
    private static String token;
    private static int tenantID;
    protected NotificationCompat.Builder builder;
    private boolean newReport;
    private int savedSize = -1;

    SharedPreferences sharedPreferences;

    public Service() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            System.out.println("creating the service");
            Log.d(TAG, "onCreate: service created");
            restartForeground();
        }
        mCurrentService = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
//        Log.d(TAG, "restarting Service !!");

        // it has been killed by Android and now it is restarted. We must make sure to have reinitialised everything
        if (intent == null) {
//            Log.d(TAG, "onStartCommand: it has been killed by Android and now it is restarted. We must make sure to have reinitialised everything");
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(this);
        }

        // make sure you call the startForeground on onStartCommand because otherwise
        // when we hide the notification on onScreen it will not restart in Android 6 and 7
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            restartForeground();
        }

        // get extras from intent
//        Log.d(TAG, "onStartCommand: setting token and tenantID");
        Bundle bundle = intent.getExtras();
        tenantID = bundle.getInt("TENANT_ID_KEY");
        token = bundle.getString("TOKEN_KEY");

        // return start sticky so if it is killed by android, it will be restarted with Intent null
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * it starts the process in foreground. Normally this is done when screen goes off
     * THIS IS REQUIRED IN ANDROID 8 :
     * "The system allows apps to call Context.startForegroundService()
     * even while the app is in the background.
     * However, the app must call that service's startForeground() method within five seconds
     * after the service is created."
     */
    public void restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Log.i(TAG, "restarting foreground");
            try {
                Notification notification = new Notification();
                startForeground(NOTIFICATION_ID, notification.setNotification(this, "Hello there!", "I will notify you of any new reports!", R.drawable.ic_baseline_anchor_24));
//                Log.i(TAG, "restarting foreground successful");
                startTask();
            } catch (Exception e) {
                Log.e(TAG, "Error in notification " + e.getMessage());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.d(TAG, "onDestroy: check if we should shut down the process");
//        System.out.println("shut down: "+checkShutDown());
        if (checkShutDown()) {
            Log.d(TAG, "onDestroy: service shutting down");
            return;
        }
        Log.d(TAG, "onDestroy: service restarting");
//        Log.i(TAG, "onDestroy called");
        // restart the never ending service
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
    }


    /**
     * this is called when the process is killed by Android
     *
     * @param rootIntent
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
//        Log.i(TAG, "onTaskRemoved called");
        // restart the never ending service
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        // do not call stoptimertask because on some phones it is called asynchronously
        // after you swipe out the app and therefore sometimes
        // it will stop the timer after it was restarted
    }

    public void startTask() {
//        Log.d(TAG, "startTask: starting task");

        // initialize the TimerTask's job
        initializeTimerTask();

//        Log.d(TAG, "Scheduling...");
        //schedule the timer, to wake up every 5 minutes
        timer.schedule(timerTask, 0, 10000);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        String name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
//        Log.d(TAG, "createNotificationChannel: notification channel created");
    }

    private void buildNotification() {
        // Create an explicit intent for latest report page
        Intent intent = new Intent(this, TenantFragmentContainer.class);
        intent.putExtra("FROM_NOTIFICATION", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                .setSmallIcon(R.drawable.ic_baseline_checklist_24)
                .setContentTitle("New Report Received")
                .setContentText("Click to resolve!")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setAutoCancel(true);
//        Log.d(TAG, "buildNotification: notification built");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startChecking() {
//        System.out.println("shut down: "+checkShutDown());
        if (checkShutDown()) {
            stopSelf();
            return;
        }
        // create apiCaller
        apiCaller = new Retrofit.Builder()
                .baseUrl("https://esc10-303807.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);

        // GET request to get information about any new reports
        Call<List<ReportedCases>> getReportedCases = apiCaller.getReportedCase("Token " + token, tenantID);
//        Log.d(TAG, "startChecking: get request sent with \ntenantID: "+tenantID+"\nand token: "+token);

        getReportedCases.enqueue(new Callback<List<ReportedCases>>() {
            @Override
            public void onResponse(@NotNull Call<List<ReportedCases>> call, @NotNull Response<List<ReportedCases>> response) {
//                Log.d(TAG, "we got back response code: " + response.code());
                if (response.isSuccessful()) {
                    List<ReportedCases> reportedCasesList = response.body();
                    int numReports = 0;
                    for (ReportedCases rc : reportedCasesList) {
                        numReports += rc.getCount();
                    }
//                    Log.d(TAG, "number of ReportedCases in list: " + numReports);
//                    Log.d(TAG, "savedSize before: "+savedSize);
//                    Log.d(TAG, "=========================================================");
                    if (savedSize != -1 && savedSize < numReports) {
//                        Log.d(TAG, "savedSize has been set more than once");
                        newReport = true;
                    }
                    savedSize = numReports;
//                    Log.d(TAG, "savedSize after: "+savedSize);
                } else {
                    // something wrong, act as if there are no new reports
                    newReport = false;
                }
            }

            @Override
            public void onFailure(Call<List<ReportedCases>> call, Throwable t) {
                t.printStackTrace();
                // something wrong, act as if there are no new reports
                newReport = false;
            }
        });

        // there will be a lag between the database response and the following check, so the notifications will always be at most 5 minutes late,
        // but we reduce any synchronization workload we would need to have immediate updates
        createNotificationChannel();
        buildNotification();
        if (newReport) {
//            Log.d(TAG, "found a new report, will make notification!");
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Service.this);
            notificationManager.notify(123, builder.build());
            newReport = false;
        }
    }

    public void initializeTimerTask() {
//        Log.i(TAG, "initialising TimerTask");
        timer = new Timer();
        timerTask = new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void run() {
                startChecking();
            }
        };
    }

    private boolean checkShutDown() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("STOP_NOTIFICATIONS", false);
    }

}

