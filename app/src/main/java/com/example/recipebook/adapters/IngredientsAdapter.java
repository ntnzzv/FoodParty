package com.example.recipebook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipebook.R;

import java.util.ArrayList;

public class IngredientsAdapter extends RecyclerView.Adapter<InstructionsAdapter.InstructionsViewHolder> {
    ArrayList<String> ingredients;

    public IngredientsAdapter(Context context, ArrayList<String> ingredients){
        this.ingredients = ingredients;
    }
    @NonNull
    @Override
    public InstructionsAdapter.InstructionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.instructions_ingridients, parent , false);
        return new InstructionsAdapter.InstructionsViewHolder(view,ingredients.get(0));
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionsAdapter.InstructionsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public static class IngredientsViewHolder extends RecyclerView.ViewHolder{
        EditText dataText;
        public IngredientsViewHolder(@NonNull View itemView,String text) {
            super(itemView);
            dataText = (EditText)itemView.findViewById(R.id.text);
            dataText.setText(text);
        }
    }
}
