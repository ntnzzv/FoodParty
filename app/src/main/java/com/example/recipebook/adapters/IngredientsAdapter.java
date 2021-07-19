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
import java.util.Objects;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder> {
    Context context;
    ArrayList<String> ingredients;
    RecyclerView ingredientsRecycler;

    public IngredientsAdapter(Context context,ArrayList<String> ingredients,RecyclerView ingredientsRecycler){
        this.context = context;
        this.ingredients = ingredients;
        this.ingredientsRecycler = ingredientsRecycler;
    }

    @NonNull
    @Override
    public IngredientsAdapter.IngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.instructions_ingridients, parent , false);
        return new IngredientsViewHolder(view,ingredients.get(0));
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsAdapter.IngredientsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class IngredientsViewHolder extends RecyclerView.ViewHolder{
        EditText dataText;
        public IngredientsViewHolder(@NonNull View itemView,String text) {
            super(itemView);
            dataText = (EditText)itemView.findViewById(R.id.text);
            dataText.setText(text);

            itemView.findViewById(R.id.delete).setOnClickListener(v -> {
                if(Objects.nonNull(getAdapterPosition())) {
                    ingredients.remove(getAdapterPosition());
                    ingredientsRecycler.removeViewAt(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(), ingredients.size());
                }
            });
        }
    }
}
