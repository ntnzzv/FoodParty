package com.example.recipebook;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;


public class MainActivity extends AppCompatActivity {

    RecipesAdapter adapter;
    MyViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MyViewModel.class);

        setRecyclerViewAdapter();
    }

    private void setRecyclerViewAdapter() {
        RecyclerView rv = findViewById(R.id.rv_recipes_list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipesAdapter(this, viewModel);
        rv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searchItem:
                //handle search
                return true;
            case R.id.settingsItem:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }
}