package com.example.singhealthapp.Views.Auditor.Checklists;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.Containers.AuditorFragmentContainer;
import com.example.singhealthapp.HelperClasses.CheckStatusInterface;
import com.example.singhealthapp.Models.ChecklistItem;
import com.example.singhealthapp.R;
import com.example.singhealthapp.HelperClasses.HandlePhotoInterface;

import java.util.ArrayList;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder>
        implements AuditChecklistFragment.OnAuditSubmitListener, AuditorFragmentContainer.OnPhotoTakenListener, CheckStatusInterface {

    private static final String TAG = "ChecklistAdapter";

    private final int green = Color.parseColor("#FF03AC13");
    private final int red = Color.parseColor("#FFDD3930");
    private final int grey = Color.parseColor("#FF808080");
    private final int black = Color.parseColor("#FF000000");
    private final int white = Color.parseColor("#FFFFFFFF");

    private ArrayList<ChecklistItem> checklist_items_array = new ArrayList<>();
    private boolean isAudit;

    private HandlePhotoInterface TakePhotoActivity;

    public ChecklistAdapter(HandlePhotoInterface takePhotoActivity, ArrayList<ChecklistItem> checklist_items_array, boolean isAudit) {
        this.isAudit = isAudit;
        this.checklist_items_array = checklist_items_array;
        this.TakePhotoActivity = takePhotoActivity;
    }

    // instantiate ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_checklist, parent, false);
        return new ViewHolder(view);
    }

    // bind data to views in ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewQuestion.setText(checklist_items_array.get(position).getStatement());
        holder.editTextRemarks.setText(checklist_items_array.get(position).getRemarks());
        if (isAudit) {
            checklist_items_array.get(position).setRemarks(holder.editTextRemarks.getText().toString());
            if (checklist_items_array.get(position).isPhotoTaken()) {
                holder.cameraButton.setBackgroundResource(R.drawable.camera_photo_taken);
                holder.colourStatusIndicator.setBackgroundColor(red);
                checklist_items_array.get(position).setCase(true);
                checklist_items_array.get(position).setPhotoTaken(true);
                checklist_items_array.get(position).setStatusSet(true);
                Log.d(TAG, "onBindViewHolder: setting qn: "+checklist_items_array.get(position).getStatement()+"\n"+
                        "to a case");
            }
        }
    }

    public boolean callTakePhoto(int position) {
        /**
        * Usage: allows adapter to ask underlying activity to take photo.
        * */
        String question = checklist_items_array.get(position).getStatement();
        return TakePhotoActivity.takePhoto(ChecklistAdapter.this, position, question);
    }

    @Override
    public int getItemCount() {
        return checklist_items_array.size();
    }

    @Override
    public ArrayList<String> sendCases() {
        /**
        * Usage:
        * - sends a list of pairs of statements(questions) and remarks(comments)
        * */
        ArrayList<String> cases  = new ArrayList<>();

        for (ChecklistItem item : checklist_items_array) {
            if (item.isCase()) {
                Log.d(TAG, "sendCases: "+item.getStatement());
                Log.d(TAG, "sendCases: "+item.getRemarks());
                cases.add(item.getStatement());
                cases.add(item.getRemarks());
            }
        }
        return cases;
    }

    @Override
    public int getNumCases() {
        int result = 0;
        for (ChecklistItem item : checklist_items_array) {
            if (item.isCase()) {
                result++;
            }
        }
        return result;
    }

    @Override
    public void photoTaken(int position) {
        checklist_items_array.get(position).setPhotoTaken(true);
        checklist_items_array.get(position).setStatusSet(true);
        checklist_items_array.get(position).setCase(true);
        notifyItemChanged(position);
    }

    @Override
    public String allStatusSet() {
        for (ChecklistItem item : checklist_items_array) {
            if (!item.isStatusSet()) {
                return item.getStatement();
            }
        }
        return null;
    }

    @Override
    public int numNA() {
        int numNA = 0;
        for (ChecklistItem item : checklist_items_array) {
            if (item.isNA()) {
                numNA++;
            }
        }
        return numNA;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewQuestion, textViewTrue, textViewFalse, textViewNA;
        private EditText editTextRemarks;
        private View colourStatusIndicator;
        private ImageButton cameraButton;
        private View extraLayer;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewQuestion = itemView.findViewById(R.id.textview_question);
            textViewTrue = itemView.findViewById(R.id.button_true);
            textViewFalse = itemView.findViewById(R.id.button_false);
            textViewNA = itemView.findViewById(R.id.button_na);
            editTextRemarks = itemView.findViewById(R.id.remarks);
            colourStatusIndicator = itemView.findViewById(R.id.color_status_indicator);
            cameraButton = itemView.findViewById(R.id.camera_button);
            extraLayer = itemView.findViewById(R.id.extra_layer);
            cardView = itemView.findViewById(R.id.cardview);

            if (isAudit) {
                editTextRemarks.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        checklist_items_array.get(getAdapterPosition()).setRemarks(s.toString());
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                cameraButton.setOnClickListener(v -> {
                    cardView.setCardBackgroundColor(white);
                    callTakePhoto(getAdapterPosition());
                });
            } else {
                cameraButton.setVisibility(View.GONE);
                editTextRemarks.setVisibility(View.GONE);
                extraLayer.setVisibility(View.VISIBLE);
            }

            textViewTrue.setOnClickListener(v -> {
                cardView.setCardBackgroundColor(white);
                if (((ColorDrawable)colourStatusIndicator.getBackground()).getColor() == green) { // view has been clicked before
                    checklist_items_array.get(getAdapterPosition()).setStatusSet(false);
                    colourStatusIndicator.setBackgroundColor(black);
                } else { // view has not been clicked before
                    colourStatusIndicator.setBackgroundColor(green);
                    checklist_items_array.get(getAdapterPosition()).setCase(false);
                    checklist_items_array.get(getAdapterPosition()).setStatusSet(true);
                }
                if (isAudit) {
                    cameraButton.setBackgroundResource(R.drawable.camera);
                }
            });

            textViewFalse.setOnClickListener(v -> {
                if (((ColorDrawable)colourStatusIndicator.getBackground()).getColor() == red) { // view has been clicked before
                    if (!checklist_items_array.get(getAdapterPosition()).isPhotoTaken()) {
                        colourStatusIndicator.setBackgroundColor(black);
                        checklist_items_array.get(getAdapterPosition()).setCase(false);
                        checklist_items_array.get(getAdapterPosition()).setStatusSet(false);
                    }
                } else { // view has not been clicked before
                    Log.d(TAG, "onClick: set false");
                    colourStatusIndicator.setBackgroundColor(red);
                    checklist_items_array.get(getAdapterPosition()).setCase(true);
                    checklist_items_array.get(getAdapterPosition()).setStatusSet(true);
                }
                if (checklist_items_array.get(getAdapterPosition()).isPhotoTaken()) {
                    if (isAudit) {
                        cameraButton.setBackgroundResource(R.drawable.camera_photo_taken);
                    }
                }
            });

            textViewNA.setOnClickListener(v -> {
                cardView.setCardBackgroundColor(white);
                if (((ColorDrawable)colourStatusIndicator.getBackground()).getColor() == grey) { // view has been clicked before
                    colourStatusIndicator.setBackgroundColor(black);
                    checklist_items_array.get(getAdapterPosition()).setNA(false);
                    checklist_items_array.get(getAdapterPosition()).setStatusSet(false);
                } else { // view has not been clicked before
                    colourStatusIndicator.setBackgroundColor(grey);
                    checklist_items_array.get(getAdapterPosition()).setCase(false);
                    checklist_items_array.get(getAdapterPosition()).setStatusSet(true);
                    checklist_items_array.get(getAdapterPosition()).setNA(true);
                }
                if (isAudit) {
                    cameraButton.setBackgroundResource(R.drawable.camera);
                }
            });
        }
    }
}