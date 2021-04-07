package com.example.singhealthapp.Views.Tenant;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.singhealthapp.Models.Case;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.R;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExpandedCase extends Fragment {
    private static final String TAG = "ExpandedCase";

    // UI stuff
    TextView companyTextView;
    TextView institutionTextView;
    TextView caseNumberTextView;
    TextView nonComplianceTypeTextView;
    TextView resolvedStatusTextView;
    ImageView unresolvedImageView;
    TextView unresolvedImageInformationTextView;
    TextView unresolvedCommentsTextView;
    ImageView resolvedImageView;
    TextView resolvedImageInformationTextView;
    TextView resolvedCommentsTextView;

    // database stuff
    DatabaseApiCaller apiCaller;
    private String token;
    private int reportID;
    private int caseID;

    private int reportNumber;
    private Integer caseNumber;
    private String company;
    private String institution;
    private String nonComplianceType;
    private boolean resolvedStatus;
    private Bitmap unresolvedImageBitmap;
    private Bitmap resolvedImageBitmap;
    private String unresolvedComments;
    private String resolvedComments;
    private String unresolvedImageName;
    private String resolvedImageName;
    private String unresolvedImageInformation;
    private String resolvedImageInformation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_expaded_case, container, false);
        findAllViews(view);

        loadToken();
        initApiCaller();
        loadCompanyAndInstitution();

        Bundle bundle = getArguments();
        // TODO: make fragment before this send report number
        try {
            reportNumber = bundle.getInt("REPORT_NUMBER_KEY");
        } catch (Exception e) {
            reportNumber = 1;
        }
        try {
            caseNumber = bundle.getInt("CASE_NUMBER_KEY");
        } catch (Exception e) {
            caseNumber = 1;
        }
        try {
            resolvedStatus = bundle.getBoolean("RESOLVED_STATUS_KEY");
        } catch (Exception e) {
            resolvedStatus = false;
        }
        try {
            reportID = bundle.getInt("REPORT_ID_KEY");
        } catch (Exception e) {
            reportID = 169;
        }
        try {
            caseID = bundle.getInt("CASE_ID_KEY");
        } catch (Exception e) {
            caseID = 31;
        }
        getActivity().setTitle("Report "+reportNumber);
        getCase();
        setAllViewsFromBundle();

        return view;
    }

    private void findAllViews(View view) {
        Log.d(TAG, "findAllViews: called");
        companyTextView = view.findViewById(R.id.companyTextView);
        institutionTextView = view.findViewById(R.id.institutionTextView);
        caseNumberTextView = view.findViewById(R.id.caseNumberTextView);
        nonComplianceTypeTextView = view.findViewById(R.id.nonComplianceTypeTextView);
        resolvedStatusTextView = view.findViewById(R.id.resolvedStatusTextView);
        unresolvedImageView = view.findViewById(R.id.unresolvedImageView);
        unresolvedImageInformationTextView = view.findViewById(R.id.unresolvedImageInformationTextView);
        unresolvedCommentsTextView = view.findViewById(R.id.unresolvedCommentsTextView);
        resolvedImageView = view.findViewById(R.id.resolvedImageView);
        resolvedImageInformationTextView = view.findViewById(R.id.resolvedImageInformationTextView);
        resolvedCommentsTextView = view.findViewById(R.id.resolvedCommentsTextView);
    }

    private void setAllViewsFromBundle() {
        Log.d(TAG, "setAllViews: called");
        companyTextView.setText(companyTextView.getText() + company);
        institutionTextView.setText(institutionTextView.getText() + institution);
        caseNumberTextView.setText(caseNumberTextView.getText() + caseNumber.toString());
        resolvedStatusTextView.setText(resolvedStatusTextView.getText() + (resolvedStatus?"true":"false"));
    }

    private void setAllViewsFromDatabase() {
        Log.d(TAG, "setAllViewsFromDatabase: called");
        nonComplianceTypeTextView.setText(nonComplianceTypeTextView.getText() + nonComplianceType);
        unresolvedImageInformationTextView.setText(unresolvedImageInformationTextView.getText() + unresolvedImageInformation);
        unresolvedCommentsTextView.setText(unresolvedCommentsTextView.getText() + unresolvedComments);
        retrieveImage(unresolvedImageName, unresolvedImageView);
        if (resolvedStatus) {
            resolvedImageInformationTextView.setText(resolvedImageInformationTextView.getText() + resolvedImageInformation);
            resolvedCommentsTextView.setText(resolvedCommentsTextView.getText() + resolvedComments);
            retrieveImage(resolvedImageName, resolvedImageView);
        }
    }

    private void loadCompanyAndInstitution() {
        Log.d(TAG, "loadCompanyAndInstitution: called");
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        company = sharedPreferences.getString("COMPANY_KEY", "default company");
        institution = sharedPreferences.getString("INSTITUTION_KEY", "default institution");
    }

    private synchronized void loadToken() {
        Log.d(TAG, "loadToken: called");
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN_KEY", null);
        notifyAll();
    }

    public void retrieveImage(String imageName, ImageView imageView) {
        Log.d(TAG, "retrieveImage: called");
        Log.d(TAG, "retrieveImage: imageName:"+imageName);
        Log.d(TAG, "retrieveImage: imageView:"+imageView.toString());

        // API call needs to be done async or on another thread
        new Thread(() -> {
            try {
                Storage storage = StorageOptions.getDefaultInstance().getService(); // get the Cloud Storage space

                // retrieve image in the form of byte array from Cloud Storage
                byte[] bitmapdata = storage.get("case-images").get(imageName).getContent();

                //convert the byte array into a bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

                // display bitmap image on ImageView in another thread
                getActivity().runOnUiThread(() -> imageView.setImageBitmap(bitmap));;

            } catch (Exception e) {
                System.out.println("Retrieval Failed! " + e);
            }
        }).start();
    }

    private synchronized void initApiCaller() {
        /**
         * This method is synchronised in order to make other methods wait until apiCaller is instantiated before trying to use it
         * as well as wait for the token to be initialised
         * */
        Log.d(TAG, "initApiCaller: called");
        while (token == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "initApiCaller: token: "+token);
        apiCaller = new Retrofit.Builder()
                .baseUrl("https://esc10-303807.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);
        notifyAll();
    }

    private synchronized void getCase() {
        /**
         * This method is synchronised to ensure that apiCaller is instantiated before we try to use it
         * Usage: retrieves the following fields from the database:
         * - nonComplianceType
         * - unresolvedComments
         * - unresolvedImageInformation
         * - unresolvedImageName
         * - resolvedImageName (null if case is not resolved)
         * - resolvedComments (null if case is not resolved)
         * - resolvedImageName (null if case is not resolved)
         * */
        Log.d(TAG, "getCase: called");
        while (apiCaller == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "getCase: apiCaller: "+apiCaller.toString());
        Call<List<Case>> myCaseList;
        Log.d(TAG, "getCase: token: "+token);
        Log.d(TAG, "getCase: reportID: "+reportID);
        myCaseList = apiCaller.getCasesById("Token " + token, reportID, (resolvedStatus?1:0));
        myCaseList.enqueue(new Callback<List<Case>>() {
            @Override
            public void onResponse(Call<List<Case>> call, Response<List<Case>> response) {
                Log.d(TAG, "getCase response code: " + response.code());
                System.out.println("getCase response code: " + response.code());
                Case myCase = null;
                for (Case c : response.body()) {
                    if (c.getId() == caseID) {
                        myCase = c;
                    }
                }
                if (myCase != null) {
                    nonComplianceType = myCase.getNon_compliance_type();
                    unresolvedComments = myCase.getUnresolved_comments();
                    unresolvedImageInformation = myCase.getUnresolved_date();
                    unresolvedImageName = myCase.getUnresolved_photo();

                    // if the case is resolved, get the data, else leave relevant fields as null
                    if (resolvedStatus) {
                        resolvedImageName = myCase.getResolved_photo();
                        resolvedComments = myCase.getResolved_comments();
                        resolvedImageName = myCase.getResolved_date();
                    }
                    setAllViewsFromDatabase();
                } else {
                    System.out.println("onResponse: no cases match given caseID");
                    Log.d(TAG, "onResponse: no cases match given caseID");
                }
            }

            @Override
            public void onFailure(Call<List<Case>> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }

    @Override
    public String toString() {
        return "ExpandedCase{" +
                "companyTextView=" + companyTextView +
                ", institutionTextView=" + institutionTextView +
                ", caseNumberTextView=" + caseNumberTextView +
                ", nonComplianceTypeTextView=" + nonComplianceTypeTextView +
                ", resolvedStatusTextView=" + resolvedStatusTextView +
                ", unresolvedImageView=" + unresolvedImageView +
                ", unresolvedImageInformationTextView=" + unresolvedImageInformationTextView +
                ", unresolvedCommentsTextView=" + unresolvedCommentsTextView +
                ", resolvedImageView=" + resolvedImageView +
                ", resolvedImageInformationTextView=" + resolvedImageInformationTextView +
                ", resolvedCommentsTextView=" + resolvedCommentsTextView +
                ", apiCaller=" + apiCaller +
                ", token='" + token + '\'' +
                ", reportID=" + reportID +
                ", reportNumber=" + reportNumber +
                ", caseNumber=" + caseNumber +
                ", company='" + company + '\'' +
                ", institution='" + institution + '\'' +
                ", nonComplianceType='" + nonComplianceType + '\'' +
                ", resolvedStatus=" + resolvedStatus +
                ", unresolvedImageBitmap=" + unresolvedImageBitmap +
                ", resolvedImageBitmap=" + resolvedImageBitmap +
                ", unresolvedComments='" + unresolvedComments + '\'' +
                ", resolvedComments='" + resolvedComments + '\'' +
                ", unresolvedImageName='" + unresolvedImageName + '\'' +
                ", resolvedImageName='" + resolvedImageName + '\'' +
                ", unresolvedImageInformation='" + unresolvedImageInformation + '\'' +
                ", resolvedImageInformation='" + resolvedImageInformation + '\'' +
                '}';
    }

}