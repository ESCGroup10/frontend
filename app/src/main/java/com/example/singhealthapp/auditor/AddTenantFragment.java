package com.example.singhealthapp.auditor;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.singhealthapp.DatabaseApiCaller;
import com.example.singhealthapp.R;
import com.example.singhealthapp.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AddTenantFragment extends Fragment {

    ArrayList<EditText> editList = new ArrayList<EditText>();
    String[] name = {"TENANT REP NAME", "COMPANY NAME", "EMAIL", "LOCATION", "INSTITUTION"};
    List<String> nameList = Arrays.asList(name);
    String[] type = {"F&B", "Non F&B"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Add New Tenant");
        View view = inflater.inflate(R.layout.fragment_add_tenant, container, false);

        editList.add(view.findViewById(R.id.text2));
        editList.add(view.findViewById(R.id.text3));
        editList.add(view.findViewById(R.id.text4));
        editList.add(view.findViewById(R.id.text5));
        editList.add(view.findViewById(R.id.text6)); // is there a better way to do this?

        // set click listeners for all the EditText view
        for (int i = 0; i < editList.size(); i++){
            EditText t = editList.get(i);
            t.setInputType(InputType.TYPE_NULL);
            int finalI = i;
            t.setOnClickListener(v -> listenerAction(t, nameList.get(finalI)));
            t.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    listenerAction(t, nameList.get(finalI));
                }
            });
        }

        // for setting type of the tenant
        Spinner spin = view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, type);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spin.setAdapter(adapter);

        // save the new tenant
        view.findViewById(R.id.addTenantConfirm).setOnClickListener(v -> {
            boolean status = saveTenant(spin.getSelectedItem().toString());
            if ( status ) getActivity().onBackPressed();
        });

        return view;
    }

    // helper method for specifying behaviour of each instance of EditText
    protected void listenerAction(EditText t, String name){
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(name);
        EditText temp = new EditText(getContext());
        temp.setMaxLines(1);
        temp.setInputType(InputType.TYPE_CLASS_TEXT);
        temp.setText(t.getText());
        alert.setView(temp);
        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        alert.setPositiveButton("Ok", (dialog, whichButton) -> {
            t.setText(temp.getText());
            imgr.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        });
        alert.setNegativeButton("Cancel", (dialog, whichButton) -> {
            imgr.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        });

        alert.show();
        temp.requestFocus();
    }

    // helper method to create a tenant object with input of the user
    private boolean saveTenant(String type){
        ArrayList<String> arguments = new ArrayList<String>();
        for (EditText t : editList){
            if (t.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "There are still blank fields left.", Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
            arguments.add(t.getText().toString());
        }
        User tenantObject = new User(arguments.get(0), arguments.get(1), arguments.get(2),
                arguments.get(3), arguments.get(4), type);
        queryAddTenant(tenantObject);
        return true;
    }

    protected void queryAddTenant(User user) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://esc10-303807.et.r.appspot.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);
        /*Call<User> call = apiCaller.postUser(user.getName(), user.getCompany(),
                user.getEmail(), user.getLocation(),
                user.getInstitution(), user.getType()); */
        Map<String, String> fields = new HashMap<>();
        fields.put("name", user.getName());
        fields.put("company", user.getCompany());
        fields.put("location", user.getLocation());
        fields.put("type", user.getType());
        fields.put("institution", user.getInstitution());
        fields.put("email", user.getEmail());

        Call<User> call = apiCaller.postUser(fields);

        // for testing placeholder site
        //fields = new HashMap<>();
        //fields.put("title", "we");
        // Call<User> call = apiCaller.postTest(fields);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    //Toast.makeText(getContext(), String.valueOf(response.code()), Toast.LENGTH_LONG).show();
                    System.out.println(response.code() + "\n\n");
                    return ;
                }
                System.out.println(response.code() + " New tenant added.");
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                try {
                    throw t;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                return ;
            }
        });
    }
}
