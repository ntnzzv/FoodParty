package com.example.recipebook.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Recipe implements Serializable {
    private String id="";
    private String creatorId="";

    private String recipeName="";
    private String description="";
    private String imageUrl="";
    private ArrayList<String> ingredients = new ArrayList<>();
    private ArrayList<String> instructions = new ArrayList<>();
    private String type="";

    public Recipe(String recipeName, String description, ArrayList<String> ingredients, ArrayList<String> instructions, String type) {
        this.recipeName = recipeName;
        this.description = description;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.type = type;
    }

    public Recipe() {
    }


    /*------------------------SETTERS---------------------------------*/
    public void setId(String id) {
        this.id = id;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setInstructions(ArrayList<String> instructions) {
        this.instructions = instructions;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    /*------------------------GETTERS---------------------------------*/
    public String getId() {
        return id;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public String getType() {
        return type;
    }

    public ArrayList<String> getInstructions() {
        return instructions;
    }

    public String getCreatorId() {
        return creatorId;
    }
    /*----------------------------------------------------------------*/

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
    /*----------------------------------------------------------------*/



}
