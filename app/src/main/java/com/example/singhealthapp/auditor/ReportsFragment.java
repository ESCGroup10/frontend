package com.example.singhealthapp.auditor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.DatabaseApiCaller;
import com.example.singhealthapp.R;
import com.example.singhealthapp.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReportsFragment extends Fragment {
    ReportPreviewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Reports");

        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        queueList();
        return view;
    }

    private void queueList(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://esc10-303807.et.r.appspot.com").addConverterFactory(GsonConverterFactory.create()).build();
        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);
        Call<List<ReportPreview>> call = apiCaller.getReportPreview();
        List<ReportPreview> list = new ArrayList<>();
        call.enqueue(new Callback<List<ReportPreview>>() {
            @Override
            public void onResponse(Call<List<ReportPreview>> call, Response<List<ReportPreview>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Unsuccessful: response code " + response.code(), Toast.LENGTH_LONG).show();
                    return ;
                }
                adapter = new ReportPreviewAdapter(response.body(), getActivity());
                list.addAll(response.body());
                try {
                    RecyclerView view = (RecyclerView) getView().findViewById(R.id.reportPreviewRecyclerView);
                    view.setLayoutManager(new LinearLayoutManager(getActivity()));
                    view.setItemAnimator(new DefaultItemAnimator());
                    view.setAdapter(adapter);
                }
                catch (Exception ignored) {}
            }

            @Override
            public void onFailure(Call<List<ReportPreview>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}