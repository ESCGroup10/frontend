package com.example.singhealthapp.HelperClasses;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.singhealthapp.Containers.TenantFragmentContainer;

public class ProcessMainClass {
    private static final String TAG = "ProcessMainClass";
    private static Intent serviceIntent = null;
    private static int tenantID = -1;
    private static String token = null;

    public ProcessMainClass() {
    }

    private void setServiceIntent(Context context) {
        if (serviceIntent == null) {
            serviceIntent = new Intent(context, Service.class);
            serviceIntent.putExtra("TENANT_ID_KEY", tenantID);
            serviceIntent.putExtra("TOKEN_KEY", token);
        }
    }

    public static void stopBackgroundService(Context context) {
        try {
            context.stopService(serviceIntent);
        } catch (Exception e) {
//            System.out.println("serviceIntent ont initialized yet");
        }
    }

    /**
     * launching the service
     */
    public void launchService(Context context) {
        if (tenantID == -1 && token == null) {
            TenantFragmentContainer tenantFragmentContainer = new TenantFragmentContainer();
            token = ((SendInfoToPMC)tenantFragmentContainer).sendToken();
            tenantID = ((SendInfoToPMC)tenantFragmentContainer).sendID();
//            Log.d(TAG, "launchService: \nreceived token: "+token+"\ntenantID: "+tenantID);
//            Log.d(TAG, "launchService: static token: "+TenantFragmentContainer.token);
//            Log.d(TAG, "launchService: static token: "+TenantFragmentContainer.tenantID);
        }
        if (context == null) {
            return;
        }
        // if tenant has not logged in for the first time
        if (tenantID == -2 || token == null) {
            return;
        }
        setServiceIntent(context);
        // depending on the version of Android we either launch the simple service (version<O)
        // or we start a foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            System.out.println(serviceIntent);
            ComponentName componentName = context.startForegroundService(serviceIntent);
            System.out.println(componentName);
//            Log.d(TAG, "launchService: sdk>Oreo");
        } else {
//            Log.d(TAG, "launchService: sdk<Oreo");
            context.startService(serviceIntent);
        }
//        Log.d(TAG, "ProcessMainClass: start service go!!!!");
    }

}
