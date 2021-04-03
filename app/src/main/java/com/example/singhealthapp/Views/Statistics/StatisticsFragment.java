package com.example.singhealthapp.Views.Statistics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.singhealthapp.HelperClasses.SetIdlingResource;
import com.example.singhealthapp.HelperClasses.SimpleIdlingResource;
import com.example.singhealthapp.R;

public class StatisticsFragment extends Fragment {
    @Nullable
    private SimpleIdlingResource mIdlingResource;
    private SetIdlingResource setIdlingResource = new SetIdlingResource(mIdlingResource);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setIdlingResource.toFalse();
        getActivity().setTitle("View Statistics");
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onStart() {
        setIdlingResource.toTrue();
        super.onStart();
    }
}