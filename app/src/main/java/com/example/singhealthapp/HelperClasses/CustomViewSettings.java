package com.example.singhealthapp.HelperClasses;

import android.annotation.SuppressLint;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.EditText;

public class CustomViewSettings {

    @SuppressLint("ClickableViewAccessibility")
    public static void makeScrollable(EditText t) {
        // to enable scrolling, multiline and done button within editText
        t.setRawInputType(InputType.TYPE_CLASS_TEXT);
        t.setOnTouchListener((v, event) -> {
            if (t.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                }
            }
            return false;
        });
    }

}
