package com.example.recipebook.utils.handlers;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.example.recipebook.firebase.RealTimeDBService;
import com.example.recipebook.firebase.StorageService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.example.recipebook.utils.Constants.IMAGE_URL_FIELD_NAME;

public final class ImageHandler {


    public static void UploadImage(Context context, Context ToastClassContext, Uri filePath, String userUid, String recipeName) {


        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference imagesRef = StorageService.getInstance().getReferenceToImagesFolder();

            // adding listeners on upload
            // or failure of image
            // Progress Listener for loading
            // percentage on the dialog box
            imagesRef.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully to cloud! now we can get the imageUrl from cloud and update it in database
                        handleOnSuccess(ToastClassContext, userUid, recipeName, progressDialog, imagesRef);
                    })
                    .addOnFailureListener(e -> {
                        // Error, Image not uploaded
                        handleOnFailure(ToastClassContext, progressDialog, e);
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        //While image uploaded, user can see the percent of uploading progress
                        handleOnProgress(progressDialog, taskSnapshot);
                    });
        }

    }

    private static void handleOnProgress(ProgressDialog progressDialog, UploadTask.TaskSnapshot taskSnapshot) {
        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
        progressDialog.setMessage("Uploaded " + (int) progress + "%");
    }

    private static void handleOnFailure(Context ToastClassContext, ProgressDialog progressDialog, Exception e) {
        progressDialog.dismiss();
        Toast.makeText(ToastClassContext, "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private static void handleOnSuccess(Context ToastClassContext, String userUid, String recipeName, ProgressDialog progressDialog, StorageReference imagesRef) {
        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Get reference to imageUrl field in DB and set the path to image in stodge cloud (uri)
                RealTimeDBService.getInstance()
                        .getReferenceToRecipeField(userUid, recipeName, IMAGE_URL_FIELD_NAME)
                        .setValue(uri.toString());
            }
        });
        progressDialog.dismiss();
        Toast.makeText(ToastClassContext, "Image Uploaded!!!", Toast.LENGTH_SHORT).show();
    }
}
