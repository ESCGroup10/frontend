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
import androidx.fragment.app.FragmentManager;

import com.example.singhealthapp.HelperClasses.BackStackInfo;
import com.example.singhealthapp.HelperClasses.CentralisedToast;
import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.HelperClasses.IOnBackPressed;
import com.example.singhealthapp.HelperClasses.Ping;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Views.Auditor.TenantsPreview.TenantsPreviewFragment;
import com.example.singhealthapp.Views.ReportsPreview.ReportsPreviewFragment;
import com.example.singhealthapp.Views.Tenant.MyReportsFragment;

import java.util.Arrays;

public class StatusConfirmationFragment extends Fragment implements IOnBackPressed {

    private static final String TAG = "StatusConfirmationFrag";
    private final String TITLE_KEY = "TITLE_KEY";
    private final String MSG_KEY = "MSG_KEY";
    private final String ADDITIONAL_MSG_KEY = "ADDITIONAL_MSG_KEY";
    private final String BUTTON_TXT_KEY = "BUTTON_TXT_KEY";

    private String userType;

    TextView messageText;
    TextView additionalMessageText;
    Button button;

    String title;
    String message;
    String additional_message_text;
    String button_text;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loadUserType();
        View view = inflater.inflate(R.layout.f_status_confirmation, container, false);

        Bundle bundle = getArguments();
        try {
            title = bundle.getString(TITLE_KEY);
            message = bundle.getString(MSG_KEY);
            button_text = bundle.getString(BUTTON_TXT_KEY);
        } catch (Exception e) {
            Log.d(TAG, "StatusConfirmationFragment could not get bundle: "+ Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        }
        getActivity().setTitle(title);

        try {
            additional_message_text = bundle.getString(ADDITIONAL_MSG_KEY);
            additionalMessageText = view.findViewById(R.id.additional_message_text);
            additionalMessageText.setText(additional_message_text);
            additionalMessageText.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.d(TAG, "onCreateView: no additional message detected");
        }

        messageText = view.findViewById(R.id.message_text);
        button = view.findViewById(R.id.button_return);
        messageText.setText(message);
        button.setText(button_text);
        Log.d(TAG, "onCreateView: set views");

        BackStackInfo.printBackStackInfo(getParentFragmentManager(), this);
        button.setOnClickListener(v -> {
            EspressoCountingIdlingResource.increment();
            BackStackInfo.printBackStackInfo(getParentFragmentManager(), this);
            getParentFragmentManager().popBackStack(0, 0);
        });
        EspressoCountingIdlingResource.decrement();
        return view;
    }

    private void loadUserType() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        userType = sharedPreferences.getString("USER_TYPE_KEY", null);
    }

    @Override
    public boolean onBackPressed() {
        while (EspressoCountingIdlingResource.getCount() > 0) {
            EspressoCountingIdlingResource.decrement();
        }
        getParentFragmentManager().popBackStack(0, 0);
        return true;
    }

}
