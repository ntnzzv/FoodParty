package com.example.recipebook.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RealTimeDBService {
    private static RealTimeDBService rtdbServiceInstance = null;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private RealTimeDBService() {
        this.database = FirebaseDatabase.getInstance();
        this.databaseReference = database.getReference();
    }

    private DatabaseReference getReferenceByPath(String path) {
        return database.getReference(path);
    }

    public static RealTimeDBService getInstance() {
        if (rtdbServiceInstance == null)
            rtdbServiceInstance = new RealTimeDBService();
        return rtdbServiceInstance;
    }

    /*  ------------SPECIFIC-TO-THIS-PROJECT------------    */

    public static final String USERS_DB_NAME = "users";

    public DatabaseReference getReferenceToUsersDB() {
        return databaseReference.child(USERS_DB_NAME);
    }

    public DatabaseReference getReferenceToRecipe(String creatorId, String recipeId) {
        return getReferenceByPath(getRecipePath(creatorId, recipeId));
    }
    public DatabaseReference getReferenceToRecipeField(String creatorId, String recipeId,String fieldName) {
        String path=getRecipePath(creatorId, recipeId) + "/" + fieldName;
        return getReferenceByPath(path);
    }

    private String getRecipePath(String creatorId, String recipeId) {
        return USERS_DB_NAME + "/"+ creatorId +"/"+recipeId;
    }


}
