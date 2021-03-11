package com.example.singhealthapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TestFragment extends Fragment {

    TextView queryTextView;
    Button testButton;

    TextView postTextView;
    Button postButton;

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
        testButton = getView().findViewById(R.id.test_button);

        postTextView = getView().findViewById(R.id.post_textview);
        postButton = getView().findViewById(R.id.post_button);

        testButton.setOnClickListener(v -> queryUsers());
        postButton.setOnClickListener(v -> addNewUser());

        loadToken();
    }

    private void queryUsers() {

        // create an api caller to the webserver
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:8000").addConverterFactory(GsonConverterFactory.create()).build();
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
                .baseUrl("http://10.0.2.2:8000")
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
}