package org.example.resource;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.model.Weather;
import org.example.util.HttpClientSingleton;
import org.example.util.ObjectMapperSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Path("/weather")
@Produces(MediaType.APPLICATION_JSON)
public class WeatherResource {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String WEATHER_URL = dotenv.get("WEATHER_URL");
    private static final String API_KEY = dotenv.get("API_KEY");
    private static final Logger LOG = LoggerFactory.getLogger(WeatherResource.class);

    public HttpRequest getWeatherUrl(String city) {
        URI uri = URI.create(WEATHER_URL + city);
        System.out.println(uri);
        return HttpRequest.newBuilder()
                .uri(uri)
                .header("X-API-KEY", API_KEY)
                .GET()
                .build();
    }

    @GET
    @Path("/{city}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWeatherForecast(@PathParam("city") String city) {
        try {
            HttpRequest request = getWeatherUrl(city);
            HttpResponse<String> httpResponse = HttpClientSingleton.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() == 200) {
                String responseBody = httpResponse.body();
                Weather weather = ObjectMapperSingleton.mapper.readValue(responseBody, Weather.class);
                int currentTemp = weather.getTemp();
                int minTemp = weather.getMinTemp();
                int maxTemp = weather.getMaxTemp();
                String responseMessage = String.format("You're currently in %s! The temperature is currently %s. The low for today is %s and the high for today is %s.", city, currentTemp, minTemp, maxTemp);

                return Response.ok(responseMessage).build();
            } else {
                LOG.error("Failed to fetch weather data. Status code: " + httpResponse.statusCode());
                return Response.serverError().build();
            }
        } catch (IOException | InterruptedException e) {
            LOG.error("Error while fetching weather data: " + e.getMessage(), e);
            return Response.serverError().entity("Error while fetching weather data").build();
        }
    }
}
