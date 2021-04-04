package com.example.singhealthapp.HelperClasses;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;

import java.io.ByteArrayOutputStream;

public class DatabasePhotoOperations {
    private static final String TAG = "DatabasePhotoOperations";

    public static void uploadImage(Bitmap bitmap, String photoName) throws NullPointerException {

        new Thread(() -> {
            try {
                Storage storage = StorageOptions.getDefaultInstance().getService();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();

                BlobId blobId = BlobId.of("case-images", photoName);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();
                storage.create(blobInfo, bitmapdata);
                Log.d(TAG, "uploadImage: success");

            } catch (StorageException e) {
                Log.d(TAG, "uploadImage: failed to upload photo");
            } catch (NullPointerException e) {
                Log.d(TAG, "uploadImage: image does not exist");
                throw new NullPointerException();
            }

        }).start();
    }

}
