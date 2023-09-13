package org.example.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.model.Recipe;
import org.example.model.Weather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Random;

@Path("/suggestions")
@Produces(MediaType.APPLICATION_JSON)
public class SuggestionResource {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("API_KEY");

    private static final String RECIPE_URL = dotenv.get("RECIPE_URL");
    private static final String WEATHER_URL = dotenv.get("WEATHER_URL");

    private static final Logger LOG = LoggerFactory.getLogger(RecipeResource.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient;

    public SuggestionResource() {
        this.httpClient = HttpClient.newHttpClient();
    }

    @GET
    @Path("/{city}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecipe(@PathParam("city") String city) {
        try {
            URI weatherUri = URI.create(WEATHER_URL + city);
            System.out.println(weatherUri);
            HttpRequest weatherRequest = HttpRequest.newBuilder()
                    .uri(weatherUri)
                    .header("X-API-KEY", API_KEY)
                    .GET()
                    .build();

            HttpResponse<String> httpWeatherResponse = httpClient.send(weatherRequest, HttpResponse.BodyHandlers.ofString());

            String responseBody = httpWeatherResponse.body();
            Weather weather = objectMapper.readValue(responseBody, Weather.class);
            int minTemp = weather.getMinTemp();
            int maxTemp = weather.getMaxTemp();
            FoodForWeather foodForWeather = new FoodForWeather();
            String mealType = foodForWeather.getMealType(minTemp, maxTemp);
            URI recipeUri = URI.create(RECIPE_URL + mealType);
            System.out.println(recipeUri);
            HttpRequest recipeRequest = HttpRequest.newBuilder()
                    .uri(recipeUri)
                    .header("X-API-KEY", API_KEY)
                    .GET()
                    .build();

            HttpResponse<String> httpRecipeResponse = httpClient.send(recipeRequest, HttpResponse.BodyHandlers.ofString());

            if (httpRecipeResponse.statusCode() == 200 && httpWeatherResponse.statusCode() == 200) {
                String recipeResponseBody = httpRecipeResponse.body();
                List<Recipe> recipes = objectMapper.readValue(recipeResponseBody, new TypeReference<>() {
                });

                if (!recipes.isEmpty()) {
                    Random generator = new Random();
                    int randomIndex = generator.nextInt(recipes.size());
                    Recipe randomRecipe = recipes.get(randomIndex);
//                    String jsonRecipe = objectMapper.writeValueAsString(randomRecipe);
                    String meal = randomRecipe.getTitle();
                    String mealIngredients = randomRecipe.getIngredients();
                    String mealInstructions = randomRecipe.getInstructions();

                    return Response.ok("You're currently in " + city + "! The low for today is " + minTemp + " and the high for today is " + maxTemp + ". I suggest you eat: \b" + meal + "\n\n" + "Ingredients: \n\n" + mealIngredients + "\n\n" + "Cooking Instructions: \n\n" + mealInstructions).build();                } else {
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
