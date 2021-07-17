package com.example.recipebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.recipebook.Constants.INGREDIENTS_FIELD_NAME;
import static com.example.recipebook.Constants.INSTRUCTIONS_FIELD_NAME;
import static com.example.recipebook.Constants.RECIPES_DB_NAME;
import static com.example.recipebook.Constants.RECIPE_DETAILS;

public class RecipeDetailsActivity extends AppCompatActivity {

    private Recipe recipe;
    private TextView nameTv, descriptionTv;
    private LinearLayout ingredientsLl;
    private LinearLayout instructionsLl;
    private ImageView imageView;
    private Toolbar toolbar;

    private ArrayList<String> ingredientsList = new ArrayList<>();
    private ArrayList<String> instructionsList = new ArrayList<>();

    FirebaseService fbs;
    private DatabaseReference ingredientsFieldReference;
    private DatabaseReference instructionsFieldReference;
    private FloatingActionButton iconView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        findViewsById();

        recipe = getRecipeObject();

        fbs = FirebaseService.getInstance();
        ingredientsFieldReference = getReference(INGREDIENTS_FIELD_NAME);
        instructionsFieldReference = getReference(INSTRUCTIONS_FIELD_NAME);

        setUI();
    }

    /*  ------------------------------------------------    */
    private void findViewsById() {
        nameTv = findViewById(R.id.tv_name_details);
        descriptionTv = findViewById(R.id.tv_description_content_details);
        ingredientsLl = findViewById(R.id.list_view_ingredients_details);
        instructionsLl = findViewById(R.id.list_view_instructions_details);
        imageView = findViewById(R.id.iv_image_details);
        toolbar = findViewById(R.id.tb_details);
        iconView=findViewById(R.id.typeIcon);


    }

    /*  ------------------------------------------------    */
    private Recipe getRecipeObject() {
        Intent intent = getIntent();
        return (Recipe) intent.getSerializableExtra(RECIPE_DETAILS);
    }

    /*  ------------------------------------------------    */
    private DatabaseReference getReference(String fieldName) {
        String path = getPath(fieldName);
        return fbs.getReferenceByPath(path);
    }

    private String getPath(String name) {
        return RECIPES_DB_NAME + "/" + recipe.getId() + "/" + name;
    }

    /*  ------------------------------------------------    */
    private void setUI() {
        nameTv.setText(recipe.getRecipeName());
        descriptionTv.setText(recipe.getDescription());

//        initializeList(this, ingredientsFieldReference, ingredientsList, ingredientsLl);
//        initializeList(this, instructionsFieldReference, instructionsList, instructionsLl);

        //ingredients and instruction setting
        setList(recipe.getIngredients(),ingredientsLl);
        setList(recipe.getInstructions(),instructionsLl);

        //image setting
        Picasso.get().load(recipe.getImageUrl()).into(imageView);

        setToolbar();

        String type=recipe.getType();
        int iconId=R.drawable.undefined_icon;
        if(type.equals("Breakfast"))
            iconId=R.drawable.breakfast_icon;
        else if (type.equals("Lunch"))
            iconId=R.drawable.lunch_icon;
        else if (type.equals("Dinner"))
            iconId=R.drawable.dinner_icon;
        else if (type.equals("Dessert"))
            iconId=R.drawable.sweet_icon;
        iconView.setImageResource(iconId);
    }

    private void setList(ArrayList<String> items, LinearLayout itemsLl) {
        TextView textView;
        for(String item :items){
            textView= getTextView(item);
            itemsLl.addView(textView);
        }
    }

    private TextView getTextView(String item) {
        TextView textView = new TextView(this);
        textView.setText(item);
        TextViewCompat.setTextAppearance(textView, R.style.DetailsContentStyle);
        textView.setPadding(0, 16, 0, 16);
        textView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable
                        (this, R.drawable.ic_baseline_keyboard_arrow_right_24),
                null, null, null);
        textView.setCompoundDrawablePadding(32);
        return textView;
    }
    private void setToolbar() {
        toolbar.setTitle(recipe.getRecipeName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

   /* private void initializeList(Context context, DatabaseReference reference, ArrayList<String> list, LinearLayout linearLayout) {

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String item = snapshot.getValue(String.class);
                list.add(item);
                TextView textView = getIngredientTv(item);
                linearLayout.addView(textView);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                //handle
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
            private TextView getIngredientTv(String item) {
                TextView textView = new TextView(context);
                textView.setText(item);
                TextViewCompat.setTextAppearance(textView, R.style.DetailsContentStyle);
                textView.setPadding(0, 16, 0, 16);
                textView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable
                                (context, R.drawable.ic_baseline_keyboard_arrow_right_24),
                        null, null, null);
                textView.setCompoundDrawablePadding(32);
                return textView;
            }
        });

    }*/
    /*  ------------------------------------------------    */

}
