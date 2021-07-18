package com.example.recipebook.AddRecipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.recipebook.R;

import java.util.ArrayList;
import java.util.Objects;

public class AddRecipeActivity extends AppCompatActivity {

    RecyclerView instructionsRecycler,ingridients;
    InstructionsAdapter instructionsAdapter;
    ArrayList<String> instructions = new ArrayList<>();
    EditText instruction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recipe);

        instruction = (EditText)findViewById(R.id.et_instruction);
        instructionsRecycler = findViewById(R.id.InstructionsRecyclerView);


        instructionsRecycler.setLayoutManager(new LinearLayoutManager(this));

        instructionsAdapter = new InstructionsAdapter(this,instructions);

        instructionsRecycler.setAdapter(instructionsAdapter);

    }

    public void addInstruction(View view) {
        instructions.add(0,instruction.getText().toString());
        Objects.requireNonNull(instructionsRecycler.getLayoutManager()).scrollToPosition(0);
        instructionsAdapter.notifyItemInserted(0);
    }
}