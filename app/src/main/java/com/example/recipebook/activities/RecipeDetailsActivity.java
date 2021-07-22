package com.example.recipebook.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipebook.firebase.RealTimeDBService;
import com.example.recipebook.R;
import com.example.recipebook.entities.Recipe;
import com.example.recipebook.utils.handlers.SharedPreferenceFileHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


import static com.example.recipebook.utils.Constants.INGREDIENTS_FIELD_NAME;
import static com.example.recipebook.utils.Constants.INSTRUCTIONS_FIELD_NAME;
import static com.example.recipebook.utils.Constants.RECIPE_DETAILS;

public class RecipeDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String FAVORITE_TAG = "in_favorites";
    public static final String DEFAULT_TAG = "not_in_favorites";

    private Recipe recipe;
    private TextView nameTv, descriptionTv;
    private LinearLayout ingredientsLl;
    private LinearLayout instructionsLl;
    private ImageView imageView;
    private Toolbar toolbar;
    private FloatingActionButton iconView;
    private FloatingActionButton favoriteBtn;
    private FloatingActionButton editBtn;
    private FloatingActionButton deleteBtn;

    private ArrayList<String> ingredientsList = new ArrayList<>();
    private ArrayList<String> instructionsList = new ArrayList<>();

    RealTimeDBService realTimeDBService;
    private DatabaseReference ingredientsFieldReference;
    private DatabaseReference instructionsFieldReference;


    SharedPreferenceFileHandler favorites;
    private String recipeId, creatorId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        //From Intent
        recipe = getRecipeObject();
        recipeId = recipe.getId();
        creatorId = recipe.getCreatorId();

        //Views initialization
        findViewsByIds();
        setListeners();

        //Create favorite sp handler
        favorites = new SharedPreferenceFileHandler(this,
                getString(R.string.preference_favorites_file),
                getString(R.string.preference_favorites_key));

        //Firebase configurations...
        realTimeDBService = RealTimeDBService.getInstance();
        ingredientsFieldReference = realTimeDBService.getReferenceToRecipeField(creatorId, recipeId, INGREDIENTS_FIELD_NAME);
        instructionsFieldReference = realTimeDBService.getReferenceToRecipeField(creatorId, recipeId, INSTRUCTIONS_FIELD_NAME);

        //UI updating
        InitializeActivity();
    }

    /*  ------------------------------------------------    */
    private void findViewsByIds() {
        nameTv = findViewById(R.id.tv_name_details);
        descriptionTv = findViewById(R.id.tv_description_content_details);
        ingredientsLl = findViewById(R.id.list_view_ingredients_details);
        instructionsLl = findViewById(R.id.list_view_instructions_details);
        imageView = findViewById(R.id.iv_image_details);
        toolbar = findViewById(R.id.toolbar_details);
        iconView = findViewById(R.id.typeIcon);
        favoriteBtn = findViewById(R.id.favorite_button_details);
        editBtn = findViewById(R.id.edit_button_details);
        deleteBtn = findViewById(R.id.delete_button_details);
    }

    private void setListeners() {
        favoriteBtn.setOnClickListener(this);
        editBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
    }

    /*  ------------------------------------------------    */
    private Recipe getRecipeObject() {
        Intent intent = getIntent();
        return (Recipe) intent.getSerializableExtra(RECIPE_DETAILS);
    }


    /*  ------------------------------------------------    */
    private void InitializeActivity() {
        nameTv.setText(recipe.getRecipeName());
        descriptionTv.setText(recipe.getDescription());

        //ingredients and instruction setting
        setList(recipe.getIngredients(), ingredientsLl);
        setList(recipe.getInstructions(), instructionsLl);

        String imgUrl = recipe.getImageUrl();
        if (imgUrl.equals(""))
            imageView.setBackgroundResource(R.drawable.no_image);
        else
            Picasso.get().load(recipe.getImageUrl()).into(imageView);

        setToolbar();

        //set type icon
        int iconId = getIconId(recipe.getType());
        iconView.setImageResource(iconId);

        //favorite button on/off
        if (favorites.contains(recipeId))
            setFavoriteButtonON();
    }


    private void setList(ArrayList<String> items, LinearLayout itemsLl) {
        TextView textView;
        for (String item : items) {
            textView = getTextView(item);
            itemsLl.addView(textView);
        }
    }

    private TextView getTextView(String item) {
        TextView textView = new TextView(this);
        textView.setText(item);
        TextViewCompat.setTextAppearance(textView, R.style.DetailsContentStyle);
        textView.setPadding(0, 16, 0, 16);
        textView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable
                        (this, R.drawable.ic_round_brightness_1_24),
                null, null, null);
        textView.setCompoundDrawablePadding(32);
        return textView;
    }

    private void setToolbar() {

        toolbar.setTitle(recipe.getRecipeName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private int getIconId(String type) {

        if (type.equals("Breakfast"))
            return R.drawable.breakfast_icon;
        else if (type.equals("Lunch"))
            return R.drawable.lunch_icon;
        else if (type.equals("Dinner"))
            return R.drawable.dinner_icon;
        else if (type.equals("Dessert"))
            return R.drawable.sweet_icon;

        return R.drawable.undefined_icon;

    }

    private void setFavoriteButtonOFF() {
        favoriteBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        favoriteBtn.setTag(DEFAULT_TAG);
    }

    private void setFavoriteButtonON() {
        favoriteBtn.setImageResource(R.drawable.ic_baseline_favorite_24);
        favoriteBtn.setTag(FAVORITE_TAG);
    }

    /*  ------------------------------------------------    */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.favorite_button_details:
                handleFavoritesButton();
                break;
            case R.id.edit_button_details:
                //...handle...
                break;
            case R.id.delete_button_details:
                handleDeleteButton();
                break;
            default:
                return;
        }
    }

    private void handleDeleteButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_dialog_title);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete_dialog_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Delete from firebase, listener in view model will updated
                realTimeDBService.getReferenceToRecipe(creatorId, recipeId).removeValue();
                finish();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setIcon(android.R.drawable.ic_menu_delete);

        // Create the alert dialog
        AlertDialog dialog = builder.create();

        // Finally, display the alert dialog
        dialog.show();
        designDeleteButton(dialog);
    }

    private void designDeleteButton(AlertDialog dialog) {

        // Get the alert dialog buttons reference
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        // Change the alert dialog buttons text and background color
        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.light_red));
    }


    private void handleFavoritesButton() {
        if (favoriteBtn.getTag() != null && favoriteBtn.getTag().toString().equals(FAVORITE_TAG)) {
            setFavoriteButtonOFF();
            favorites.remove(recipe.getId());
            Toast.makeText(this, getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT).show();
        } else {
            setFavoriteButtonON();
            favorites.add(recipe.getId());
            Toast.makeText(this, getString(R.string.added_to_favorites), Toast.LENGTH_SHORT).show();
        }
    }

}