package com.example.recipebook.utils;

import android.content.Intent;

import com.example.recipebook.entities.Recipe;
import com.example.recipebook.firebase.AuthGoogleService;

import static com.example.recipebook.utils.Constants.RECIPE_DETAILS;

public class Methods {
    public static  Recipe getRecipeObject(Intent intent) {

        return (Recipe) intent.getSerializableExtra(RECIPE_DETAILS);
    }
    public static boolean thisUserCreateThisRecipe(Recipe recipe) {
        return recipe.getCreatorId().
                equals(AuthGoogleService.getInstance().getFirebaseCurrentUser().getUid());
    }
}
