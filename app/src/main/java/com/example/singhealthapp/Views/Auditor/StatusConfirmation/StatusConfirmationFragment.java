package com.example.singhealthapp.Views.Auditor.StatusConfirmation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.singhealthapp.HelperClasses.CentralisedToast;
import com.example.singhealthapp.HelperClasses.Ping;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Auditor.Reports.ReportsFragment;
import com.example.singhealthapp.Views.Tenant.MyReportsFragment;

import java.util.Arrays;

public class StatusConfirmationFragment extends Fragment {

    private static final String TAG = "StatusConfirmationFrag";
    private final String TITLE_KEY = "title_key";
    private final String MSG_KEY = "message_key";
    private final String BUTTON_TXT_KEY = "button_text_key";

    private String userType;

    TextView messageText;
    Button button;

    String title;
    String message;
    String button_text;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        loadUserType();

        Bundle bundle = getArguments();
        try {
            title = bundle.getString(TITLE_KEY);
            message = bundle.getString(MSG_KEY);
            button_text = bundle.getString(BUTTON_TXT_KEY);
        } catch (Exception e) {
            Log.d(TAG, "onCreateView: "+ Arrays.toString(e.getStackTrace()));
        }

        getActivity().setTitle(title);
        View view = inflater.inflate(R.layout.fragment_status_confirmation, container, false);
        messageText = view.findViewById(R.id.message_text);
        button  = view.findViewById(R.id.button_return);
        messageText.setText(message);
        button.setText(button_text);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Ping) requireActivity()).incrementCountingIdlingResource(1);
                if (userType.equals("Auditor")) {
                    StatusConfirmationFragment.this.getParentFragmentManager().beginTransaction()
                            .replace(R.id.auditor_fragment_container, new ReportsFragment(), "getReport")
                            .commit();
                } else {
                    StatusConfirmationFragment.this.getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new MyReportsFragment(), "MyReportsFragment")
                            .commit();
                }

            }
        });

        return view;
    }

    private void loadUserType() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        userType = sharedPreferences.getString("USER_TYPE_KEY", null);
    }
}
