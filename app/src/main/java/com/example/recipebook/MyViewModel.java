package com.example.recipebook;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.example.recipebook.Constants.RECIPES_DB_NAME;

public class MyViewModel extends AndroidViewModel {
    private MutableLiveData<List<Recipe>> recipesLiveData;
    private MutableLiveData<List<Recipe>> favoritesRecipesLiveData;
    private MutableLiveData<Boolean> favoritesOnlyLiveData = new MutableLiveData<>();

    List<Recipe> recipesList;
    List<Recipe> favoritesRecipesList;
    boolean favoritesOnlyFlag;

    FirebaseService fbs;
    DatabaseReference recipesDBReference;

    private final SharedPreferences defaultSp;
    private final SharedPreferences.OnSharedPreferenceChangeListener defaultSpListener;
    SharedPreferenceFileHandler favorites;
    private final SharedPreferences.OnSharedPreferenceChangeListener spListener;

    public MyViewModel(@NonNull Application application) {
        super(application);

        initializeVariables();

        /*----------------------------------------------------------------*/

        //Get default SP file, there are a preference settings,
        // includes user's preference- to see all recipes or only his favorites.
        defaultSp = PreferenceManager.getDefaultSharedPreferences(application);

        //update flag to current preference value
        favoritesOnlyFlag = defaultSp.getBoolean(application.getString(R.string.favorites), false);

        //Listener for changes in default SP file
        defaultSpListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

                //update flag to new preference value
                favoritesOnlyFlag = prefs.getBoolean(application.getString(R.string.favorites), false);

                //Set LiveData too, in order to wake up the observer in RecipeAdapter
                favoritesOnlyLiveData.setValue(favoritesOnlyFlag);
            }
        };
        defaultSp.registerOnSharedPreferenceChangeListener(defaultSpListener);

        /*----------------------------------------------------------------*/

        //Instance for class that handling with "FavoriteList.xml" Shared preference file (read, write, check...)
        favorites = new SharedPreferenceFileHandler(application,
                application.getString(R.string.preference_favorites_file),
                application.getString(R.string.preference_favorites_key));

        //Listener for changes in favorite file
        spListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                //Get set with favorite recipes's ids
                Set<String> spFavorites = favorites.read();

                //Put all recipes in sp file to favorites list
                favoritesRecipesList.clear();
                recipesList.forEach(recipe -> {
                    if (spFavorites.contains(recipe.getId()))
                        favoritesRecipesList.add(recipe);
                });
                //Set LiveData too, in order to wake up the observer in RecipeAdapter
                favoritesRecipesLiveData.setValue(favoritesRecipesList);
            }
        };
        favorites.getShredPref().registerOnSharedPreferenceChangeListener(spListener);

        /*----------------------------------------------------------------*/

        //Firebase configurations
        fbs = FirebaseService.getInstance();
        recipesDBReference = fbs.getDBReference(RECIPES_DB_NAME);
        recipesDBReference.addChildEventListener(new RecipeEventListener());
    }

    /*----------------------------------------------------------------*/
    private void initializeVariables() {
        recipesList = new ArrayList<>();
        favoritesRecipesList = new ArrayList<>();
        recipesLiveData = new MutableLiveData<>();
        favoritesRecipesLiveData = new MutableLiveData<>();
    }

    /*----------------------------------------------------------------*/
    public LiveData<List<Recipe>> getRecipes() {
        if (recipesLiveData.getValue() == null)
            recipesLiveData.setValue(recipesList);
        return recipesLiveData;
    }

    public LiveData<List<Recipe>> getFavoritesRecipes() {
        if (favoritesRecipesLiveData.getValue() == null) {
            favoritesRecipesLiveData.setValue(favoritesRecipesList);
        }
        return favoritesRecipesLiveData;
    }

    public LiveData<Boolean> getFavoritesOnlyFlag() {
        if (favoritesOnlyLiveData.getValue() == null) {
            favoritesOnlyLiveData.setValue(favoritesOnlyFlag);
        }
        return favoritesOnlyLiveData;
    }

    /*----------------------------------------------------------------*/
    private class RecipeEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Recipe recipe = getRecipeFromSnapshot(dataSnapshot);
            recipesList.add(recipe);
            recipesLiveData.setValue(recipesList);
            if (favorites.contains(recipe.getId())) {
                favoritesRecipesList.add(recipe);
                favoritesRecipesLiveData.setValue(favoritesRecipesList);
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Recipe recipe = getRecipeFromSnapshot(dataSnapshot);
            recipesList.remove(recipe);
            recipesList.add(recipe);
            recipesLiveData.setValue(recipesList);
            if (favorites.contains(recipe.getId())) {
                favoritesRecipesList.remove(recipe);
                favoritesRecipesList.add(recipe);
                favoritesRecipesLiveData.setValue(favoritesRecipesList);
            }

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Recipe recipe = getRecipeFromSnapshot(dataSnapshot);
            recipesList.remove(recipe);
            recipesLiveData.setValue(recipesList);
            if (favorites.contains(recipe.getId())) {
                favoritesRecipesList.remove(recipe);
                favoritesRecipesLiveData.setValue(favoritesRecipesList);
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }

        private Recipe getRecipeFromSnapshot(@NonNull DataSnapshot dataSnapshot) {
            Recipe recipe = dataSnapshot.getValue(Recipe.class);
            recipe.setId(dataSnapshot.getKey());
            return recipe;
        }
    }
}