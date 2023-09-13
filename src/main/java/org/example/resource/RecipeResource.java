package org.example.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.model.Recipe;
import org.example.util.HttpClientSingleton;
import org.example.util.ObjectMapperSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Path("/recipes")
@Produces(MediaType.APPLICATION_JSON)
public class RecipeResource {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("API_KEY");
    private static final String RECIPE_URL = dotenv.get("RECIPE_URL");
    private static final Logger LOG = LoggerFactory.getLogger(RecipeResource.class);

    public HttpRequest getRecipeUrl(String mealType) {
        URI uri = URI.create(RECIPE_URL + mealType);
        System.out.println(uri);

        return HttpRequest.newBuilder()
                .uri(uri)
                .header("X-API-KEY", API_KEY)
                .GET()
                .build();
    }

    @GET
    @Path("random/{mealType}")
    public Response getRecipe(@PathParam("mealType") String mealType) {
        try {
            HttpRequest request = getRecipeUrl(mealType);
            HttpResponse<String> httpResponse = HttpClientSingleton.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() == 200) {
                String responseBody = httpResponse.body();
                List<Recipe> recipes = ObjectMapperSingleton.mapper.readValue(responseBody, new TypeReference<>() {});
                List<Recipe> result = recipes.stream().limit(5).toList();

                StringBuilder responseMessage = new StringBuilder("Here are five random " + mealType + " suggestions:\n\n");
                for (int i = 0; i < result.size(); i++) {
                    responseMessage.append("Recipe ").append(i + 1).append(":\n\n")
                            .append("Name: ").append(result.get(i).getTitle()).append("\n")
                            .append("Ingredients: ").append(result.get(i).getIngredients()).append("\n")
                            .append("Instructions: ").append(result.get(i).getInstructions()).append("\n")
                            .append("\n");
                }

                return Response.ok(responseMessage.toString()).build();
            } else {
                LOG.error("Failed to fetch recipe data. Status code: " + httpResponse.statusCode());
                return Response.status(httpResponse.statusCode())
                        .entity("Failed to fetch recipe data. Status code: " + httpResponse.statusCode())
                        .build();
            }

        } catch (IOException | InterruptedException e) {
            LOG.error("Error while fetching recipe data: " + e.getMessage(), e);
            return Response.serverError()
                    .entity("Error while fetching recipe data: " + e.getMessage())
                    .build();
        }
    }
}