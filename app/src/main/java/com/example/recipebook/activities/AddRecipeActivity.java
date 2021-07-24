package com.example.recipebook.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.recipebook.adapters.IngredientsAdapter;
import com.example.recipebook.adapters.InstructionsAdapter;
import com.example.recipebook.R;
import com.example.recipebook.broadcastreceivers.BatteryInfoReceiver;
import com.example.recipebook.broadcastreceivers.NetworkStateReceiver;
import com.example.recipebook.entities.Recipe;
import com.example.recipebook.firebase.AuthGoogleService;
import com.example.recipebook.firebase.RealTimeDBService;
import com.example.recipebook.services.UploadImageToCloudService;
import com.example.recipebook.utils.ActivityConstants;
import com.example.recipebook.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

import static com.example.recipebook.utils.Constants.CALLING_ACTIVITY;
import static com.example.recipebook.utils.Constants.FILE_PATH;
import static com.example.recipebook.utils.Constants.INGREDIENTS_FIELD_NAME;
import static com.example.recipebook.utils.Constants.INSTRUCTIONS_FIELD_NAME;
import static com.example.recipebook.utils.Constants.RECIPE_DETAILS;
import static com.example.recipebook.utils.Constants.RECIPE_ID;
import static com.example.recipebook.utils.Constants.USER_UID;


