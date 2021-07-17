package com.example.recipebook;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseService {
    private static FirebaseService fbsInstance=null;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseService() {
        this.firebaseDatabase=FirebaseDatabase.getInstance();
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getDBReference(String childName) {
        return databaseReference.child(childName);
    }
    public DatabaseReference getReferenceByPath(String path) {
        return firebaseDatabase.getReference(path);
    }
    public static FirebaseService getInstance(){
        if(fbsInstance==null)
            fbsInstance=new FirebaseService();
        return fbsInstance;
    }
}
