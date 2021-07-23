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
import com.example.recipebook.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

import static com.example.recipebook.utils.Constants.FILE_PATH;
import static com.example.recipebook.utils.Constants.RECIPE_ID;
import static com.example.recipebook.utils.Constants.USER_UID;


public class AddRecipeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 22;

    RecyclerView instructionsRecycler, ingredientsRecycler;

    InstructionsAdapter instructionsAdapter;
    IngredientsAdapter ingredientsAdapter;

    ArrayList<String> instructions = new ArrayList<>();
    ArrayList<String> ingredients = new ArrayList<>();

    TextInputEditText instructionTextInput, ingredientTextInput;
    private NetworkStateReceiver netStateReceiver;

    private Uri filePath;

    private BatteryInfoReceiver batteryInfoReceiver;
    private boolean userSelectImage =false;//לא לשכוח לטפל במגרה של היפוך מסך (לשמור ערכים )

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recipe);
        netStateReceiver = new NetworkStateReceiver();
        if(savedInstanceState != null){
            instructions = (ArrayList<String>) savedInstanceState.get("instructions");
            ingredients = (ArrayList<String>)savedInstanceState.get("ingredients");
        }
        InitializeActivity();

        batteryInfoReceiver = new BatteryInfoReceiver();


    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(netStateReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        //create intent filter and register receiver to get battery info changes
        registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(netStateReceiver);

        //unregister receivers
        unregisterReceiver(batteryInfoReceiver);
    }

    /*----------------------------------------------------------------*/
    public void InitializeActivity() {
        findViewsByIds();

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

        populateDropdown();
    }

    private void findViewsByIds() {
        instructionTextInput = (TextInputEditText) findViewById(R.id.et_instruction);
        ingredientTextInput = (TextInputEditText) findViewById(R.id.et_addIngredient);

        instructionsRecycler = findViewById(R.id.InstructionsRecyclerView);
        ingredientsRecycler = findViewById(R.id.IngredientsRecyclerView);
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("ingredients",ingredients);
        outState.putStringArrayList("instructions",instructions);

    }

    private void populateDropdown() {

        Recipe.MealType[] options =  Recipe.MealType.values();

        ArrayAdapter<Recipe.MealType> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.meal_dropdown_menu_item,
                        options);

        AutoCompleteTextView editTextFilledExposedDropdown =
                findViewById(R.id.dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);
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

    /*-------------------BUTTONS-HANDLERS-----------------------------*/

    // Select image btn pressed
    public void onSelectImage(View view) {
        // Defining Implicit Intent to mobile gallery
        userSelectImage =true;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), PICK_IMAGE_REQUEST);
    }

    //save btn pressed
    public void onSave(View view) {

        TextInputLayout description = findViewById(R.id.outlinedTextField);
        String descriptionText = ((TextInputEditText) findViewById(R.id.description)).getEditableText().toString();
        TextInputLayout type = findViewById(R.id.spinner_mealType);
        String typeText = ((AutoCompleteTextView) findViewById(R.id.dropdown)).getEditableText().toString();
        EditText recipeName = findViewById(R.id.et_recipe_name);
        String recipeNameText = recipeName.getEditableText().toString();
        String userUid = AuthGoogleService.getInstance().getFirebaseCurrentUser().getUid();
        TextInputLayout instructionsLayout = findViewById(R.id.textInput_instruction);
        TextInputLayout ingredientsLayout = findViewById(R.id.textInput_ingredient);

        description.setErrorEnabled(false);
        type.setErrorEnabled(false);
        recipeName.setError(null);
        instructionsLayout.setErrorEnabled(false);
        ingredientsLayout.setErrorEnabled(false);

        if (descriptionText.isEmpty() || typeText.isEmpty() || recipeNameText.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {

            if (descriptionText.isEmpty()) description.setError("Please enter description");
            if (typeText.isEmpty()) type.setError("Please choose a type");
            if (recipeNameText.isEmpty()) recipeName.setError("Please enter a recipe name");
            if (instructions.isEmpty()) instructionsLayout.setError("Please add instructions");
            if (ingredients.isEmpty()) ingredientsLayout.setError("Please add ingredients");
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_LONG).show();
            return;
        }

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
                    Toast.makeText(AddRecipeActivity.this, R.string.recipe_submitted, Toast.LENGTH_SHORT).show();

                    Collections.reverse(ingredients);
                    Collections.reverse(instructions);

                    Recipe recipe = new Recipe(recipeNameText, descriptionText, ingredients, instructions, typeText);
                    String recipeId=UUID.randomUUID().toString();
                    //add new recipe to database
                    RealTimeDBService.getInstance().getReferenceToRecipe(userUid, recipeId).setValue(recipe);

                    //foreground service
                    if(userSelectImage)
                    {
                        Intent intent = new Intent(this, UploadImageToCloudService.class);

                        intent.putExtra(FILE_PATH, filePath);
                        intent.putExtra(RECIPE_ID, recipeId);
                        intent.putExtra(USER_UID, userUid);

                        startForegroundService(intent);
                    }

                    //add image to storage cloud and then update imageUrl in DB
                    //  ImageHandler.UploadImage( filePath, userUid, recipeNameText);

                    //back to main activity
                    finish();
                })
                .OnNegativeClicked(() -> Toast.makeText(AddRecipeActivity.this, "Submission canceled", Toast.LENGTH_SHORT).show())
                .build();

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
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
        else
            userSelectImage =false;

    }

}