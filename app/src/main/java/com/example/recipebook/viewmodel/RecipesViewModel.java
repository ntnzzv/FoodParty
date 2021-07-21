package com.example.recipebook.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.example.recipebook.R;
import com.example.recipebook.entities.Recipe;
import com.example.recipebook.entities.User;
import com.example.recipebook.utils.RealTimeDBService;
import com.example.recipebook.utils.SharedPreferenceFileHandler;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.example.recipebook.utils.Constants.USERS_DB_NAME;

public class RecipesViewModel extends AndroidViewModel {
    private MutableLiveData<List<User>> recipesLiveData;
    private MutableLiveData<List<Recipe>> favoritesRecipesLiveData;
    private MutableLiveData<Boolean> favoritesOnlyLiveData = new MutableLiveData<>();

    List<User> userList;

    List<Recipe> favoritesRecipesList;
    boolean favoritesOnlyFlag;

    RealTimeDBService realTimeDBService;
    DatabaseReference usersDBReference;

    private final SharedPreferences defaultSp;
private final SharedPreferences.OnSharedPreferenceChangeListener defaultSpListener;
    SharedPreferenceFileHandler favorites;
    private final SharedPreferences.OnSharedPreferenceChangeListener spListener;

    public RecipesViewModel(@NonNull Application application) {
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
                for(User user: userList) {
                    user.getRecipes().forEach(recipe -> {
                        if (spFavorites.contains(recipe.getId()))
                            favoritesRecipesList.add(recipe);
                    });
                }
                //Set LiveData too, in order to wake up the observer in RecipeAdapter
                favoritesRecipesLiveData.setValue(favoritesRecipesList);
            }
        };
        favorites.getShredPref().registerOnSharedPreferenceChangeListener(spListener);

        /*----------------------------------------------------------------*/

        //Firebase configurations
        realTimeDBService = RealTimeDBService.getInstance();
        usersDBReference = realTimeDBService.getDBReference(USERS_DB_NAME);
        usersDBReference.addChildEventListener(new UserEventListener());
    }

    /*----------------------------------------------------------------*/
    private void initializeVariables() {
        userList = new ArrayList<>();
        favoritesRecipesList = new ArrayList<>();
        recipesLiveData = new MutableLiveData<>();
        favoritesRecipesLiveData = new MutableLiveData<>();
    }

    /*----------------------------------------------------------------*/
    public LiveData<List<User>> getUsers() {
        if (recipesLiveData.getValue() == null)
            recipesLiveData.setValue(userList);
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
    private class UserEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(@NonNull DataSnapshot userSnapshot, @Nullable String s) {
            User user = getUserFromSnapshot(userSnapshot);
            updateUserRecipesList(userSnapshot, user);
            userList.add(user);
            recipesLiveData.setValue(userList);

            for (Recipe recipe : user.getRecipes())
                if (favorites.contains(recipe.getId()))
                    favoritesRecipesList.add(recipe);
            favoritesRecipesLiveData.setValue(favoritesRecipesList);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot userSnapshot, @Nullable String s) {
            User user = getUserFromSnapshot(userSnapshot);
            userList.remove(user);
            updateUserRecipesList(userSnapshot, user);
            userList.add(user);
            recipesLiveData.setValue(userList);


            for (Recipe recipe : user.getRecipes())
                if (favorites.contains(recipe.getId())) {
                    favoritesRecipesList.remove(recipe);
                    favoritesRecipesList.add(recipe);
                }
            favoritesRecipesLiveData.setValue(favoritesRecipesList);


        }


        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            User user = getUserFromSnapshot(dataSnapshot);
            userList.remove(user);
            recipesLiveData.setValue(userList);

            for (Recipe recipe : user.getRecipes())
                if (favorites.contains(recipe.getId()))
                    favoritesRecipesList.remove(recipe);
            favoritesRecipesLiveData.setValue(favoritesRecipesList);


        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }

        private User getUserFromSnapshot(@NonNull DataSnapshot dataSnapshot) {
            User user = dataSnapshot.getValue(User.class);
            user.setId(dataSnapshot.getKey());
            return user;
        }

        private void updateUserRecipesList(@NonNull DataSnapshot userSnapshot, User user) {
            for (DataSnapshot recipeSnapshot : userSnapshot.getChildren()) {
                Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                recipe.setDescription(recipeSnapshot.getKey());
                user.getRecipes().add(recipe);
            }
        }
    }
}


