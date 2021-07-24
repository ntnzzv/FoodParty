package com.example.recipebook.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipebook.activities.MainActivity;
import com.example.recipebook.firebase.AuthGoogleService;
import com.example.recipebook.viewmodel.RecipesViewModel;
import com.example.recipebook.R;
import com.example.recipebook.entities.Recipe;
import com.example.recipebook.activities.RecipeDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.example.recipebook.utils.Constants.RECIPE_DETAILS;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {

    private final Context context;
    private final RecipesViewModel viewModel;
    List<Recipe> presentedRecipes;

    public RecipesAdapter(Context context, RecipesViewModel viewModel) {

        this.context = context;
        this.viewModel = viewModel;

        presentedRecipes = new ArrayList<>();

        /*---------------OBSERVER-FOR-FAVORITES-ONLY-FLAG-----------------*/
        viewModel.getFavoritesOnlyFlag().observe((LifecycleOwner) context, favoritesOnlyFlag -> {

            //If true we want only favorites recipes in recycler view
            if (favoritesOnlyFlag) {

                //Stop observing for changes on all recipes
                // Else it make a duplicates, so we want one active observer
                viewModel.getAllRecipes().removeObservers((LifecycleOwner) context);

                //Get updated list of favorites recipes - for recyclerview updates
                updatePresentedRecipes(viewModel.getFavoritesRecipes().getValue());

                /*-----OBSERVER-FOR-FAVORITES-RECIPE-LIST-----*/
                viewModel.getFavoritesRecipes().observe((LifecycleOwner) context, favoritesRecipes ->
                        updatePresentedRecipes(favoritesRecipes));


            } else { //Else, we want to show all recipes

                //Stop observing for changes on favorites recipes -
                // Else it make a duplicates, so we want one active observer
                viewModel.getFavoritesRecipes().removeObservers((LifecycleOwner) context);

                //Get updated list of all recipes - for recyclerview updates
                updatePresentedRecipes(viewModel.getAllRecipes().getValue());

                /*-----OBSERVER-FOR-ALL-RECIPES-LIST-----*/
                viewModel.getAllRecipes().observe((LifecycleOwner) context, recipes ->
                        updatePresentedRecipes(recipes));
            }
        });

        /*---------------OBSERVER-FOR-SHOW-ONLY-MY-RECIPES-FLAG-----------*/
        viewModel.getShowOnlyMyRecipesFlag().observe((LifecycleOwner) context, showOnlyMyRecipesFlag -> {
            if (viewModel.getFavoritesOnlyFlag().getValue())
                updatePresentedRecipes(viewModel.getFavoritesRecipes().getValue());
            else
                updatePresentedRecipes(viewModel.getAllRecipes().getValue());

        });

        /*---------------OBSERVER-FOR-SEARCH-RESULTS-LIST-----------------*/
        viewModel.getSearchResults().observe((LifecycleOwner) context, searchResults -> {
            if(MainActivity.search) {
                List<Recipe> filteredRecipes = new ArrayList<>();
                if (viewModel.getFavoritesOnlyFlag().getValue())
                    filteredRecipes.addAll(viewModel.getFavoritesRecipes().getValue());
                else
                    filteredRecipes.addAll(viewModel.getAllRecipes().getValue());
                filteredRecipes.removeIf(recipe -> !searchResults.contains(recipe));
                updatePresentedRecipes(filteredRecipes);
            }


        });
    }


    //Populate list for recycler view with updated list
    private void updatePresentedRecipes(List<Recipe> recipes) {
        presentedRecipes.clear();
        if (viewModel.getShowOnlyMyRecipesFlag().getValue()) {
            recipes.forEach(recipe ->
            {
                if (AuthGoogleService.currentUserCreateThisRecipe(recipe))
                    presentedRecipes.add(recipe);
            });
        } else
            presentedRecipes.addAll(recipes);
        notifyDataSetChanged();

    }



    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.rv_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.setDetails(presentedRecipes.get(position));

        //Handling with click on recipe in rv
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RecipeDetailsActivity.class);
                intent.putExtra(RECIPE_DETAILS, presentedRecipes.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return presentedRecipes.size();
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

            String imgUrl = recipe.getImageUrl();
            if (imgUrl.equals("")) {
                imageView.setImageDrawable(null);
                imageView.setBackgroundResource(R.drawable.no_image);
            } else {
                Picasso.get().load(recipe.getImageUrl()).fit().centerCrop()
                        .placeholder(R.drawable.no_image)
                        .into(imageView);
            }

        }
    }

}
