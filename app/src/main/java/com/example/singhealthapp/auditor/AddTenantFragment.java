package com.example.singhealthapp.auditor;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.singhealthapp.R;
import com.example.singhealthapp.tenant.TenantObject;


public class AddTenantFragment extends Fragment {

    ArrayList<EditText> editList = new ArrayList<EditText>();
    String[] name = {"STALL ID", "TENANT REP NAME", "PASSWORD", "COMPANY NAME", "EMAIL", "LOCATION", "INSTITUTION"};
    List<String> nameList = Arrays.asList(name);
    String[] type = {"F&B", "Non F&B"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Add New Tenant");
        View view = inflater.inflate(R.layout.fragment_add_tenant, container, false);

        editList.add(view.findViewById(R.id.text1));
        editList.add(view.findViewById(R.id.text2));
        editList.add(view.findViewById(R.id.textPassword));
        editList.add(view.findViewById(R.id.text3));
        editList.add(view.findViewById(R.id.text4));
        editList.add(view.findViewById(R.id.text5));
        editList.add(view.findViewById(R.id.text6)); // is there a better way to do this?

        // set click listeners for all the EditText view
        for (int i = 0; i < editList.size(); i++){
            EditText t = editList.get(i);
            t.setInputType(InputType.TYPE_NULL);
            if ( i == 2 ) t.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
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


    protected void listenerAction(EditText t, String name){
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(name);
        EditText temp = new EditText(getContext());
        temp.setMaxLines(1);
        temp.setInputType(InputType.TYPE_CLASS_TEXT);
        if (name.equals("STALL ID")) temp.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (name.equals("PASSWORD")) temp.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
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

    private boolean saveTenant(String type){
        ArrayList<String> arguments = new ArrayList<String>();
        for (EditText t : editList){
            if (t.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "There are still blank fields left.", Toast.LENGTH_SHORT).show();
                return false;
            }
            arguments.add(t.getText().toString());
        }
        TenantObject tenantObject = new TenantObject(arguments.get(1), Integer.parseInt(arguments.get(0)), arguments.get(4), arguments.get(2), arguments.get(3), arguments.get(5), arguments.get(6), type);
        Toast.makeText(getContext(), "New tenant has been successfully added!", Toast.LENGTH_LONG).show();
        return true;
    }
}

