package com.example.singhealthapp.Views;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.User;
import com.example.singhealthapp.R;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TestFragment extends Fragment {

    TextView queryTextView, postTextView, imagesTextView, uploadedImageTextView;
    Button postButton, testButton, listImagesButton, uploadImgButton;
    ImageView uploadedImage;
    Drawable testImage;

    private String token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("How to Query Data from Web");

        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        queryTextView = getView().findViewById(R.id.query_textview);
        postTextView = getView().findViewById(R.id.post_textview);
        imagesTextView = getView().findViewById(R.id.image_list);
        uploadedImageTextView = getView().findViewById(R.id.uploadedImg_textview);

        testButton = getView().findViewById(R.id.test_button);
        postButton = getView().findViewById(R.id.post_button);
        listImagesButton = getView().findViewById(R.id.listimage_button);
        uploadImgButton = getView().findViewById((R.id.uploadImg_button));

        testImage = getResources().getDrawable(R.drawable.testupload);

        uploadedImage = getView().findViewById(R.id.uploadedImg_image);

        testButton.setOnClickListener(v -> queryUsers());
        postButton.setOnClickListener(v -> addNewUser());
        listImagesButton.setOnClickListener(v -> retrieveImageList());
        uploadImgButton.setOnClickListener(v -> {
            uploadImage();
//                displayImage();
        });

        loadToken();
    }

    private void queryUsers() {

        // create an api caller to the webserver
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://esc10-303807.et.r.appspot.com/").addConverterFactory(GsonConverterFactory.create()).build();
        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);
        Call<List<User>> call = apiCaller.getUsers("Token " + token); // let the call response be a List of User

        // make a call to get a response of a List of User
        call.enqueue(new Callback<List<User>>() {

            // if query succeeds, show the query on the app by updating the TextView
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), String.valueOf(response.code()), Toast.LENGTH_LONG).show();
                    return ;
                }

                List<User> webUserList = response.body(); // get the response body from the webserver
                String localUser = new String(); // create a String to hold the query (to display on app)

                // append all the info to the String (to display later on)
                for (User webUser : webUserList) {
                    localUser += " [id: " + webUser.getId() + " name: " + webUser.getName() + " email: " + webUser.getEmail() + " company: " + webUser.getCompany() + " institution: " + webUser.getInstitution() + " type: " + webUser.getType() + "]";
                }

                queryTextView.setText(localUser); // update the TextView with the query
            }

            // if query fails, display the reason for failure using Toast
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

                queryTextView.setText("Query Failed!");
                Toast.makeText(getActivity(), "" + t, Toast.LENGTH_LONG).show(); // show the error message with Toast
            }
        });
    }

    private void addNewUser() {
        // create an api caller to the webserver
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://esc10-303807.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);

        // *** PLEASE CHANGE THE EMAIL EVERY TIME YOU DO A NEW POST REQUEST!!!! **
        Call<User> call = apiCaller.postNewUser("Token " + token, "Joey@t.com", "1234","Joey", "Joey Bakery", "Blk 4 Lvl 1", "SGH", "F&B");

        // make a call to post a new User to the database
        call.enqueue(new Callback<User>() {
            // on success, the TextView shows success message
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                System.out.println("Response body: " + response.errorBody());
                postTextView.setText("Post Requested!\n" + "Response Code: " + response.code());
            }

            // on failure, the TextView shows a failure message
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                postTextView.setText("Failure! Error: "+ t);
            }
        });
    }

    private void loadToken() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN_KEY", null);

        int userId = sharedPreferences.getInt("USER_ID_KEY", 0);
        System.out.println("User ID " + userId);
    }

    public void retrieveImageList() {
        // If you don't specify credentials when constructing the client, the client library will
        // look for credentials via the environment variable GOOGLE_APPLICATION_CREDENTIALS.
        new Thread(() -> {
            Storage storage = StorageOptions.getDefaultInstance().getService();

            Bucket bucket = storage.get("case-images");

            ArrayList<String> imageNames = new ArrayList<>();
            Iterable<Blob> list = storage.list("case-images").getValues();
            for (Blob blob : list) {
                imageNames.add(blob.getName());
            }
            getActivity().runOnUiThread(() -> imagesTextView.setText("Bucket Name: " + bucket + "\n" +imageNames.toString()));
        }).start();
    }

    public void uploadImage() {

        int caseId = 3;

        new Thread(() -> {
            try {
                Storage storage = StorageOptions.getDefaultInstance().getService();

                Bitmap bitmap = ((BitmapDrawable) testImage).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();

                BlobId blobId = BlobId.of("case-images", "case-"+caseId);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/png").build();
                storage.create(blobInfo, bitmapdata);

            } catch (Exception e) {
                System.out.println("Upload Failed" + e);
            }
            getActivity().runOnUiThread(() -> uploadedImageTextView.setText("case-" + caseId));

        }).start();
    }

}