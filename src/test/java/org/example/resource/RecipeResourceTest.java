package org.example.resource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.example.model.Recipe;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RecipeResourceTest {
    @Mock
    private HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private Recipe recipeMockResponse;
    String meal = "pizza";
    String allergy = "cheese";


    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        recipeMockResponse = mapper.readValue(getClass().getResource("/recipe_example_response.json"), Recipe.class);
    }

    @Test
    public void testGetRandomRecipeByMeal() throws IOException, InterruptedException {
        HttpResponse response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(200);

        ObjectMapper objectMapper = new ObjectMapper();
        String recipeString = objectMapper.writeValueAsString(recipeMockResponse);
        when(response.body()).thenReturn(recipeString);

        RecipeResource recipeResource = new RecipeResource();

        when(httpClient.send(any(), any())).thenReturn(response);
        Response randomRecipeByMeal = recipeResource.getRecipe(meal);
        Assertions.assertNotNull(randomRecipeByMeal);
        Assertions.assertEquals(1, randomRecipeByMeal.getLength());
    }

    @Test
    public void testGetSortedMealByAllergy() throws IOException, InterruptedException {
        HttpResponse response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(200);

        ObjectMapper objectMapper = new ObjectMapper();
        String recipeString = objectMapper.writeValueAsString(recipeMockResponse);
        when(response.body()).thenReturn(recipeString);

        RecipeResource recipeResource = new RecipeResource();

        when(httpClient.send(any(), any())).thenReturn(response);
        Response randomRecipeByMeal = recipeResource.getSortedRecipes(meal, allergy);
        Assertions.assertNotNull(randomRecipeByMeal);
        List<Recipe> recipes = randomRecipeByMeal.readEntity(new GenericType<List<Recipe>>() {});
        Assertions.assertFalse(recipes.isEmpty());
        Assertions.assertEquals(1, recipes.size());
        Assertions.assertEquals("Emeril's Pizza", recipes.get(0).getTitle());
    }

}