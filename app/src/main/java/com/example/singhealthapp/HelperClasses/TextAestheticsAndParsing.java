package com.example.singhealthapp.HelperClasses;

import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

public class TextAestheticsAndParsing {

    public static void setHalfBoldTextViews(TextView mytextview, String textToAdd) {
        String originalText = mytextview.getText().toString();
        if(Build.VERSION.SDK_INT < 24) {
            String sourceString = "<b>" + originalText + "</b> " + textToAdd;
            mytextview.setText(Html.fromHtml(sourceString));
        } else {
            int INT_END = originalText.length();
            SpannableStringBuilder str = new SpannableStringBuilder(originalText + textToAdd);
            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, INT_END, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mytextview.setText(str);
        }
    }

    public static void setHalfBoldTextViews(TextView mytextview, String boldStyle, String normalStyle) {
        if(Build.VERSION.SDK_INT < 24) {
            String sourceString = "<b>" + boldStyle + "</b> " + normalStyle;
            mytextview.setText(Html.fromHtml(sourceString));
        } else {
            int INT_END = boldStyle.length();
            SpannableStringBuilder str = new SpannableStringBuilder(boldStyle + normalStyle);
            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, INT_END, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mytextview.setText(str);
        }
    }

    public static void setHalfBoldTextViews(TextView mytextview, String boldStyle, String normalStyle, Boolean boldAtEnd) {
        if (boldAtEnd) {
            if (Build.VERSION.SDK_INT < 24) {
                String sourceString = normalStyle + "<b>" + boldStyle + "</b> ";
                mytextview.setText(Html.fromHtml(sourceString));
            } else {
                int INT_START = normalStyle.length();
                int INT_END = boldStyle.length() + INT_START;
                SpannableStringBuilder str = new SpannableStringBuilder(normalStyle + boldStyle);
                str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), INT_START, INT_END, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                mytextview.setText(str);
            }
        } else {
            setHalfBoldTextViews(mytextview, normalStyle, boldStyle);
        }
    }

}
