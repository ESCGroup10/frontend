package com.example.singhealthapp.HelperClasses;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.singhealthapp.R;

public class NotificationFromBackground extends Service {
    private final LocalBinder mBinder = new LocalBinder();
    protected NotificationCompat.Builder builder;

    public class LocalBinder extends Binder {
        public NotificationFromBackground getService() {
            return NotificationFromBackground.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        builder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                .setSmallIcon(R.drawable.ic_baseline_checklist_24)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        handler = new Handler();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("I'm here!");
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(NotificationFromBackground.this);

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(1, builder.build());
                stopSelf();
                System.out.println("stopped");
            }
        });
        return android.app.Service.START_STICKY;
    }

}