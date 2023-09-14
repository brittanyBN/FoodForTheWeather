package org.example.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class WeatherResourceTest {

    @Mock
    private final Dotenv dotenv = Dotenv.load();
    private final String API_KEY = dotenv.get("API_KEY");
    private final String WEATHER_URL = dotenv.get("WEATHER_URL");
    private final String city = "oslo";

    private final HttpClient client = HttpClient.newBuilder().build();

    @Test
    void getWeatherResponseSuccess() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(WEATHER_URL + city))
                .header("X-API-KEY", API_KEY)
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void getWeatherResponseFail() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(WEATHER_URL + city))
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(400);
    }

}

