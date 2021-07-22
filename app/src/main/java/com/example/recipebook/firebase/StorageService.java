package com.example.recipebook.firebase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class StorageService {
    private static StorageService storageServiceInstance = null;


    private FirebaseStorage storage ;
    private StorageReference storageReference;

    private StorageService() {
        this.storage= FirebaseStorage.getInstance();
        this.storageReference = storage.getReference();
    }

    private StorageReference getReferenceByPath(String path) {
        return storage.getReference(path);
    }

    public static StorageService getInstance() {
        if (storageServiceInstance == null)
            storageServiceInstance = new StorageService();
        return storageServiceInstance;
    }
    /*  ------------SPECIFIC-TO-THIS-PROJECT------------    */
    public static final String IMAGES_FOLDER_PATH = "images/";

    public StorageReference getReferenceToImagesFolder() {
        return storageReference.child(IMAGES_FOLDER_PATH+ UUID.randomUUID().toString());
    }

}
