package org.example.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.model.Weather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Path("/weather")
@Produces(MediaType.APPLICATION_JSON)
public class WeatherResource {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String WEATHER_URL = "https://api.api-ninjas.com/v1/weather?city=";
    private static final String API_KEY = dotenv.get("API_KEY");

    private static final Logger LOG = LoggerFactory.getLogger(WeatherResource.class);
    private final HttpClient httpClient;
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public WeatherResource() {
        this.httpClient = HttpClient.newHttpClient();
    }

    @GET
    @Path("/{city}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWeatherForecast(@PathParam("city") String city) throws IOException, InterruptedException {
        URI uri = URI.create(WEATHER_URL + city);
        System.out.println(uri);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("X-API-KEY", API_KEY)
                .GET()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 200) {
            String responseBody = httpResponse.body();
            Weather weather = objectMapper.readValue(responseBody, Weather.class);
            int minTemp = weather.getMinTemp();
            int maxTemp = weather.getMaxTemp();

            return Response.ok("You're currently in " + city + "! The low for today is " + minTemp + " and the high for today is " + maxTemp + ". Lets find something enjoyable to eat in this weather..").build();
        } else {
            LOG.error("Failed to fetch weather data. Status code: " + httpResponse.statusCode());
            return Response.serverError().build();
        }
    }

}
