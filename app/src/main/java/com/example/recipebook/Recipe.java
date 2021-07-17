package com.example.recipebook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Recipe implements Serializable {
    private String id;
    private String recipeName;
    private String description;
    private String imageUrl;


    //    private String longDescription;
    private ArrayList<String> ingredients = new ArrayList<>();
    private ArrayList<Ingredient> ingredients2 = new ArrayList<>();

    public Recipe() {
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recipe)) return false;
        Recipe recipe = (Recipe) o;
        return getId().equals(recipe.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<Ingredient> getIngredients2() {
        return ingredients2;
    }

    public void setIngredients2(ArrayList<Ingredient> ingredients2) {
        this.ingredients2 = ingredients2;
    }

}
