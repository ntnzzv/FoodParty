package com.example.recipebook.AddRecipe;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IngridientsAdapter extends RecyclerView.Adapter<IngridientsAdapter.IngridientsViewHolder> {


    @NonNull
    @Override
    public IngridientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull IngridientsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public class IngridientsViewHolder extends RecyclerView.ViewHolder{

        public IngridientsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
