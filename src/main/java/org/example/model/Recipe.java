package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Recipe {

    @JsonProperty("title")
    private String title;

    @JsonProperty("ingredients")
    private String ingredients;

    @JsonProperty("servings")
    private String servings;

    @JsonProperty("instructions")
    private String instructions;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getServings() {
        return servings;
    }

    public void setServings(String servings) {
        this.servings = servings;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

}
