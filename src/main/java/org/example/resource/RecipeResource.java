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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Random;

@Path("/recipes")
@Produces(MediaType.APPLICATION_JSON)
public class RecipeResource {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("API_KEY");

    private static final String RECIPE_URL = "https://api.api-ninjas.com/v1/recipe?query=";

    private static final Logger LOG = LoggerFactory.getLogger(WeatherResource.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient;

    public RecipeResource() {
        this.httpClient = HttpClient.newHttpClient();
    }

    @GET
    @Path("/{mealType}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecipe(@PathParam("mealType") String mealType) throws IOException, InterruptedException {
        URI uri = URI.create(RECIPE_URL + mealType);
        System.out.println(uri);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("X-API-KEY", API_KEY)
                .GET()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 200) {
            String responseBody = httpResponse.body();
            List<Recipe> recipes = objectMapper.readValue(responseBody, new TypeReference<>() {
            });

            Random generator = new Random();
            int randomIndex = generator.nextInt(recipes.size());
            Recipe randomRecipe = recipes.get(randomIndex);
            String jsonRecipe = objectMapper.writeValueAsString(randomRecipe);

            return Response.ok(jsonRecipe).build();
        } else {
            LOG.error("Failed to fetch recipe data. Status code: " + httpResponse.statusCode());
            return Response.serverError().build();
        }
    }

}
