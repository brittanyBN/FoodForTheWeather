package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RecipeList {

    @JsonProperty("recipes")
    private List<Recipe> recipes;

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
}
