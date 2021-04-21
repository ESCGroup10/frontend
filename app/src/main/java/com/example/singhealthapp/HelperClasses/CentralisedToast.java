package com.example.singhealthapp.HelperClasses;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class CentralisedToast {

    public static final int LENGTH_SHORT = 0;
    public static final int LENGTH_LONG = 1;

    public static void makeText(Context context, String text, int duration) {
        Toast toast = Toast.makeText(context, text, duration);
        try {
            TextView toastView = toast.getView().findViewById(android.R.id.message);
            if (toastView != null) toastView.setGravity(Gravity.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        toast.show();
    }
}
