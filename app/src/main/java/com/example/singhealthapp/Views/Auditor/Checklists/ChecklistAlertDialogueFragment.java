package com.example.singhealthapp.Views.Auditor.Checklists;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ChecklistAlertDialogueFragment extends DialogFragment {

    String non_compliance;

    public void ChecklistAlertDialogFragment(String non_compliance) {
        this.non_compliance = non_compliance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Non-compliance for:\n"+this.non_compliance)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: check for photo and notes and save to db
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }
}
