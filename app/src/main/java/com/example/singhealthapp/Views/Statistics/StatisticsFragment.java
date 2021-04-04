package com.example.singhealthapp.Views.Statistics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.singhealthapp.HelperClasses.Ping;
import com.example.singhealthapp.R;

public class StatisticsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("View Statistics");
        ((Ping)requireActivity()).decrementCountingIdlingResource();
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }
}