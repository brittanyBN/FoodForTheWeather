package org.example.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.model.Recipe;
import org.example.model.Weather;
import org.example.util.FoodForWeather;
import org.example.util.HttpClientSingleton;
import org.example.util.ObjectMapperSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Random;

@Path("/suggestions")
@Produces(MediaType.APPLICATION_JSON)
public class SuggestionResource {
    private static final Logger LOG = LoggerFactory.getLogger(RecipeResource.class);

    @GET
    @Path("/{city}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecipe(@PathParam("city") String city) {
        try {
            WeatherResource weatherUrl = new WeatherResource();
            HttpRequest weatherRequest = weatherUrl.getWeatherUrl(city);

            HttpResponse<String> httpWeatherResponse = HttpClientSingleton.httpClient.send(weatherRequest, HttpResponse.BodyHandlers.ofString());

            String responseBody = httpWeatherResponse.body();
            Weather weather = ObjectMapperSingleton.mapper.readValue(responseBody, Weather.class);
            int minTemp = weather.getMinTemp();
            int maxTemp = weather.getMaxTemp();
            FoodForWeather foodForWeather = new FoodForWeather();
            String mealType = foodForWeather.getMealType(minTemp, maxTemp);

            RecipeResource recipeUrl = new RecipeResource();
            HttpRequest recipeRequest = recipeUrl.getRecipeUrl(mealType);

            HttpResponse<String> httpRecipeResponse = HttpClientSingleton.httpClient.send(recipeRequest, HttpResponse.BodyHandlers.ofString());

            if (httpRecipeResponse.statusCode() == 200 && httpWeatherResponse.statusCode() == 200) {
                String recipeResponseBody = httpRecipeResponse.body();
                List<Recipe> recipes = ObjectMapperSingleton.mapper.readValue(recipeResponseBody, new TypeReference<>() {
                });

                if (!recipes.isEmpty()) {
                    Random generator = new Random();
                    int randomIndex = generator.nextInt(recipes.size());
                    Recipe randomRecipe = recipes.get(randomIndex);
                    String meal = randomRecipe.getTitle();
                    String mealIngredients = randomRecipe.getIngredients();
                    String mealInstructions = randomRecipe.getInstructions();

                    String responseMessage = String.format("You're currently in %s! The low for today is %s and the high for today is %s. I suggest you eat: \b%s\n\nIngredients:\n\n%s\n\nCooking Instructions:\n\n%s",
                            city, minTemp, maxTemp, meal, mealIngredients, mealInstructions);

                    return Response.ok(responseMessage).build();
                } else {
                    LOG.warn("No recipes found for meal type: " + mealType);
                    return Response.status(Response.Status.NOT_FOUND).entity("No recipes found for meal type: " + mealType).build();
                }
            } else {
                LOG.error("Failed to fetch data. Recipe Status code: " + httpRecipeResponse.statusCode() + " Weather Status code: " + httpWeatherResponse.statusCode());
                return Response.serverError().entity("Failed to fetch data. Recipe Status code: " + httpRecipeResponse.statusCode() + " Weather Status code: " + httpWeatherResponse.statusCode()).build();
            }

        } catch (IOException | InterruptedException e) {
            LOG.error("Error while fetching recipe data: " + e.getMessage(), e);
            return Response.serverError().entity("Error while fetching recipe data").build();
        }
    }
}
