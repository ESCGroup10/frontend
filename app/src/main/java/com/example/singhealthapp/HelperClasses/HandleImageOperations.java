package com.example.singhealthapp.HelperClasses;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.ImageFormat;
import android.media.Image;
import android.media.ImageWriter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.IntentCompat;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import static android.app.Activity.RESULT_OK;

public class HandleImageOperations {

    public static File createFile(Activity activity) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    public static Bitmap getBitmap(int resultCode, Activity activity, Uri uri) {
        /**
         * Usage: Get image bitmap
         * */
        Bitmap bitmap = null;
        if (resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT < 28) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                            activity.getContentResolver(),
                            uri
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ImageDecoder.Source source = ImageDecoder.createSource(activity.getContentResolver(), uri);
                try {
                    bitmap = ImageDecoder.decodeBitmap(source);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    public static void retrieveImageFromDatabase(Activity activity, String imageName, ImageView imageView, @Nullable TextView placeholder, int reqWidth, int reqHeight) {
        // API call needs to be done async or on another thread
        new Thread(() -> {
            try {
                Storage storage = StorageOptions.getDefaultInstance().getService(); // get the Cloud Storage space
                System.out.println("Storage has connected");

                // retrieve image in the form of byte array from Cloud Storage
                // TODO: Make this take less time! (at most 4s)
                Date date = new Date();
                long startTime = date.getTime();
                byte[] bitmapdata = storage.get("case-images").get(imageName).getContent();
                Date date2 = new Date();
                long endTime = date2.getTime();
                System.out.println("Time taken to get bitmapdata from storage: "+((endTime-startTime)/1000)+"s");

                // get image dimensions
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length, options);

                // calculate down-sampling size
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

                // get bitmap
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length, options);

                // display bitmap image on ImageView in ui thread
                activity.runOnUiThread(() -> {
                    if (placeholder != null) {
                        placeholder.setVisibility(View.GONE);
                    }
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(bitmap);
                });

            } catch (ExceptionInInitializerError e) {
                e.printStackTrace();
                placeholder.setText("Image could not be loaded, please try again later.");
            } catch (NullPointerException e) {
                placeholder.setText("Image does not exist");
                System.out.println("Image does not exist, check backend");
                e.printStackTrace();
            } catch (Exception e) {
                placeholder.setText("Image could not be loaded");
                System.out.println("Retrieval Failed!");
                e.printStackTrace();
            } catch (NoClassDefFoundError e) {
                placeholder.setText("Image could not be loaded, please come back to this page again.");
                e.printStackTrace();
            }
        }).start();
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void uploadImageToDatabase(Bitmap bitmap, String photoName, int count) throws NullPointerException {

        new Thread(() -> {
            try {
                Storage storage = StorageOptions.getDefaultInstance().getService();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();

                BlobId blobId = BlobId.of("case-images", photoName);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();
                storage.create(blobInfo, bitmapdata);
                System.out.println("uploadImage: success");

            } catch (StorageException e) {
                System.out.println("uploadImage: failed to upload photo");
            } catch (NullPointerException e) {
                System.out.println("uploadImage: image does not exist");
                throw new NullPointerException();
            } catch (Exception e) {
                e.printStackTrace();
                if (count > 0) {
                    System.out.println("Trying to upload image to database again");
                    uploadImageToDatabase(bitmap, photoName, count - 1);
                } else {
                    System.out.println("Unable to upload image to database");
                }
            }

        }).start();
    }

//    private Byte[] compressJpg(String fileName) throws FileNotFoundException {
//        File imageFile = new File(fileName);
//        File compressedImageFile = new File("compressed_" + fileName);
//
//        InputStream is = new FileInputStream(imageFile);
//        OutputStream os = new FileOutputStream(compressedImageFile);
//
//        float quality = 0.5f;
//
//        // create a BufferedImage as the result of decoding the supplied InputStream
//        BufferedImage image = ImageIO.read(is);
//
//        // get all image writers for JPG format
//        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
//
//        if (!writers.hasNext())
//            throw new IllegalStateException("No writers found");
//
//        ImageWriter writer = (ImageWriter) writers.next();
//        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
//        writer.setOutput(ios);
//
//        ImageWriteParam param = writer.getDefaultWriteParam();
//
//        // compress to a given quality
//        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//        param.setCompressionQuality(quality);
//
//        // appends a complete image stream containing a single image and
//        //associated stream and image metadata and thumbnails to the output
//        writer.write(null, new IIOImage(image, null, null), param);
//
//        // close all streams
//        is.close();
//        os.close();
//        ios.close();
//        writer.dispose();
//
//    }

}