public class AddRecipeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 22;
    private static final String USER_SELECT_IMG = "userSelectImage";

    InstructionsAdapter instructionsAdapter;
    IngredientsAdapter ingredientsAdapter;

    ArrayList<String> instructions = new ArrayList<>();
    ArrayList<String> ingredients = new ArrayList<>();

    RecyclerView instructionsRecycler, ingredientsRecycler;

    TextInputEditText instructionTextInput, ingredientTextInput;
    TextInputEditText descriptionView;
    AutoCompleteTextView typeView;
    EditText recipeNameView;
    ImageView imageView;
    TextInputLayout descriptionLayout, typeLayout, instructionsLayout, ingredientsLayout;

    private Recipe recipe;
    private String descriptionText;
    private String typeText;
    private String recipeNameText;

    private Uri filePath;
    private boolean userSelectImage = false;

    private NetworkStateReceiver netStateReceiver;
    private BatteryInfoReceiver batteryInfoReceiver;
    private int callingActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recipe);

        netStateReceiver = NetworkStateReceiver.getInstance();
        batteryInfoReceiver = BatteryInfoReceiver.getInstance();

        callingActivity = getIntent().getIntExtra(CALLING_ACTIVITY, 0);

        if (callingActivity == ActivityConstants.ACTIVITY_DETAILS)
            recipe = (Recipe) getIntent().getSerializableExtra(RECIPE_DETAILS);
        if (callingActivity == ActivityConstants.ACTIVITY_MAIN)
            recipe = new Recipe();

        findViewsByIds();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        saveUserInput();
        setRecipeDetails();
        outState.putSerializable(RECIPE_DETAILS, recipe);
        outState.putBoolean(USER_SELECT_IMG, userSelectImage);
        if (userSelectImage)
            outState.putParcelable(FILE_PATH, filePath);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        recipe = (Recipe) savedInstanceState.getSerializable(RECIPE_DETAILS);

        userSelectImage = savedInstanceState.getBoolean(USER_SELECT_IMG);

        if (userSelectImage)
            filePath = savedInstanceState.getParcelable(FILE_PATH);
    }

    @Override
    protected void onResume() {
        super.onResume();

        InitializeActivity();
        fillWithExistedData();

        registerReceiver(netStateReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(netStateReceiver);
        unregisterReceiver(batteryInfoReceiver);
    }


    /*--------------------INITIALIZATION------------------------------*/
    public void InitializeActivity() {

        initList(instructions, recipe.getInstructions());
        initList(ingredients, recipe.getIngredients());

        //listener for insert instruction
        final TextInputLayout textInputLayout = findViewById(R.id.textInput_instruction);
        textInputLayout.setEndIconOnClickListener(v -> addInstruction());

        //listener for insert ingredients
        final TextInputLayout ingredientInputLayout = findViewById(R.id.textInput_ingredient);
        ingredientInputLayout.setEndIconOnClickListener(v -> addIngredient());

        //set adapter for instructions recycler view
        instructionsRecycler.setLayoutManager(new LinearLayoutManager(this));
        instructionsAdapter = new InstructionsAdapter(this, instructions, instructionsRecycler);
        instructionsRecycler.setAdapter(instructionsAdapter);

        //set adapter for ingredients recycler view
        ingredientsRecycler.setLayoutManager(new LinearLayoutManager(this));
        ingredientsAdapter = new IngredientsAdapter(this, ingredients, ingredientsRecycler);
        ingredientsRecycler.setAdapter(ingredientsAdapter);

        //for type
        populateDropdown();

    }

    private void findViewsByIds() {
        instructionTextInput = (TextInputEditText) findViewById(R.id.et_instruction);
        ingredientTextInput = (TextInputEditText) findViewById(R.id.et_addIngredient);

        instructionsRecycler = findViewById(R.id.InstructionsRecyclerView);
        ingredientsRecycler = findViewById(R.id.IngredientsRecyclerView);

        descriptionView = (TextInputEditText) findViewById(R.id.description);
        typeView = (AutoCompleteTextView) findViewById(R.id.dropdown);
        recipeNameView = findViewById(R.id.et_recipe_name);

        imageView = findViewById(R.id.UploadedImage);

        descriptionLayout = findViewById(R.id.outlinedTextField);
        typeLayout = findViewById(R.id.spinner_mealType);
        instructionsLayout = findViewById(R.id.textInput_instruction);
        ingredientsLayout = findViewById(R.id.textInput_ingredient);

    }

    public void addInstruction() {
        if (!instructionTextInput.getText().toString().isEmpty()) {
            instructions.add(0, instructionTextInput.getText().toString());
            instructionTextInput.setText("");
            Objects.requireNonNull(instructionsRecycler.getLayoutManager()).scrollToPosition(0);
            instructionsAdapter.notifyItemInserted(0);
        }
    }

    public void addIngredient() {
        if (!ingredientTextInput.getText().toString().isEmpty()) {
            ingredients.add(0, ingredientTextInput.getText().toString());
            ingredientTextInput.setText("");
            Objects.requireNonNull(ingredientsRecycler.getLayoutManager()).scrollToPosition(0);
            ingredientsAdapter.notifyItemInserted(0);
        }
    }


    private void populateDropdown() {

        Recipe.MealType[] options = Recipe.MealType.values();

        ArrayAdapter<Recipe.MealType> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.meal_dropdown_menu_item,
                        options);

        AutoCompleteTextView editTextFilledExposedDropdown =
                findViewById(R.id.dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);
    }

    private void fillWithExistedData() {
        recipeNameView.setText(recipe.getRecipeName());
        descriptionView.setText(recipe.getDescription());
        typeView.setText(typeView.getAdapter().getItem(recipe.getType().ordinal()).toString(), false);

        if (recipe.getImageUrl().equals("")) {
            imageView.setImageDrawable(null);
            if (filePath != null) {
                setImageInHeader();
            }
        } else
            Picasso.get().load(recipe.getImageUrl()).into(imageView);


    }


    private void setImageInHeader() {
        try {
            // Setting image on image view using Bitmap
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            // Log the exception
            e.printStackTrace();
        }
    }

    /*-------------------BUTTONS-HANDLERS-----------------------------*/

    // Select image btn pressed
    public void onSelectImage(View view) {
        // Defining Implicit Intent to mobile gallery
        if (callingActivity == ActivityConstants.ACTIVITY_DETAILS)
            recipe.setImageUrl("");
        userSelectImage = true;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), PICK_IMAGE_REQUEST);
    }

    //save btn pressed
    public void onSave(View view) {

        saveUserInput();

        if (thereAreEmptyFields())
            return;

        new FancyGifDialog.Builder(this)
                .setTitle(R.string.save_dialog_title) // You can also send title like R.string.from_resources
                .setMessage(R.string.save_dialog_msg) // or pass like R.string.description_from_resources
                .setTitleTextColor(R.color.browser_actions_title_color)
                .setDescriptionTextColor(R.color.browser_actions_text_color)
                .setNegativeBtnText(R.string.cancel) // or pass it like android.R.string.cancel
                .setPositiveBtnBackground(R.color.common_google_signin_btn_text_dark)
                .setPositiveBtnText(R.string.submit) // or pass it like android.R.string.ok
                .setNegativeBtnBackground(R.color.purple_200)
                .setGifResource(R.drawable.gif1)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(() -> {
                    if (!NetworkStateReceiver.isOff()) {
                        Toast.makeText(AddRecipeActivity.this, R.string.recipe_submitted, Toast.LENGTH_SHORT).show();

                        String userUid = AuthGoogleService.getInstance().getFirebaseCurrentUser().getUid();

                        if (callingActivity == ActivityConstants.ACTIVITY_MAIN)
                            recipe.setId(UUID.randomUUID().toString());

                        setRecipeDetails();

                        //add new recipe to database
                        RealTimeDBService.getInstance().getReferenceToRecipe(userUid, recipe.getId()).setValue(recipe);

                        //foreground service
                        if (userSelectImage) {
                            Intent intent = new Intent(this, UploadImageToCloudService.class);

                            intent.putExtra(FILE_PATH, filePath);
                            intent.putExtra(RECIPE_ID, recipe.getId());
                            intent.putExtra(USER_UID, userUid);

                            //add image to storage cloud and then update imageUrl in DB
                            startForegroundService(intent);
                        }
                        if (callingActivity == ActivityConstants.ACTIVITY_DETAILS) {
                            Intent intent = new Intent();
                            intent.putExtra(RECIPE_DETAILS, recipe);
                            setResult(RESULT_OK, intent);
                        }

                        finish();

                    } else
                        Toast.makeText(this, "Network OFF, try letter", Toast.LENGTH_LONG).show();

                })
                .OnNegativeClicked(() -> Toast.makeText(AddRecipeActivity.this, "Submission canceled", Toast.LENGTH_SHORT).show())
                .build();

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit? Any entered data will be lost")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddRecipeActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    /*-------------------HELPERS--------------------------------------*/

    private boolean thereAreEmptyFields() {
        descriptionLayout.setErrorEnabled(false);
        typeLayout.setErrorEnabled(false);
        recipeNameView.setError(null);
        instructionsLayout.setErrorEnabled(false);
        ingredientsLayout.setErrorEnabled(false);

        if (descriptionText.isEmpty() || typeText.isEmpty() || recipeNameText.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {

            if (descriptionText.isEmpty()) descriptionLayout.setError("Please enter description");
            if (typeText.isEmpty()) typeLayout.setError("Please choose a type");
            if (recipeNameText.isEmpty()) recipeNameView.setError("Please enter a recipe name");
            if (instructions.isEmpty()) instructionsLayout.setError("Please add instructions");
            if (ingredients.isEmpty()) ingredientsLayout.setError("Please add ingredients");
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private void saveUserInput() {
        descriptionText = descriptionView.getEditableText().toString();
        typeText = typeView.getEditableText().toString();
        recipeNameText = recipeNameView.getEditableText().toString();

    }

    private void setRecipeDetails() {
        Collections.reverse(ingredients);
        Collections.reverse(instructions);
        recipe.setRecipeName(recipeNameText);
        recipe.setDescription(descriptionText);
        recipe.setIngredients(ingredients);
        recipe.setInstructions(instructions);
        recipe.setType(Recipe.MealType.valueOf(typeText));
    }

    private void initList(ArrayList<String> list, ArrayList<String> newList) {
        list.clear();
        list.addAll(newList);
        Collections.reverse(list);
    }

    /*----------------------------------------------------------------*/

    //After user choose an image, it will showed in activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        ImageView image = findViewById(R.id.UploadedImage);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            setImageInHeader();
        } else
            userSelectImage = false;

    }

}