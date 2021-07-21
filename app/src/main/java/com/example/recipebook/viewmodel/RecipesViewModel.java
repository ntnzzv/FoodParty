package com.example.recipebook.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

import static com.example.recipebook.utils.Constants.USERS_DB_NAME;

public class RecipesViewModel extends AndroidViewModel {
    private MutableLiveData<List<User>> userLiveData;
    private MutableLiveData<List<Recipe>> favoritesRecipesLiveData;
    private MutableLiveData<Boolean> favoritesOnlyLiveData = new MutableLiveData<>();

    List<User> usersList;

    List<Recipe> favoritesRecipesList;
    boolean favoritesOnlyFlag;

    RealTimeDBService realTimeDBService;
    DatabaseReference recipesDBReference;

//    private final SharedPreferences defaultSp;
//    private final SharedPreferences.OnSharedPreferenceChangeListener defaultSpListener;
    SharedPreferenceFileHandler favorites;
  //  private final SharedPreferences.OnSharedPreferenceChangeListener spListener;

    public RecipesViewModel(@NonNull Application application) {
        super(application);

        initializeVariables();

        /*----------------------------------------------------------------*/

        //Get default SP file, there are a preference settings,
        // includes user's preference- to see all recipes or only his favorites.
    /*    defaultSp = PreferenceManager.getDefaultSharedPreferences(application);

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
*/
        /*----------------------------------------------------------------*/

        //Instance for class that handling with "FavoriteList.xml" Shared preference file (read, write, check...)
    /*    favorites = new SharedPreferenceFileHandler(application,
                application.getString(R.string.preference_favorites_file),
                application.getString(R.string.preference_favorites_key));
*/
        //Listener for changes in favorite file
       /* spListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                //Get set with favorite recipes's ids
                Set<String> spFavorites = favorites.read();

                //Put all recipes in sp file to favorites list
                favoritesRecipesList.clear();
                usersList.forEach(recipe -> {
                    if (spFavorites.contains(recipe.getId()))
                        favoritesRecipesList.add(recipe);
                });
                //Set LiveData too, in order to wake up the observer in RecipeAdapter
                favoritesRecipesLiveData.setValue(favoritesRecipesList);
            }
        };
        favorites.getShredPref().registerOnSharedPreferenceChangeListener(spListener);
*/
        /*----------------------------------------------------------------*/

        //Firebase configurations
        realTimeDBService = RealTimeDBService.getInstance();
        recipesDBReference = realTimeDBService.getDBReference(USERS_DB_NAME);
        recipesDBReference.addChildEventListener(new UserEventListener());
    }

    /*----------------------------------------------------------------*/
    private void initializeVariables() {
        usersList = new ArrayList<>();
        favoritesRecipesList = new ArrayList<>();
        userLiveData = new MutableLiveData<>();
        favoritesRecipesLiveData = new MutableLiveData<>();
    }

    /*----------------------------------------------------------------*/
    public LiveData<List<User>> getRecipes() {
        if (userLiveData.getValue() == null)
            userLiveData.setValue(usersList);
        return userLiveData;
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
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            User user = getUserFromSnapshot(dataSnapshot);
            for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
          //      user.setRecipes(dataSnapshot1.getValue(Recipe.class));

            }
            usersList.add(user);
            userLiveData.setValue(usersList);
//            if (favorites.contains(user.getId())) {
//                favoritesRecipesList.add(user);
//                favoritesRecipesLiveData.setValue(favoritesRecipesList);
            //      }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            User user = getUserFromSnapshot(dataSnapshot);
            usersList.remove(user);
            usersList.add(user);
            userLiveData.setValue(usersList);
//            if (favorites.contains(user.getId())) {
//                favoritesRecipesList.remove(user);
//                favoritesRecipesList.add(user);
//                favoritesRecipesLiveData.setValue(favoritesRecipesList);
//            }

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            User user = getUserFromSnapshot(dataSnapshot);
            usersList.remove(user);
            userLiveData.setValue(usersList);
//            if (favorites.contains(user.getId())) {
//                favoritesRecipesList.remove(user);
//                favoritesRecipesLiveData.setValue(favoritesRecipesList);
            // }
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
    }
}


