package com.example.singhealthapp.Views.Auditor.AddTenant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.singhealthapp.HelperClasses.Ping;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.R;
import com.example.singhealthapp.Models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AddTenantFragment extends Fragment {

    private String token;

    ArrayList<EditText> editList = new ArrayList<EditText>();
    private final String[] name = {"TENANT REP NAME", "COMPANY NAME", "EMAIL", "LOCATION", "INSTITUTION"};
    private final List<String> nameList = Arrays.asList(name);
    private final String[] type = {"F&B", "Non F&B"};
    private final String regExpnEmail =
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1, type);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spin.setAdapter(adapter);

        // button for saving the new tenant
        view.findViewById(R.id.addTenantConfirm).setOnClickListener(v -> {
            if ( saveTenant(spin.getSelectedItem().toString()) ) {
                new androidx.appcompat.app.AlertDialog.Builder(getContext())
                        .setTitle("SUCCESS")
                        .setMessage("NEW TENANT ADDED!")
                        .setPositiveButton(android.R.string.yes, null).create().show();
            }
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
        if (name.equals("EMAIL")) temp.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        temp.setText(t.getText());
        alert.setView(temp);
        InputMethodManager imgr = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        alert.setPositiveButton("Ok", (dialog, whichButton) -> {

            // check email format
            if ( name.equals("EMAIL") && ! temp.getText().toString().isEmpty()) {
                CharSequence inputStr = temp.getText().toString();
                Pattern pattern = Pattern.compile(regExpnEmail, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(inputStr);

                if ( ! matcher.matches() ) {
                    new androidx.appcompat.app.AlertDialog.Builder(getContext())
                            .setTitle("ERROR")
                            .setMessage("INVALID EMAIL FORMAT!")
                            .setPositiveButton(android.R.string.yes, (arg0, arg1) ->
                                    t.performClick()).create().show();
                    return ;
                }
            }
            t.setText(temp.getText());
            imgr.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        });
        alert.setNegativeButton("Cancel", (dialog, whichButton) -> {
            imgr.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        });

        alert.show();
        temp.requestFocus();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((Ping)requireActivity()).decrementCountingIdlingResource();
        loadToken();
    }

    // helper method to create a tenant object with input of the user and call a query with it
    private boolean saveTenant(String type){
        ArrayList<String> arguments = new ArrayList<String>();
        for (EditText t : editList){
            if (t.getText().toString().isEmpty()) {
                new androidx.appcompat.app.AlertDialog.Builder(getContext())
                        .setTitle("ERROR")
                        .setMessage("THERE ARE STILL BLANK FIELD LEFT!")
                        .setPositiveButton(android.R.string.yes, null).create().show();
                return false;
            }
            arguments.add(t.getText().toString());
        }
        User tenantObject = new User(arguments.get(2), "1234", arguments.get(0), arguments.get(1),
                arguments.get(3), arguments.get(4), type);
        queryAddTenant(tenantObject);
        return true;
    }

    // calling server API to create a new object in the cloud
    protected void queryAddTenant(User user) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://esc10-303807.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DatabaseApiCaller apiCaller = retrofit.create(DatabaseApiCaller.class);

        Map<String, String> fields = new HashMap<>();
        fields.put("email", user.getEmail());
        fields.put("password", user.getPassword());
        fields.put("name", user.getName());
        fields.put("company", user.getCompany());
        fields.put("location", user.getLocation());
        fields.put("type", user.getType());
        fields.put("institution", user.getInstitution());

        Call<Void> call = apiCaller.postUser("Token " + token, fields);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    // Toast
                    return ;
                }
                //System.out.println(response.code() + ": New tenant added.");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Toast
                return ;
            }
        });
    }

    private void loadToken() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN_KEY", null);
    }
}
