package com.example.recipebook;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.example.recipebook.Constants.RECIPE_DETAILS;

public class RecipesAdapter extends FirebaseRecyclerAdapter<Recipe, RecipesAdapter.RecipeViewHolder> {

    private final Context context;
    List<Recipe> recipes = new ArrayList<>();

    public RecipesAdapter(Context context, @NonNull FirebaseRecyclerOptions<Recipe> options) {
        super(options);
        this.context = context;

    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.rv_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecipeViewHolder holder, int position, @NonNull Recipe model) {
        holder.setDetails(model);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RecipeDetailsActivity.class);
                intent.putExtra(RECIPE_DETAILS,recipes.get(position));
                context.startActivity(intent);
            }
        });
    }


    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv;
        TextView shortDescriptionTv;
        ImageView imageView;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.tv_name_item);
            shortDescriptionTv = itemView.findViewById(R.id.tv_description_item);
            imageView = itemView.findViewById(R.id.iv_image_item);
        }

        public void setDetails(Recipe recipe) {
            nameTv.setText(recipe.getRecipeName());
            shortDescriptionTv.setText(recipe.getDescription());
            Picasso.get().load(recipe.getImageUrl()).into(imageView);
        }
    }

}
