package com.example.recipebook.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.recipebook.R;
import com.example.recipebook.activities.MainActivity;
import com.example.recipebook.firebase.RealTimeDBService;
import com.example.recipebook.firebase.StorageService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import static com.example.recipebook.utils.Constants.FILE_PATH;
import static com.example.recipebook.utils.Constants.IMAGE_URL_FIELD_NAME;
import static com.example.recipebook.utils.Constants.RECIPE_NAME;
import static com.example.recipebook.utils.Constants.USER_UID;

public class UploadImageToCloudService extends Service {
    String CHANNEL_ID = "recipes_channel_01";
    private static final int NOTIFICATION_ID = 1;

    static NotificationManager notificationManager = null;

    Notification.Builder builder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        initForeground();
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        Bundle bundle = i.getExtras();
        Uri filePath = bundle.getParcelable(FILE_PATH);
        String recipeName = bundle.getString(RECIPE_NAME);
        String userUid = bundle.getString(USER_UID);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP / Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        builder = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Image upload status")
                .setSmallIcon(R.drawable.app_icon2)
                .setContentIntent(pendingIntent);

        if (filePath != null) {
            // Defining the child of storageReference
            StorageReference imagesRef = StorageService.getInstance().getReferenceToImagesFolder();
            imagesRef.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully to cloud! now we can get the imageUrl from cloud and update it in database
                        imagesRef.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //Get reference to imageUrl field in DB and set the path to image in stodge cloud (uri)
                                        RealTimeDBService.getInstance()
                                                .getReferenceToRecipeField(userUid, recipeName, IMAGE_URL_FIELD_NAME)
                                                .setValue(uri.toString());
                                        updateNotification("Image uploaded successfully");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // Error, Image not uploaded
                                    updateNotification("Image upload failed");
                                });
                    });
        }

        updateNotification(Integer.toString(0));
        return super.onStartCommand(i, flags, startId);
    }


    private void initForeground() {

        if (notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Recipes main channel", NotificationManager.IMPORTANCE_DEFAULT);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        //create an explicit intent for an activity

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP / Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        builder = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Testing Notification...")
                .setSmallIcon(R.drawable.app_icon2)
                .setContentIntent(pendingIntent);

        startForeground(NOTIFICATION_ID, updateNotification(Integer.toString(0)));
    }

    private Notification updateNotification(String details) {
        builder.setContentText(details).setOnlyAlertOnce(false);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        notificationManager.notify(NOTIFICATION_ID, notification);

        return notification;
    }


}
