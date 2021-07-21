package com.example.recipebook.entities;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id="";
    List<Recipe> recipes=new ArrayList<>();

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
}
