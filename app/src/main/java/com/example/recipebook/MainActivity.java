package com.example.recipebook;


import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import static com.example.recipebook.Constants.RECIPES_DB_NAME;

public class MainActivity extends AppCompatActivity {

    FirebaseService fbs;
    DatabaseReference recipesDBReference;
    private RecipesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fbs = FirebaseService.getInstance();
        recipesDBReference = fbs.getDBReference(RECIPES_DB_NAME);
        setRecyclerViewAdapter();
        recipesDBReference.addChildEventListener(new RecipeEventListener());
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void setRecyclerViewAdapter() {
        RecyclerView rv = findViewById(R.id.rv_recipes_list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerOptions<Recipe> options = new FirebaseRecyclerOptions.Builder<Recipe>()
                .setQuery(recipesDBReference, Recipe.class)
                .build();
        adapter = new RecipesAdapter(this, options);
        rv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        Log.i(Constants.TAG, Constants.MAIN + "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.i(Constants.TAG, Constants.MAIN + "onOptionsItemSelected()");
        switch (item.getItemId()) {
            case R.id.searchItem:
                //handle search
                return true;
            case R.id.settingsItem:
                //handle settings
                return true;
            default:
                return false;
        }
    }

    private class RecipeEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Recipe recipe = dataSnapshot.getValue(Recipe.class);
            recipe.setId(dataSnapshot.getKey());
            adapter.recipes.add(recipe);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Recipe recipe = dataSnapshot.getValue(Recipe.class);
            recipe.setId(dataSnapshot.getKey());
            adapter.recipes.remove(recipe);
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }
}