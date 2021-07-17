package com.example.recipebook;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseService {
    private static FirebaseService fbsInstance=null;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private FirebaseService() {
        this.firebaseDatabase=FirebaseDatabase.getInstance();
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    protected DatabaseReference getSpecificDBReference(String childName) {
        return databaseReference.child(childName);
    }

    public static FirebaseService getInstance(){
        if(fbsInstance==null)
            fbsInstance=new FirebaseService();
        return fbsInstance;
    }
}
