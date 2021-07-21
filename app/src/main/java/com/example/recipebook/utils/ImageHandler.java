package com.example.recipebook.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.recipebook.activities.AddRecipeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public final class ImageHandler {



    public static void UploadImage(Context context, Context ToastClassContext, Uri filePath,String userUid, String recipeName){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        String recipePath = "Recipe/" + userUid + "/" + recipeName ;
        if (filePath != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("images/");

            // adding listeners on upload
            // or failure of image
            // Progress Listener for loading
// percentage on the dialog box
            ref.putFile(filePath).addOnSuccessListener(
                    taskSnapshot -> {
                        // Image uploaded successfully
                        // Dismiss dialog
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUrl = uri;

                                FirebaseService.getInstance().getDBReference(userUid)
                                        .child("Recipes/")
                                        .child(recipeName)
                                        .child("imageUrl").setValue(uri.toString());
                                //Do what you want with the url
                            }});
                        progressDialog.dismiss();
                        Toast.makeText(ToastClassContext, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                    })

                    .addOnFailureListener(e -> {
                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast.makeText(ToastClassContext,"Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int)progress + "%");
                            });
        }

    }
}
