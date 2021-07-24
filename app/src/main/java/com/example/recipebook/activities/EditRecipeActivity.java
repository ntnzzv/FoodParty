package com.example.recipebook.activities;

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
import com.example.recipebook.services.MyForegroundService;
import com.example.recipebook.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static com.example.recipebook.utils.Constants.RECIPE_DETAILS;
import static com.example.recipebook.utils.Constants.RECIPE_ID;
import static com.example.recipebook.utils.Constants.USER_UID;


public class EditRecipeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 22;

    InstructionsAdapter instructionsAdapter;
    IngredientsAdapter ingredientsAdapter;

    ArrayList<String> instructions = new ArrayList<>();
    ArrayList<String> ingredients = new ArrayList<>();

    RecyclerView instructionsRecycler, ingredientsRecycler;

    TextInputEditText instructionTextInput, ingredientTextInput;
    TextInputEditText descriptionView ;
    AutoCompleteTextView typeView ;
    EditText recipeNameView ;
    ImageView imageView;

    private NetworkStateReceiver netStateReceiver;
    private BatteryInfoReceiver batteryInfoReceiver;

    private Uri filePath;

    private boolean userSelectImage =false;//לא לשכוח לטפל במגרה של היפוך מסך (לשמור ערכים )
    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);
        netStateReceiver = new NetworkStateReceiver();

        recipe=(Recipe) getIntent().getSerializableExtra(RECIPE_DETAILS);
        InitializeActivity();

        fillWithExistedData();

        batteryInfoReceiver = new BatteryInfoReceiver();

    }

    private void fillWithExistedData() {
     //   recipe.getInstructions().forEach(this::addInstruction);
     //   recipe.getIngredients().forEach(this::addIngredient);
        recipeNameView.setText(recipe.getRecipeName());
        descriptionView.setText(recipe.getDescription());
        typeView.setText(typeView.getAdapter().getItem(recipe.getType().ordinal()).toString(), false);

        if (recipe.getImageUrl().equals("")) {
            imageView.setImageDrawable(null);
            imageView.setBackgroundResource(R.drawable.no_image);
        } else {
            Picasso.get().load(recipe.getImageUrl()).into(imageView);
        }
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

        instructions.addAll(recipe.getInstructions());
        ingredients.addAll(recipe.getIngredients());
        //listener for insert instruction
        final TextInputLayout textInputLayout = findViewById(R.id.edit_textInput_instruction);
        textInputLayout.setEndIconOnClickListener(v -> addInstruction());

        //listener for insert ingredients
        final TextInputLayout ingredientInputLayout = findViewById(R.id.edit_textInput_ingredient);
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
        instructionTextInput = (TextInputEditText) findViewById(R.id.edit_et_instruction);
        ingredientTextInput = (TextInputEditText) findViewById(R.id.edit_et_addIngredient);

        instructionsRecycler = findViewById(R.id.edit_InstructionsRecyclerView);
        ingredientsRecycler = findViewById(R.id.edit_IngredientsRecyclerView);

         descriptionView = (TextInputEditText) findViewById(R.id.edit_description);
         typeView = (AutoCompleteTextView) findViewById(R.id.edit_dropdown);
         recipeNameView = findViewById(R.id.edit_et_recipe_name);

         imageView = findViewById(R.id.edit_UploadedImage);

    }

    public void addInstruction() {
        if (!instructionTextInput.getText().toString().isEmpty()) {
            instructions.add(0, instructionTextInput.getText().toString());
            instructionTextInput.setText("");
            Objects.requireNonNull(instructionsRecycler.getLayoutManager()).scrollToPosition(0);
            instructionsAdapter.notifyItemInserted(0);
        }
    }
    public void addInstruction(String instruction) {
        if (!instruction.isEmpty()) {
            instructions.add(0, instruction);
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
    public void addIngredient(String ingredient) {
        if (!ingredient.isEmpty()) {
            ingredients.add(0, ingredient);
            ingredientTextInput.setText("");
            Objects.requireNonNull(ingredientsRecycler.getLayoutManager()).scrollToPosition(0);
            ingredientsAdapter.notifyItemInserted(0);
        }
    }
    private void populateDropdown() {

        Recipe.MealType[] options =  Recipe.MealType.values();

        ArrayAdapter<Recipe.MealType> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.meal_dropdown_menu_item,
                        options);

        AutoCompleteTextView editTextFilledExposedDropdown =
                findViewById(R.id.edit_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit? Any entered data will be lost")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditRecipeActivity.super.onBackPressed();
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
        recipe.setImageUrl("");
        imageView.setImageDrawable(null);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), PICK_IMAGE_REQUEST);
    }

    //save btn pressed
    public void onSave(View view) {

        TextInputLayout descriptionLayout = findViewById(R.id.edit_outlinedTextField);
        TextInputLayout typeLayout = findViewById(R.id.edit_spinner_mealType);
        TextInputLayout instructionsLayout = findViewById(R.id.edit_textInput_instruction);
        TextInputLayout ingredientsLayout = findViewById(R.id.edit_textInput_ingredient);



        String descriptionText = descriptionView.getEditableText().toString();
        String typeText = typeView.getEditableText().toString();
        String recipeNameText = recipeNameView.getEditableText().toString();

        String userUid = AuthGoogleService.getInstance().getFirebaseCurrentUser().getUid();

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
                    Toast.makeText(EditRecipeActivity.this, R.string.recipe_submitted, Toast.LENGTH_SHORT).show();

                    Collections.reverse(ingredients);
                    Collections.reverse(instructions);

                     recipe.setRecipeName(recipeNameText);
                     recipe.setDescription( descriptionText);
                     recipe.setIngredients(ingredients);
                     recipe.setInstructions(instructions);
                     recipe.setType(Recipe.MealType.valueOf(typeText));

                    //add new recipe to database
                    RealTimeDBService.getInstance().getReferenceToRecipe(userUid, recipe.getId()).setValue(recipe);

                    //foreground service
                    if(userSelectImage)
                    {
                        //add image to storage cloud and then update imageUrl in DB
                        Intent intent = new Intent(this, MyForegroundService.class);

                        intent.putExtra(Constants.FILE_PATH, filePath);
                        intent.putExtra(RECIPE_ID, recipe.getId());
                        intent.putExtra(USER_UID, userUid);

                        startForegroundService(intent);
                    }


                    Intent intent = new Intent();
                    intent.putExtra(RECIPE_DETAILS, recipe);
                    setResult(RESULT_OK, intent);
                    //back to main activity
                    finish();
                })
                .OnNegativeClicked(() -> Toast.makeText(EditRecipeActivity.this, "Submission canceled", Toast.LENGTH_SHORT).show())
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


        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
        else
            userSelectImage =false;

    }

}