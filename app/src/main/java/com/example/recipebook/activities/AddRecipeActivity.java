package com.example.recipebook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.recipebook.adapters.IngredientsAdapter;
import com.example.recipebook.adapters.InstructionsAdapter;
import com.example.recipebook.R;
import com.example.recipebook.viewmodel.RecipesViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
public class AddRecipeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 22;
    RecyclerView instructionsRecycler,ingredientsRecycler;
    InstructionsAdapter instructionsAdapter;
    IngredientsAdapter ingredientsAdapter;
    ArrayList<String> instructions = new ArrayList<>();
    ArrayList<String> ingredients = new ArrayList<>();
    TextInputEditText instructionTextInput,ingredientTextInput;


    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recipe);
        InitializeActivity();
        populateDropdown();
    }

    public void addInstruction() {
        if(!instructionTextInput.getText().toString().isEmpty()) {
            instructions.add(0, instructionTextInput.getText().toString());
            Objects.requireNonNull(instructionsRecycler.getLayoutManager()).scrollToPosition(0);
            instructionsAdapter.notifyItemInserted(0);
        }
    }

    public void addIngredient(){
        if(!ingredientTextInput.getText().toString().isEmpty()){
            ingredients.add(0,ingredientTextInput.getText().toString());
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
}