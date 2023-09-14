package org.example.resource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import org.example.model.Recipe;
import org.example.model.Weather;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SuggestionResourceTest {
    @Mock
    private HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private Weather weatherMockResponse;
    private List<Recipe> recipeMockResponse;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        weatherMockResponse = mapper.readValue(getClass().getResource("/weather_example_response.json"), Weather.class);
        recipeMockResponse = Collections.singletonList(mapper.readValue(getClass().getResource("/recipe_example_response.json"), Recipe.class));
    }

    @Test
    public void testGetRecipeSuggestion() throws IOException, InterruptedException {
        HttpResponse weatherResponse = mock(HttpResponse.class);
        HttpResponse recipeResponse = mock(HttpResponse.class);

        when(weatherResponse.statusCode()).thenReturn(200);
        when(recipeResponse.statusCode()).thenReturn(200);

        String weatherString = mapper.writeValueAsString(weatherMockResponse);
        String recipeString = mapper.writeValueAsString(recipeMockResponse);

        when(weatherResponse.body()).thenReturn(weatherString);
        when(recipeResponse.body()).thenReturn(recipeString);

        SuggestionResource suggestionResource = new SuggestionResource();

        when(httpClient.send(any(), any())).thenReturn(weatherResponse, recipeResponse);

        Response suggestionResponse = suggestionResource.getRecipe("oslo");

        Assertions.assertNotNull(suggestionResponse);
        Assertions.assertEquals(200, suggestionResponse.getStatus());
    }
}

