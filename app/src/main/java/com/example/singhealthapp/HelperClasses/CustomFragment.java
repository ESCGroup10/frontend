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
    private static final String TAG = "CustomFragment";

    @Override
    public boolean onBackPressed() {
        System.out.println("CustomFragment: activity is: "+getActivity());
        String tagName = this.getClass().getName();

        FragmentManager manager = getParentFragmentManager();

        boolean fragmentPopped;
        if (manager.getBackStackEntryCount() > 1) {
//            Log.d(TAG, "onBackPressed: getBackStackEntryCount() > 1");
            fragmentPopped = manager.popBackStackImmediate();
        } else {
//            Log.d(TAG, "onBackPressed: getBackStackEntryCount() <= 1");
            return false;
        }

        if (!fragmentPopped) { //fragment not in back stack, create it.
            manager.beginTransaction().replace(R.id.fragment_container, this, tagName)
                    .addToBackStack(tagName)
                    .commit();
        }
        return true;
    }

}
