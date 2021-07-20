package com.example.recipebook.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.recipebook.entities.Recipe;
import com.example.recipebook.utils.Authentication;
import com.example.recipebook.utils.FirebaseService;
import com.example.recipebook.utils.ImageHandler;
import com.example.recipebook.utils.Instances;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class AddRecipeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 22;
    RecyclerView instructionsRecycler,ingredientsRecycler;
    InstructionsAdapter instructionsAdapter;
    IngredientsAdapter ingredientsAdapter;
    ArrayList<String> instructions = new ArrayList<>();
    ArrayList<String> ingredients = new ArrayList<>();
    TextInputEditText instructionTextInput,ingredientTextInput;

    private Uri filePath;

    private BatteryInfoReceiver batteryInfoReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recipe);
        InitializeActivity();
        populateDropdown();

        batteryInfoReceiver =new BatteryInfoReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //create intent filter and register receiver to get battery info changes
        registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();

        //unregister receivers
        unregisterReceiver(batteryInfoReceiver);
    }

    public void addInstruction() {
        if(!instructionTextInput.getText().toString().isEmpty()) {
            instructions.add(0, instructionTextInput.getText().toString());
            instructionTextInput.setText("");
            Objects.requireNonNull(instructionsRecycler.getLayoutManager()).scrollToPosition(0);
            instructionsAdapter.notifyItemInserted(0);
        }
    }

    public void addIngredient(){
        if(!ingredientTextInput.getText().toString().isEmpty()){
            ingredients.add(0,ingredientTextInput.getText().toString());
            ingredientTextInput.setText("");
            Objects.requireNonNull(ingredientsRecycler.getLayoutManager()).scrollToPosition(0);
            ingredientsAdapter.notifyItemInserted(0);
        }
    }

    private void populateDropdown(){

        String[] options = new String[] {"Breakfast", "Brunch", "Dinner", "Desert"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.meal_dropdown_menu_item,
                        options);

        AutoCompleteTextView editTextFilledExposedDropdown =
                findViewById(R.id.dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
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
        }
        try {
            // Setting image on image view using Bitmap
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            image.setImageBitmap(bitmap);
        }

        catch (IOException e) {
            // Log the exception
            e.printStackTrace();
        }
    }

    // Select Image method
    public void SelectImage(View view)
    {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."),PICK_IMAGE_REQUEST);

    }

    // UploadImage method
    public void InitializeActivity()
    {

        instructionTextInput = (TextInputEditText)findViewById(R.id.et_instruction);
        ingredientTextInput = (TextInputEditText)findViewById(R.id.et_addIngredient);

        instructionsRecycler = findViewById(R.id.InstructionsRecyclerView);
        ingredientsRecycler = findViewById(R.id.IngredientsRecyclerView);

        final TextInputLayout textInputLayout = findViewById(R.id.textInput_instruction);
        textInputLayout.setEndIconOnClickListener(v -> addInstruction());

        final TextInputLayout ingredientInputLayout = findViewById(R.id.textInput_ingredient);
        ingredientInputLayout.setEndIconOnClickListener(v -> addIngredient());

        instructionsRecycler.setLayoutManager(new LinearLayoutManager(this));
        instructionsAdapter = new InstructionsAdapter(this,instructions,instructionsRecycler);
        instructionsRecycler.setAdapter(instructionsAdapter);
        ingredientsRecycler.setLayoutManager(new LinearLayoutManager(this));
        ingredientsAdapter = new IngredientsAdapter(this,ingredients,ingredientsRecycler);
        ingredientsRecycler.setAdapter(ingredientsAdapter);

    }

    public void Save(View view) {
        new FancyGifDialog.Builder(this)
                .setTitle("Almost done!") // You can also send title like R.string.from_resources
                .setMessage("Have your forgot to add something? press Cancel Otherwise press Submit") // or pass like R.string.description_from_resources
                .setTitleTextColor(R.color.browser_actions_title_color)
                .setDescriptionTextColor(R.color.browser_actions_text_color)
                .setNegativeBtnText("Cancel") // or pass it like android.R.string.cancel
                .setPositiveBtnBackground(R.color.common_google_signin_btn_text_dark)
                .setPositiveBtnText("Submit") // or pass it like android.R.string.ok
                .setNegativeBtnBackground(R.color.purple_200)
                .setGifResource(R.drawable.gif1)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(() ->{
                    Toast.makeText(AddRecipeActivity.this,"Recipe submitted!",Toast.LENGTH_SHORT).show();
                    Recipe recipe = new Recipe();
                    String description = ((TextInputEditText)findViewById(R.id.description)).getEditableText().toString();
                    String type = ((AutoCompleteTextView)findViewById(R.id.dropdown)).getText().toString();
                    String recipeNAme = ((EditText)findViewById(R.id.et_recipe_name)).getText().toString();
                    String userUid = Instances.currentUser.getUid();
                    recipe.setIngredients(ingredients);
                    recipe.setInstructions(instructions);
                    recipe.setDescription(description);
                    recipe.setRecipeName(recipeNAme);
                    recipe.setType(type);
                    FirebaseService.getInstance().getDBReference("Recipes/").child(userUid).child(recipeNAme).setValue(recipe);

                    ImageHandler.UploadImage(this,this,filePath,userUid,recipeNAme);



                })
                .OnNegativeClicked(() -> Toast.makeText(AddRecipeActivity.this,"Submission canceled",Toast.LENGTH_SHORT).show())
                .build();
    }
}