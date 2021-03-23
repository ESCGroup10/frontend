package com.example.singhealthapp.auditor.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singhealthapp.Models.Checklist_item;
import com.example.singhealthapp.R;
import com.example.singhealthapp.auditor.TakePhotoInterface;

import java.io.File;
import java.util.ArrayList;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {

    private static final String TAG = "ChecklistAdapter";

    private final int green = Color.parseColor("#FF00FF00");
    private final int red = Color.parseColor("#FFFF0000");
    private final int grey = Color.parseColor("#FF808080");
    private final int black = Color.parseColor("#FF000000");

    private ArrayList<Checklist_item> checklist_items_array = new ArrayList<>();

    private TakePhotoInterface photoFragment;

    public ChecklistAdapter(TakePhotoInterface photoFragment, ArrayList<Checklist_item> checklist_items_array) {
        this.checklist_items_array = checklist_items_array;
        this.photoFragment = photoFragment;
    }

    public ChecklistAdapter(ArrayList<Checklist_item> checklist_items_array) {
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
        // TODO: find a way to get remarks saved to the checklist_item just before changing fragment
        checklist_items_array.get(position).setRemarks(holder.editTextRemarks.getText().toString());
        if (checklist_items_array.get(position).getImageBitmap() != null) {
            holder.cameraButton.setImageBitmap(checklist_items_array.get(position).getImageBitmap());
        }
    }

    @Override
    public int getItemCount() {
        return checklist_items_array.size();
    }

    public void updateAdapter(Bitmap photo, int adapterPosition) {
        checklist_items_array.get(adapterPosition).setImageBitmap(photo);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewQuestion, textViewTrue, textViewFalse, textViewNA;
        private EditText editTextRemarks;
        private View colourStatusIndicator;
        private ImageButton cameraButton;

        File photoFile;
        String currentPhotoPath;

//        private File createImageFile(Context context, String currentPhotoPath) throws IOException {
//            // Create an image file name
//            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//            String imageFileName = "JPEG_" + timeStamp + "_" + "user_id"; //TODO: add part of user id for collision resistance
//            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//            File image = File.createTempFile(
//                    imageFileName,  /* prefix */
//                    ".jpg",         /* suffix */
//                    storageDir      /* directory */
//            );
//
//            // Save a file: path for use with ACTION_VIEW intents
//            currentPhotoPath = image.getAbsolutePath();
//            return image;
//        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewQuestion = itemView.findViewById(R.id.textview_question);
            textViewTrue = itemView.findViewById(R.id.button_true);
            textViewFalse = itemView.findViewById(R.id.button_false);
            textViewNA = itemView.findViewById(R.id.button_na);
            editTextRemarks = itemView.findViewById(R.id.remarks);
            colourStatusIndicator = itemView.findViewById(R.id.color_status_indicator);
            cameraButton = itemView.findViewById(R.id.camera_button);

            Context context = cameraButton.getContext();

            cameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("registered camera click");
                    photoFragment.takePhoto(ChecklistAdapter.this, getAdapterPosition());
                }
            });

//            cameraButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, "onClick: "+cameraButton);
////                    auditChecklistFragmentPhotoListener.onPhotoReturnListener((ImageButton)cameraButton);
//                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    String chooser_title = "Take picture with";
//                    Intent chooser = Intent.createChooser(takePictureIntent, chooser_title);
//                    if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
//                        try {
//                            try {
//                                photoFile = createImageFile(context, currentPhotoPath);
//                            } catch (IOException ex) {
//                                // Error occurred while creating the File
//                            }
//                            // Continue only if the File was successfully created
//                            if (photoFile != null) {
//                                Uri photoURI = FileProvider.getUriForFile(context,
//                                        "com.example.android.fileprovider",
//                                        photoFile);
//                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                                ((Activity) context).startActivityForResult(chooser, REQUEST_IMAGE_CAPTURE);
//                            }
//                        } catch (ActivityNotFoundException e) {
//                            Log.d(TAG, "Error: "+e);
//                            CentralisedToast.makeText(context, "Error: Camera Activity not found", CentralisedToast.LENGTH_SHORT);
//                        }
//                    } else {
//                        CentralisedToast.makeText(context, "Unable to find camera", CentralisedToast.LENGTH_SHORT);
//                    }
//                }
//            });

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