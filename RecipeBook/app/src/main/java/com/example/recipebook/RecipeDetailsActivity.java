package com.example.recipebook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.recipebook.Constants.INGREDIENTS_DB_NAME;
import static com.example.recipebook.Constants.INSTRUCTIONS_DB_NAME;
import static com.example.recipebook.Constants.RECIPES_DB_NAME;
import static com.example.recipebook.Constants.RECIPE_DETAILS;

public class RecipeDetailsActivity extends AppCompatActivity {


    private Recipe recipe;
    private TextView nameTv, descriptionTv;
    private LinearLayout ingredientsLl;
    private LinearLayout instructionsLl;
    private ImageView imageView;

    private ArrayList<String> ingredientsList = new ArrayList<>();
    private ArrayList<String> instructionsList = new ArrayList<>();

    FirebaseService fbs;
    private DatabaseReference ingredientsDBReference;
    private DatabaseReference instructionsDBReference;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        findViewsById();
        setRecipeObject();
        setDBReferences();
        setUI();


    }

    private void findViewsById() {
        nameTv = findViewById(R.id.tv_name_details);
        descriptionTv = findViewById(R.id.tv_description_content_details);
        ingredientsLl = findViewById(R.id.list_view_ingredients_details);
        instructionsLl = findViewById(R.id.list_view_instructions_details);
        imageView = findViewById(R.id.iv_image_details);
        toolbar = findViewById(R.id.tb_details);
    }

    private void setUI() {
        nameTv.setText(recipe.getRecipeName());
        descriptionTv.setText(recipe.getDescription());
        initializeListView(this, ingredientsDBReference, ingredientsList, ingredientsLl);
        initializeListView(this, instructionsDBReference, instructionsList, instructionsLl);
        Picasso.get().load(recipe.getImageUrl()).into(imageView);

        toolbar.setTitle(recipe.getRecipeName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setRecipeObject() {
        Intent intent = getIntent();
        recipe = (Recipe) intent.getSerializableExtra(RECIPE_DETAILS);
    }

    private void setDBReferences() {
        fbs = FirebaseService.getInstance();
        ingredientsDBReference = fbs.getSpecificDBReference(RECIPES_DB_NAME)
                .child(recipe.getId())
                .child(INGREDIENTS_DB_NAME);
        instructionsDBReference = fbs.getSpecificDBReference(RECIPES_DB_NAME)
                .child(recipe.getId())
                .child(INSTRUCTIONS_DB_NAME);
    }

    private void initializeListView(Context context, DatabaseReference reference, ArrayList<String> list, LinearLayout linearLayout) {

        // in below line we are calling method for add child event
        // listener to get the child of our database.
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String item = snapshot.getValue(String.class);
                list.add(item);
                TextView textView = new TextView(context);
                textView.setText(item);
                TextViewCompat.setTextAppearance(textView, R.style.DetailsContentStyle);
                textView.setPadding(0, 16, 0, 16);
                textView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable
                                (context, R.drawable.ic_baseline_keyboard_arrow_right_24),
                        null, null, null);
                textView.setCompoundDrawablePadding(32);
                linearLayout.addView(textView);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String item = snapshot.getValue(String.class);
                list.remove(item);
                //handle
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

}
