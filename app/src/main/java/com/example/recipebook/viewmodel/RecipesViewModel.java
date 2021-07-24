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
import com.example.recipebook.firebase.RealTimeDBService;
import com.example.recipebook.utils.SharedPreferenceFileHandler;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class RecipesViewModel extends AndroidViewModel {

    private MutableLiveData<List<User>> usersLiveData;
    private MutableLiveData<List<Recipe>> favoritesRecipesLiveData;
    private MutableLiveData<List<Recipe>> allRecipesLiveData;
    private MutableLiveData<List<Recipe>> searchResultsLiveData;

    private MutableLiveData<Boolean> favoritesOnlyLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> showOnlyMyRecipesLiveData = new MutableLiveData<>();

    List<User> usersList;
    List<Recipe> favoritesRecipesList;
    List<Recipe> allRecipesList;
    List<Recipe> searchResultsList;

    boolean favoritesOnlyFlag;
    boolean showOnlyMyRecipesFlag;

    RealTimeDBService realTimeDBService;
    DatabaseReference usersDBReference;

    private final SharedPreferences defaultSp;
    private final SharedPreferences.OnSharedPreferenceChangeListener defaultSpListener;

    SharedPreferenceFileHandler favorites;
    private final SharedPreferences.OnSharedPreferenceChangeListener spListener;

    public RecipesViewModel(@NonNull Application application) {
        super(application);



        /*----------------------------------------------------------------*/

        //Get default SP file, there are a preference settings,
        // includes user's preference- to see all recipes or only his favorites.
        defaultSp = PreferenceManager.getDefaultSharedPreferences(application);

        //update flag to current preference value
        favoritesOnlyFlag = defaultSp.getBoolean(application.getString(R.string.favorites), false);
        showOnlyMyRecipesFlag = defaultSp.getBoolean(application.getString(R.string.added), false);
        initializeVariables();


        //Listener for changes in default SP file
        defaultSpListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals(application.getString(R.string.favorites))) {

                    //update flag to new preference value
                    favoritesOnlyFlag = prefs.getBoolean(application.getString(R.string.favorites), false);

                    //Set LiveData too, in order to wake up the observer in RecipeAdapter
                    favoritesOnlyLiveData.setValue(favoritesOnlyFlag);
                }
                if (key.equals(application.getString(R.string.added))) {

                    //update flag to new preference value
                    showOnlyMyRecipesFlag = prefs.getBoolean(application.getString(R.string.added), false);

                    //Set LiveData too, in order to wake up the observer in RecipeAdapter
                    showOnlyMyRecipesLiveData.setValue(showOnlyMyRecipesFlag);
                }
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

                //For each user in local list we check his recipes
                allRecipesList.forEach(recipe ->
                {
                    //For each recipe we check if id exists in sp file or no,
                    // and update our favorites list if needed

                    if (userDropsRecipeFromFavorites(spFavorites, recipe))
                        favoritesRecipesList.remove(recipe);

                    if (userAddsRecipeToFavorites(spFavorites, recipe))
                        favoritesRecipesList.add(recipe);
                });
                //Set LiveData too, in order to wake up the observer in RecipeAdapter
                favoritesRecipesLiveData.setValue(favoritesRecipesList);
            }

            private boolean userAddsRecipeToFavorites(Set<String> spFavorites, Recipe recipe) {
                return !favoritesRecipesList.contains(recipe) //not in our local list
                        && spFavorites.contains(recipe.getId());//but exists in sp file as fvorite
            }

            private boolean userDropsRecipeFromFavorites(Set<String> spFavorites, Recipe recipe) {
                return favoritesRecipesList.contains(recipe) //exist in our local list
                        && !spFavorites.contains(recipe.getId()); //but not in sp anymore
            }
        };
        favorites.getShredPref().registerOnSharedPreferenceChangeListener(spListener);

        /*----------------------------------------------------------------*/

        //Firebase configurations
        realTimeDBService = RealTimeDBService.getInstance();
        usersDBReference = realTimeDBService.getReferenceToUsersDB();
        usersDBReference.addChildEventListener(new UserEventListener());
    }


    /*----------------------------------------------------------------*/
    private void initializeVariables() {
        usersList = new ArrayList<>();
        favoritesRecipesList = new ArrayList<>();
        allRecipesList = new ArrayList<>();
        searchResultsList = new ArrayList<>();

        usersLiveData = new MutableLiveData<>();
        favoritesRecipesLiveData = new MutableLiveData<>();
        allRecipesLiveData = new MutableLiveData<>();
        searchResultsLiveData = new MutableLiveData<>();
    }

    /*----------------------------------------------------------------*/
    public LiveData<List<User>> getUsers() {
        if (usersLiveData.getValue() == null)
            usersLiveData.setValue(usersList);
        return usersLiveData;
    }

    public LiveData<List<Recipe>> getFavoritesRecipes() {
        if (favoritesRecipesLiveData.getValue() == null) {
            favoritesRecipesLiveData.setValue(favoritesRecipesList);
        }
        return favoritesRecipesLiveData;
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        if (allRecipesLiveData.getValue() == null) {
            allRecipesLiveData.setValue(allRecipesList);
        }
        return allRecipesLiveData;
    }

    public LiveData<List<Recipe>> getSearchResults() {
        if (searchResultsLiveData.getValue() == null) {
            searchResultsLiveData.setValue(searchResultsList);
        }
        return searchResultsLiveData;
    }

    public LiveData<Boolean> getFavoritesOnlyFlag() {
        if (favoritesOnlyLiveData.getValue() == null) {
            favoritesOnlyLiveData.setValue(favoritesOnlyFlag);
        }
        return favoritesOnlyLiveData;
    }

    public LiveData<Boolean> getShowOnlyMyRecipesFlag() {
        if (showOnlyMyRecipesLiveData.getValue() == null) {
            showOnlyMyRecipesLiveData.setValue(showOnlyMyRecipesFlag);
        }
        return showOnlyMyRecipesLiveData;
    }

    /*----------------------------------------------------------------*/
    private class UserEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(@NonNull DataSnapshot userSnapshot, @Nullable String s) {
            User user = getUserFromSnapshot(userSnapshot);

            updateUserRecipesList(userSnapshot, user);
            usersList.add(user);
            usersLiveData.setValue(usersList);


            allRecipesList.addAll(user.getRecipes());

            user.getRecipes().forEach(recipe ->
                    {
                        if (favorites.contains(recipe.getId()))
                            favoritesRecipesList.add(recipe);
                    }

            );

            favoritesRecipesLiveData.setValue(favoritesRecipesList);
            allRecipesLiveData.setValue(allRecipesList);

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot userSnapshot, @Nullable String s) {
            User user = getUserFromSnapshot(userSnapshot);
            usersList.remove(user);
            updateUserRecipesList(userSnapshot, user);
            usersList.add(user);
            usersLiveData.setValue(usersList);

            allRecipesList.removeIf(recipe -> recipe.getCreatorId().equals(user.getId()));
            allRecipesList.addAll(user.getRecipes());

            favoritesRecipesList.removeIf(recipe -> recipe.getCreatorId().equals(user.getId()));
            user.getRecipes().forEach(recipe ->
                    {
                        if (favorites.contains(recipe.getId()))
                            favoritesRecipesList.add(recipe);
                    }

            );

            //update LiveData
            favoritesRecipesLiveData.setValue(favoritesRecipesList);
            allRecipesLiveData.setValue(allRecipesList);

        }


        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            User user = getUserFromSnapshot(dataSnapshot);
            usersList.remove(user);
            usersLiveData.setValue(usersList);

            allRecipesList.removeIf(recipe -> recipe.getCreatorId().equals(user.getId()));
            favoritesRecipesList.removeIf(recipe -> recipe.getCreatorId().equals(user.getId()));


            favoritesRecipesLiveData.setValue(favoritesRecipesList);
            allRecipesLiveData.setValue(allRecipesList);


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
                recipe.setId(recipeSnapshot.getKey());
                recipe.setCreatorId(userSnapshot.getKey());
                user.getRecipes().add(recipe);
            }


        }
    }

    public void filter(String query) {

        searchResultsList.clear();
        if ( query == null || query.isEmpty()) {
            searchResultsList.addAll(allRecipesList);
        } else {
            query = query.toLowerCase();
            for (Recipe item : allRecipesList) {
                if (item.getDescription().toLowerCase().contains(query) || item.getRecipeName().toLowerCase().contains(query)) {
                    searchResultsList.add(item);
                }
            }
        }
        searchResultsLiveData.setValue(searchResultsList);

    }
}


