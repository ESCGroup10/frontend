package com.example.singhealthapp.Views.Auditor.Checklists;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.Models.ChecklistItem;
import com.example.singhealthapp.R;
import com.example.singhealthapp.HelperClasses.TakePhotoInterface;

import java.util.ArrayList;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {

    private static final String TAG = "ChecklistAdapter";

    private final int green = Color.parseColor("#FF00FF00");
    private final int red = Color.parseColor("#FFFF0000");
    private final int grey = Color.parseColor("#FF808080");
    private final int black = Color.parseColor("#FF000000");

    private ArrayList<ChecklistItem> checklist_items_array = new ArrayList<>();

    private TakePhotoInterface photoFragment;

    public ChecklistAdapter(TakePhotoInterface photoFragment, ArrayList<ChecklistItem> checklist_items_array) {
        this.checklist_items_array = checklist_items_array;
        this.photoFragment = photoFragment;
    }

    public ChecklistAdapter(ArrayList<ChecklistItem> checklist_items_array) {
        this.checklist_items_array = checklist_items_array;
    }

    // instantiate ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_checklist_item, parent, false);
        return new ViewHolder(view);
    }

    // bind data to views in ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewQuestion.setText(checklist_items_array.get(position).getStatement());
        checklist_items_array.get(position).setRemarks(holder.editTextRemarks.getText().toString());
    }

    @Override
    public int getItemCount() {
        return checklist_items_array.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewQuestion, textViewTrue, textViewFalse, textViewNA;
        private EditText editTextRemarks;
        private View colourStatusIndicator;
        private ImageButton cameraButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewQuestion = itemView.findViewById(R.id.textview_question);
            textViewTrue = itemView.findViewById(R.id.button_true);
            textViewFalse = itemView.findViewById(R.id.button_false);
            textViewNA = itemView.findViewById(R.id.button_na);
            editTextRemarks = itemView.findViewById(R.id.remarks);
            colourStatusIndicator = itemView.findViewById(R.id.color_status_indicator);
            cameraButton = itemView.findViewById(R.id.camera_button);

            cameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d(TAG, "onClick: registered camera click");
                    photoFragment.takePhoto(ChecklistAdapter.this, getAdapterPosition());
                }
            });

            textViewTrue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (colourStatusIndicator.getBackground().equals(green)) { // view has been clicked before
                        colourStatusIndicator.setBackgroundColor(black);
                    } else { // view has not been clicked before
                        colourStatusIndicator.setBackgroundColor(green);
                    }
                }
            });

            textViewFalse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (colourStatusIndicator.getBackground().equals(red)) { // view has been clicked before
                        colourStatusIndicator.setBackgroundColor(black);
                    } else { // view has not been clicked before
                        colourStatusIndicator.setBackgroundColor(red);
                    }
                }
            });

            textViewNA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (colourStatusIndicator.getBackground().equals(grey)) { // view has been clicked before
                        colourStatusIndicator.setBackgroundColor(black);
                    } else { // view has not been clicked before
                        colourStatusIndicator.setBackgroundColor(grey);
                    }
                }
            });
        }
    }
}