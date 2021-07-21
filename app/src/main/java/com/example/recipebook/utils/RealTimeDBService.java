package com.example.recipebook.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RealTimeDBService {
    private static RealTimeDBService rtdbServiceInstance = null;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private RealTimeDBService() {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getDBReference(String childName) {
        return databaseReference.child(childName);
    }

    public DatabaseReference getReferenceByPath(String path) {
        return firebaseDatabase.getReference(path);
    }

    public static RealTimeDBService getInstance() {
        if (rtdbServiceInstance == null)
            rtdbServiceInstance = new RealTimeDBService();
        return rtdbServiceInstance;
    }
}
