package com.example.singhealthapp.HelperClasses;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.singhealthapp.R;

import static java.lang.Thread.yield;

public class CustomFragment extends Fragment implements IOnBackPressed{

//    private boolean onBackPressedActivated;

    @Override
    public boolean onBackPressed() {
        if (!isActivityNull()) {
            System.out.println("activity is: "+getActivity());
            String tagName = this.getClass().getName();

            FragmentManager manager = getParentFragmentManager();

            boolean fragmentPopped = manager.popBackStackImmediate();

            if (!fragmentPopped) { //fragment not in back stack, create it.
                manager.beginTransaction().replace(R.id.fragment_container, this, tagName)
                        .addToBackStack(tagName)
                        .commit();
            }
        } else {
            System.out.println("activity should be null: "+getActivity());
            System.out.println("Waiting for things to load before pressing back");
        }
        return true;
    }

    /**
     * Gets the Activity associated with this fragment, but is able to yield a specified number of times until the Activity is
     * available. If it still is not available after that, it returns null.
     * - Do not increase count too much, otherwise it might turn into an pseudo infinite while loop
    * */
    public Activity customRequireActivity(int count) {
        System.out.println("customRequireActivity CALLED");
        Activity activity = getActivity();
//        if (isActivityNull()) {
//            onBackPressedActivated = false;
//        }
        return activity;
    }

    private boolean isActivityNull() {
        return (getActivity() == null);
    }
}
