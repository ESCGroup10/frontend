package com.example.singhealthapp.HelperClasses;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class CentralisedToast {

    public static void makeText(Context context, String text, int duration) {
        Toast toast = Toast.makeText(context, text, duration);
        TextView toastView = (TextView) toast.getView().findViewById(android.R.id.message);
        if( toastView != null) toastView.setGravity(Gravity.CENTER);
        toast.show();
    }
}
