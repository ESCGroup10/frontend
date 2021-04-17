package com.example.singhealthapp.HelperClasses;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class BackStackInfo {

    public static void printBackStackInfo(FragmentManager fm, Fragment f) {
        System.out.println("this fragment's tag: "+f.getTag());
        System.out.println("Number of fragments on the back stack: "+fm.getBackStackEntryCount());
    }

}
