package com.example.singhealthapp.HelperClasses;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.singhealthapp.R;

public class CustomFragment extends Fragment implements IOnBackPressed{

    @Override
    public boolean onBackPressed() {
        String tagName = this.getClass().getName();

        FragmentManager manager = getParentFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate();

        if (!fragmentPopped){ //fragment not in back stack, create it.
            manager.beginTransaction().replace(R.id.fragment_container, this, tagName)
                    .addToBackStack(tagName)
                    .commit();
        }
        return true;
    }
}
