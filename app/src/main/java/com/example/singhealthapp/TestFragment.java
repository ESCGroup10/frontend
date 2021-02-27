package com.example.singhealthapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

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
    }

    public void queryUsers() {

        // create an api caller to the webserver
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://esc10-303807.et.r.appspot.com").addConverterFactory(GsonConverterFactory.create()).build();
        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);
        Call<List<User>> call = apiCaller.getUsers(); // let the call response be a List of User

        // make a call to get a response of a List of User
        call.enqueue(new Callback<List<User>>() {

            // if query succeeds, show the query on the app by updating the TextView
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

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

    public void addNewUser() {
        // create an api caller to the webserver
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://esc10-303807.et.r.appspot.com").addConverterFactory(GsonConverterFactory.create()).build();
        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);
        Call<User> call = apiCaller.postNewUser("Alice", "Alice Bakery", "alice@t.com", "Blk 4 Lvl 1", "SGH", "F&B");

        // make a call to post a new User to the database
        call.enqueue(new Callback<User>() {

            // on success, the TextView shows success message
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                postTextView.setText("Post Success! Click on 'Get All Users' button to see changes! \n" + "Response Code: " + response.code());
            }

            // on failure, the TextView shows a failure message
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                postTextView.setText("Failure! Error: "+ t);
            }
        });
    }
}