package com.example.recipebook.AddRecipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.example.recipebook.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;

public class AddRecipeActivity extends AppCompatActivity {

    RecyclerView instructionsRecycler,ingredientsRecycler;
    InstructionsAdapter instructionsAdapter,ingredientsAdapter;
    ArrayList<String> instructions = new ArrayList<>();
    ArrayList<String> ingredients = new ArrayList<>();
    TextInputEditText instructionTextInput,ingredientTextInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recipe);

        instructionTextInput = (TextInputEditText)findViewById(R.id.et_instruction);
        ingredientTextInput = (TextInputEditText)findViewById(R.id.et_addIngredient);

        instructionsRecycler = findViewById(R.id.InstructionsRecyclerView);
        ingredientsRecycler = findViewById(R.id.IngredientsRecyclerView);
        final TextInputLayout textInputLayout = findViewById(R.id.textInput_instruction);
        textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInstruction();
            }
        });

        final TextInputLayout ingredientInputLayout = findViewById(R.id.textInput_ingredient);
        ingredientInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredient();
            }
        });

        instructionsRecycler.setLayoutManager(new LinearLayoutManager(this));
        instructionsAdapter = new InstructionsAdapter(this,instructions);
        instructionsRecycler.setAdapter(instructionsAdapter);

        ingredientsRecycler.setLayoutManager(new LinearLayoutManager(this));
        ingredientsAdapter= new InstructionsAdapter(this,ingredients);
        ingredientsRecycler.setAdapter(ingredientsAdapter);

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
}